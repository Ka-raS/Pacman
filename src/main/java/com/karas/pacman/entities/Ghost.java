package com.karas.pacman.entities;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

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
        State state = getState();
        if (state == State.IDLE) {
            getSprite().update(deltaTime);
            return;   
        }

        updateDirection();
        move(deltaTime);
        getSprite().update(deltaTime);

        if (state == State.DEAD && getGridPos().equals(_HomeGridPos)) // a bit ugly, just a bit
            enterState(State.HUNTER);
    }
  
    @Override
    public void reset() {
        setPosition(Map.toPixelVector2(_HomeGridPos));
        setDirection(_HomeDirection);
        enterState(State.HUNTER);
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
        _HomeGridPos = gridPos;
        _HomeDirection = direction;
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
                _BaseSprite.setOffset(getDirection().ordinal() * 2);
                setSprite(_BaseSprite);
                break;

            case PREY:
                _PreySprite.setFrameCount(2);
                setSprite(_PreySprite);
                break;

            case DEAD:
                _DeathSprite.setOffset(getDirection().ordinal());
                setSprite(_DeathSprite);
                _DeathSound.play();
                break;

            case IDLE:
                break;
        }
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
            
            case IDLE:
                break;
        }
        setDirection(nextDir);
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

    private final Vector2 _HomeGridPos;
    private final Direction _HomeDirection;
    private final Sprite _BaseSprite, _PreySprite, _DeathSprite;
    private final Sound _DeathSound;
    private final ImmutableEntity _Pacman;
    private Vector2 _prevGridPos;

}
