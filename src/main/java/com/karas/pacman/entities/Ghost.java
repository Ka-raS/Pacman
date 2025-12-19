package com.karas.pacman.entities;

import java.util.EnumSet;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.graphics.EntitySprite;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.Sound;
import com.karas.pacman.resources.SpriteID;

public abstract class Ghost extends Entity {

    public void enableFlashing() {
        if (getState() == State.PREY)
            getSprite().setFrameCount(4);
    }

    @Override
    public void update(double deltaTime) {
        switch (getState()) {
            case DEAD:
                Vector2 delta = getPosition().sub(HOME_POSITION).abs();
                if (delta.ix() <= 1.0 && delta.iy() <= 1.0)
                    enterState(State.HUNTER);
            
            default:
                updateDirection();
                move(deltaTime);

            case IDLE:
                getSprite().update(deltaTime);
        }
    }

    @Override
    public void reset() {
        setGridPosition(_startGridPosition);
        _prevGridPos = null;
        setDirection(_startDirection);
        _DeathSound.reset();
        enterState(State.HUNTER);
    }

    protected Ghost(Vector2 gridPosition, Direction direction, int speed, SpriteID baseSprite,
                    ImmutableEntity PacmanRef, ImmutableMap MapRef, ResourcesManager ResourcesMgr) {
        super(
            gridPosition, direction, speed,
            new EntitySprite(ResourcesMgr.getSprite(baseSprite), direction.ordinal() * 2, 2),
            MapRef
        );
        _prevGridPos = null;
        _baseSpeed = speed;
        _startGridPosition = gridPosition;
        _startDirection = direction;

        _baseSprite = getSprite();
        _preySprite = new EntitySprite(ResourcesMgr.getSprite(SpriteID.PREY_GHOST), 0, 2);
        _deathSprite = new EntitySprite(ResourcesMgr.getSprite(SpriteID.DEAD_GHOST), direction.ordinal(), 1);

        _DeathSound = ResourcesMgr.getSound(ResourceID.GHOST_DEATH_SOUND);
        _Pacman = PacmanRef;

        enterState(State.HUNTER);
    }

    protected abstract Vector2 findHunterTarget(ImmutableEntity PacmanRef);

    @Override
    protected void handleStateTransition(State nextState) {
        switch (nextState) {
            case HUNTER:
                setSpeed(_baseSpeed);
                _baseSprite.setOffset(getDirection().ordinal() * 2);
                setSprite(_baseSprite);
                break;

            case PREY:
                setSpeed(Configs.PX.GHOST_PREY_SPEED);
                _preySprite.setFrameCount(2);
                setSprite(_preySprite);
                break;

            case DEAD:
                setSpeed(Configs.PX.GHOST_DEAD_SPEED);
                _deathSprite.setOffset(getDirection().ordinal());
                setSprite(_deathSprite);
                _DeathSound.play();
                break;

            case IDLE:
                _DeathSound.pause();
                break;
        }
    }


    private void updateDirection() {
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

        switch (getState()) {
            case HUNTER -> {
                Vector2 target = findHunterTarget(_Pacman);
                Direction nextDir = currGridPos.closestTo(target, validDirections);
                setDirection(nextDir);
                getSprite().setOffset(nextDir.ordinal() * 2);
            }

            case PREY -> {
                Vector2 threat = _Pacman.getGridPosition();
                Direction nextDir = currGridPos.furthestFrom(threat, validDirections);
                setDirection(nextDir);
            }
                
            case DEAD -> {
                Direction nextDir = currGridPos.closestTo(Configs.Grid.GHOST_HOME, validDirections);
                setDirection(nextDir);
                getSprite().setOffset(nextDir.ordinal());
            }
            
            case IDLE -> {}
        }
    }

    private static final Vector2 HOME_POSITION = Map.toPixelVector2(Configs.Grid.GHOST_HOME);

    private final Sound _DeathSound;
    private final ImmutableEntity _Pacman;
    
    private final int _baseSpeed;
    private final Vector2 _startGridPosition;
    private final Direction _startDirection;
    private final EntitySprite _baseSprite, _preySprite, _deathSprite;

    private Vector2 _prevGridPos;

}
