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
                Vector2 delta = getGridPosition().sub(Constants.Grid.GHOST_HOME);
                if (delta.ix() == 0 && delta.iy() == 0)
                    enterState(State.HUNTER);
            
            default:
                updateDirection();
                move(deltaTime);

            case IDLE:
                updateSprite(deltaTime);
        }
    }

    @Override
    public final void reset() {
        setGridPosition(_startGridPosition);
        setDirection(_startDirection);
        _prevGridPos = null;
        _DeathSound.reset();
        enterState(State.HUNTER);
    }

    protected Ghost(Vector2 gridPosition, Direction direction, int speed, SpriteID baseSpriteID,
                    ImmutableEntity PacmanRef, ImmutableMap MapRef, ResourcesManager ResourcesMgr) {
        super(gridPosition, direction, speed, MapRef);
        _prevGridPos = null;
        _baseSpeed = speed;
        _startGridPosition = gridPosition;
        _startDirection = direction;
        _DeathSound = ResourcesMgr.getSound(ResourceID.GHOST_DEATH_SOUND);
        _Pacman = PacmanRef;

        _baseSprite  = new Sprite(ResourcesMgr.getSprite(baseSpriteID), direction);
        _preySprite  = new Sprite(ResourcesMgr.getSprite(SpriteID.PREY_GHOST));
        _flashSprite = new Sprite(ResourcesMgr.getSprite(SpriteID.FLASH_GHOST));
        _deathSprite = new Sprite(ResourcesMgr.getSprite(SpriteID.DEAD_GHOST), direction);
        enterState(State.HUNTER);
    }

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


    private final void updateDirection() {
        Vector2 currGridPos = getGridPosition();
        if ((!isCenteredInTile() || currGridPos.equals(_prevGridPos)) && _prevGridPos != null)
            return;
        _prevGridPos = currGridPos;

        EnumSet<Direction> validDirections = EnumSet.noneOf(Direction.class);
        for (Direction d : Direction.values())
            if (canMoveInDirection(d))
                validDirections.add(d);

        if (validDirections.size() > 1)
            validDirections.remove(getDirection().opposite());
        else if (validDirections.isEmpty())
            return;

        Direction nextDirection = switch (getState()) {
            case HUNTER -> currGridPos.closestTo(findHunterTarget(_Pacman), validDirections);
            case DEAD   -> currGridPos.closestTo(Constants.Grid.GHOST_HOME, validDirections);
            case PREY   -> currGridPos.furthestFrom(_Pacman.getGridPosition(), validDirections);                
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
    private Vector2 _prevGridPos;

}
