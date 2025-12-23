package com.karas.pacman.entities;

import java.util.EnumSet;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.Sound;
import com.karas.pacman.resources.SpriteID;

public abstract class Ghost extends Entity {

    public final void startFlashing() {
        if (getState() == State.PREY)
            setSprite(_flashSprite);
    }

    @Override
    public final void update(double deltaTime) {
        switch (getState()) {
            case DEAD:
                if (getGridPosition().equals(Constants.Grid.GHOST_HOME))
                    enterState(State.HUNTER);
            
            default:
                updateDirection();
                updatePosition(deltaTime);

            case IDLE:
                updateSprite(deltaTime);
        }
    }

    @Override
    public final void reset() {
        setGridPosition(_startGridPosition);
        setDirection(_startDirection);
        _lastGridPosition = _startGridPosition;
        _DeathSound.reset();
        enterState(State.HUNTER);
    }

    protected Ghost(Vector2 gridPosition, Direction direction, int speed, SpriteID baseSpriteID,
                    ImmutableEntity PacmanRef, ImmutableMap MapRef, ResourcesManager ResourcesMgr) {
        super(gridPosition, direction, speed, MapRef);
        _baseSpeed = speed;
        _startGridPosition = _lastGridPosition = gridPosition;
        _startDirection = direction;
        _DeathSound = ResourcesMgr.getSound(ResourceID.GHOST_DEATH_SOUND);
        _Pacman = PacmanRef;
        
        _baseSprite  = new Sprite(ResourcesMgr.getSprite(baseSpriteID), direction);
        _preySprite  = new Sprite(ResourcesMgr.getSprite(SpriteID.PREY_GHOST));
        _flashSprite = new Sprite(ResourcesMgr.getSprite(SpriteID.FLASH_GHOST));
        _deathSprite = new Sprite(ResourcesMgr.getSprite(SpriteID.DEAD_GHOST), direction);
        enterState(State.HUNTER);
    }

    /** @return grid position */
    protected abstract Vector2 findHunterTarget(ImmutableEntity PacmanRef);

    @Override
    protected final void handleStateTransition(State nextState) {
        switch (nextState) {
            case HUNTER:
                setSpeed(_baseSpeed);
                setSprite(_baseSprite);
                break;

            case PREY:
                setSpeed(Constants.Pixel.GHOST_PREY_SPEED);
                setSprite(_preySprite);
                break;

            case DEAD:
                setSpeed(Constants.Pixel.GHOST_DEAD_SPEED);
                setSprite(_deathSprite);
                _DeathSound.play();
                break;

            case IDLE:
                _DeathSound.pause();
                break;
        }
    }

    @Override
    protected final boolean validMovement(Vector2 fromGrid, Vector2 toGrid) {
        return switch (tileAt(toGrid)) {
            case WALL -> false;
            case GATE -> getState() == State.DEAD || fromGrid.y() > toGrid.y();
            default   -> true;
        };
    }


    private final void updateDirection() {
        Vector2 currentGrid = getGridPosition();
        if ((!isCenteredInTile() || currentGrid == _lastGridPosition) && currentGrid != _startGridPosition)
            return;
        _lastGridPosition = currentGrid;

        EnumSet<Direction> validDirections = EnumSet.noneOf(Direction.class);
        for (Direction dir : Direction.values())
            if (validMovement(currentGrid, currentGrid.add(dir.toVector2())))
                validDirections.add(dir);

        if (validDirections.size() > 1)
            validDirections.remove(getDirection().opposite());
        if (validDirections.size() == 1) {
            setDirection(validDirections.iterator().next());
            return;
        }

        Direction nextDirection = switch (getState()) {
            case HUNTER -> currentGrid.closestTo(findHunterTarget(_Pacman), validDirections);
            case DEAD   -> currentGrid.closestTo(Constants.Grid.GHOST_HOME, validDirections);
            case PREY   -> currentGrid.furthestFrom(_Pacman.getGridPosition(), validDirections);                
            case IDLE   -> getDirection();
        };
        setDirection(nextDirection);
    }

    private final Sound _DeathSound;
    private final ImmutableEntity _Pacman;
    
    private final int _baseSpeed;
    private final Vector2 _startGridPosition;
    private final Direction _startDirection;
    private final Sprite _baseSprite, _preySprite, _flashSprite, _deathSprite;
    private Vector2 _lastGridPosition;

}
