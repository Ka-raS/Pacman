package com.karas.pacman.entities;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.logging.Logger;

import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.Sound;
import com.karas.pacman.resources.Sprite;

public abstract class Ghost extends Entity {

    public boolean hasCaughtPacman() {
        return getState() == Entity.State.HUNTER && _caughtPacman;
    }

    public void enableFlashing() {
        if (getState() == Entity.State.PREY)
            getSprite().setFrameCount(4);
    }

    @Override
    public void enterState(Entity.State nextState) {
        if ((getState() == Entity.State.HUNTER && nextState == Entity.State.PREY)
         || (getState() == Entity.State.PREY   && nextState != Entity.State.DEAD))
            enterStateInternal(nextState);
    }

    @Override
    public void update(double deltaTime) {
        if (!isIdle()) {
            updateDirection();
            move(deltaTime);
            handleCollision();
        }
        getSprite().update(deltaTime);
    }
  
    @Override
    public void reset() {
        setPosition(Map.toPixelVector2(_HomeGridPos));
        setDirection(_HomeDirection);
        enterStateInternal(Entity.State.HUNTER);
    }

    
    protected Ghost(Vector2 gridPos, Direction direction, int speed, 
                    BufferedImage[] baseSprite, BufferedImage[] preySprite, BufferedImage[] deathSprite, Sound deathSound,
                    ImmutableEntity pacman, ImmutableMap map) {
        super(
            Map.toPixelVector2(gridPos),
            direction, speed,
            new Sprite(baseSprite, direction.ordinal() * 2, 2),
            map
        );
        _prevGridPos = null;
        _caughtPacman = false;

        _HomeGridPos = gridPos;
        _HomeDirection = direction;
        _BaseSprite = getSprite();
        _PreySprite = new Sprite(preySprite, 0, 2);
        _DeathSprite = new Sprite(deathSprite, direction.ordinal(), 1);
        _DeathSound = deathSound;
        _Pacman = pacman;

        enterStateInternal(Entity.State.HUNTER);
    }

    protected abstract Vector2 findHunterTarget(ImmutableEntity pacman);


    private void enterStateInternal(Entity.State nextState) {
        switch (nextState) {
            case HUNTER:
                _caughtPacman = false;
                _BaseSprite.setOffset(getDirection().ordinal() * 2);
                setSprite(_BaseSprite);
                break;

            case PREY:
                _PreySprite.setFrameCount(2);
                setSprite(_PreySprite);
                break;

            case DEAD:
                if (getState() == Entity.State.PREY)
                    _LOGGER.info(getClass().getSimpleName() + " eaten!");
                _DeathSprite.setOffset(getDirection().ordinal());
                setSprite(_DeathSprite);
                _DeathSound.play();
                break;
        }
        setState(nextState);
    }

    private void updateDirection() {
        Vector2 currGridPos = getGridPos();
        if (!isCenteredInTile() || currGridPos.equals(_prevGridPos))
            return;
        _prevGridPos = currGridPos;

        EnumSet<Direction> validDirections = getValidDirections();
        if (validDirections.isEmpty())
            return;

        Direction nextDir = getDirection();
        switch (getState()) {
            case HUNTER: 
                nextDir = currGridPos.closestTo(findHunterTarget(_Pacman), validDirections);
                getSprite().setOffset(nextDir.ordinal() * 2);
                break;

            case PREY:
                nextDir = currGridPos.furthestFrom(_Pacman.getGridPos(), validDirections);
                break;
                
            case DEAD:
                nextDir = currGridPos.closestTo(_HomeGridPos, validDirections);
                getSprite().setOffset(nextDir.ordinal());
                break;
        }
        setDirection(nextDir);
    }

    private void handleCollision() {
        Vector2 currGridPos = getGridPos();
        switch (getState()) {
            case HUNTER:
                _caughtPacman |= collidesWith(_Pacman);       
                break;
        
            case PREY:
                if (collidesWith(_Pacman))
                    enterStateInternal(Entity.State.DEAD);
                break;

            case DEAD:
                if (currGridPos.equals(_HomeGridPos))
                    enterStateInternal(Entity.State.HUNTER);
                break;
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

    private static final Logger _LOGGER = Logger.getLogger(Ghost.class.getName());

    private final Vector2 _HomeGridPos;
    private final Direction _HomeDirection;
    private final Sprite _BaseSprite, _PreySprite, _DeathSprite;
    private final Sound _DeathSound;
    private final ImmutableEntity _Pacman;
    private Vector2 _prevGridPos;
    private boolean _caughtPacman;

}
