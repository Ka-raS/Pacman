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
        if (isIdle()) {
            updateSprites(deltaTime);
            return;
        }

        // update direction and stuffs
        boolean earlyReturn = switch (getState()) {
            case HUNTER -> updateHunter();
            case PREY   -> updatePrey();
            case DEAD   -> updateDead();
        };
        if (earlyReturn)
            return;

        move(deltaTime);
        updateSprites(deltaTime);
    }
  

    protected Ghost(Vector2 homeGridPos, Direction direction, double speed, SpriteSheet spriteName, ImmutableEntity pacman, ImmutableMap map) {
        super(
            Map.toPixelVector2(homeGridPos),
            direction,
            speed,
            null, // set in enterStateInternal()
            map
        );
        _HomeGridPos = homeGridPos;
        _SpriteName = spriteName;
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
                setSprites(new Sprites(_SpriteName, getDirection().ordinal() * 2, 2));
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

    private boolean updateHunter() {
        final boolean RETURN = false;
        _caughtPacman |= collidesWith(_Pacman);
        
        if (!isCenteredInTile())
            return RETURN;

        Vector2 currGridPos = getGridPos();
        if (currGridPos.equals(_prevGridPos))
            return RETURN;
        _prevGridPos = currGridPos;

        EnumSet<Direction> validDirections = getValidDirections();
        if (!validDirections.isEmpty()) {
            Vector2 target = findHunterTarget(_Pacman);
            Direction nextDir = currGridPos.closestTo(target, validDirections);
            setDirection(nextDir);
        }
        setSpritesOffset(getDirection().ordinal() * 2);
        return RETURN;
    }

    private boolean updatePrey() {
        if (collidesWith(_Pacman)) {
            enterStateInternal(Entity.State.DEAD);
            return true;
        }
        final boolean RETURN = false;

        if (!isCenteredInTile())
            return RETURN;

        Vector2 currGridPos = getGridPos();
        if (currGridPos.equals(_prevGridPos))
            return RETURN;
        _prevGridPos = currGridPos;
        
        EnumSet<Direction> validDirections = getValidDirections();
        if (!validDirections.isEmpty()) {
            Direction nextDir = currGridPos.furthestFrom(_Pacman.getGridPos(), validDirections);
            setDirection(nextDir);
        }
        return RETURN;
    }

    private boolean updateDead() {
        if (!isCenteredInTile())
            return false;

        Vector2 currGridPos = getGridPos();
        if (currGridPos.equals(_HomeGridPos)) {
            enterStateInternal(Entity.State.HUNTER);
            return true;
        }
        final boolean RETURN = false;

        if (currGridPos.equals(_prevGridPos))
            return RETURN;
        _prevGridPos = currGridPos;
        
        EnumSet<Direction> validDirections = getValidDirections();
        if (!validDirections.isEmpty()) {
            Direction nextDir = currGridPos.closestTo(_HomeGridPos, validDirections);
            setDirection(nextDir);
        }
        setSpritesOffset(getDirection().ordinal());
        return RETURN;
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
    private final SpriteSheet _SpriteName;
    private final ImmutableEntity _Pacman;
    
    private Vector2 _prevGridPos;
    private boolean _caughtPacman;

}
