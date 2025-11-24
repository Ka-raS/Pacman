package com.karas.pacman.entities;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.Sound;
import com.karas.pacman.resources.Sprite;

public abstract class Ghost extends Entity {

    public void enableFlashing() {
        if (getState() == State.PREY)
            getSprite().setFrameCount(4);
    }

    @Override
    public void update(double deltaTime) {
        switch (getState()) {
            case DEAD:
                if (isAtHomePosition())
                    enterState(State.HUNTER);
            
            default:
                updateDirection();
                move(deltaTime);

            case IDLE:
                getSprite().update(deltaTime);
                break;
        }
    }


    protected Ghost(Vector2 gridPosition, Direction direction, int speed, 
                    BufferedImage[] baseSprite, BufferedImage[] preySprite, BufferedImage[] deathSprite, Sound deathSound,
                    ImmutableEntity pacman, ImmutableMap map) {
        super(
            gridPosition, direction, speed,
            new Sprite(baseSprite, direction.ordinal() * 2, 2),
            map
        );
        _prevGridPos = null;
        _BaseSpeed = speed;
        _BaseSprite = getSprite();
        _PreySprite = new Sprite(preySprite, 0, 2);
        _DeathSprite = new Sprite(deathSprite, direction.ordinal(), 1);
        _DeathSound = deathSound;
        _Pacman = pacman;

        enterState(State.HUNTER);
    }

    protected abstract Vector2 findHunterTarget(ImmutableEntity pacman);

    @Override
    protected void handleStateTransition(State nextState) {
        switch (nextState) {
            case HUNTER:
                setSpeed(_BaseSpeed);
                _BaseSprite.setOffset(getDirection().ordinal() * 2);
                setSprite(_BaseSprite);
                break;

            case PREY:
                setSpeed(Configs.PX.GHOST_PREY_SPEED);
                _PreySprite.setFrameCount(2);
                setSprite(_PreySprite);
                break;

            case DEAD:
                setSpeed(Configs.PX.GHOST_DEAD_SPEED);
                _DeathSprite.setOffset(getDirection().ordinal());
                setSprite(_DeathSprite);
                _DeathSound.play();
                break;

            case IDLE:
                break;
        }
    }


    private void updateDirection() {
        Vector2 currGridPos = getGridPosition();
        if ((!isCenteredInTile() || currGridPos.equals(_prevGridPos)) && _prevGridPos != null)
            return;
        _prevGridPos = currGridPos;

        EnumSet<Direction> validDirections = getValidDirections();
        if (validDirections.isEmpty())
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

    private EnumSet<Direction> getValidDirections() {
        EnumSet<Direction> result = EnumSet.noneOf(Direction.class);
        for (Direction d : Direction.values())
            if (isValidDirection(d))
                result.add(d);

        if (result.size() > 1)
            result.remove(getDirection().opposite());
        return result;
    }

    private boolean isAtHomePosition() {
        Vector2 p = getPosition().sub(HOME_POSITION).abs();
        return p.ix() <= 1.0 && p.iy() <= 1.0;
    }

    private static final Vector2 HOME_POSITION = Map.toPixelVector2(Configs.Grid.GHOST_HOME);

    private final int _BaseSpeed;
    private final Sprite _BaseSprite, _PreySprite, _DeathSprite;
    private final Sound _DeathSound;
    private final ImmutableEntity _Pacman;
    private Vector2 _prevGridPos;

}
