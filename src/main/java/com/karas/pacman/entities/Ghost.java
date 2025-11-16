package com.karas.pacman.entities;

import java.util.EnumSet;

import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.SpriteSheet;
import com.karas.pacman.resources.Sprites;

public abstract class Ghost extends Entity {

    public boolean hasCaughtPacman() {
        return getState() == Entity.State.HUNTER && _caughtPacman;
    }

    public void enableFlashing() {
        if (getState() == Entity.State.PREY)
            setSpritesFrameCount(4);
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
        updateSprites(deltaTime);
    }
  

    protected Ghost(Vector2 homeGridPos, Direction direction, double speed, SpriteSheet baseSprite, ImmutableEntity pacman, ImmutableMap map) {
        super(
            Map.toPixelVector2(homeGridPos),
            direction,
            speed,
            null, // set in enterStateInternal()
            map
        );
        _HomeGridPos = homeGridPos;
        _BaseSprite = baseSprite;
        _Pacman = pacman;
        _prevGridPos = null;
        _caughtPacman = false;
        enterStateInternal(Entity.State.HUNTER);
    }

    protected abstract Vector2 findHunterTarget(ImmutableEntity pacman);


    private void enterStateInternal(Entity.State nextState) {
        switch (nextState) {
            case HUNTER:
                _caughtPacman = false;
                setSprites(new Sprites(_BaseSprite, getDirection().ordinal() * 2, 2));
                break;

            case PREY:
                setSprites(new Sprites(SpriteSheet.PREY_GHOST, 0, 2));
                break;

            case DEAD:
                if (getState() == Entity.State.PREY)
                    System.out.println(getClass().getSimpleName() + " eaten!");
                setSprites(new Sprites(SpriteSheet.DEAD_GHOST, getDirection().ordinal(), 1));
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
                setSpritesOffset(getDirection().ordinal() * 2);
                break;

            case PREY:
                nextDir = currGridPos.furthestFrom(_Pacman.getGridPos(), validDirections);
                break;
                
            case DEAD:
                nextDir = currGridPos.closestTo(_HomeGridPos, validDirections);
                setSpritesOffset(getDirection().ordinal());
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
            if (validDirection(d))
                result.add(d);

        if (result.size() > 1)
            result.remove(getDirection().opposite());
        return result;
    }

    private final Vector2 _HomeGridPos;
    private final SpriteSheet _BaseSprite;
    private final ImmutableEntity _Pacman;
    
    private Vector2 _prevGridPos;
    private boolean _caughtPacman;

}
