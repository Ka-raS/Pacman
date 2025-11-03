package com.karas.pacman.entities;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

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
  

    protected Ghost(Vector2 gridPos, Direction direction, double speed, SpriteSheet spriteName, ImmutableEntity pacman, ImmutableMap map) {
        super(
            Map.toPixelVector2(gridPos),
            direction,
            speed,
            null, // set in enterStateInternal()
            map
        );
        _nextDirections = new ArrayDeque<>(32);
        _Pacman = pacman;
        enterStateInternal(Entity.State.HUNTER);
    }

    protected abstract Vector2 getHomeGridPos();
    
    protected abstract SpriteSheet getSpriteName();

    protected abstract Collection<Direction> findPathToPacman(Vector2 begin, Vector2 target);
    
    protected abstract Collection<Direction> findPathToRunaway(Vector2 begin, Vector2 target);

    protected abstract Collection<Direction> findPathToHome(Vector2 begin, Vector2 target);


    private void enterStateInternal(Entity.State nextState) {
        switch (nextState) {
            case HUNTER:
                _caughtPacman = false;
                setSprites(new Sprites(getSpriteName(), getDirection().ordinal() * 2, 2));
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
        _caughtPacman |= collidesWith(_Pacman);
        
        final boolean RETURN = false;
        if (!isCenteredInTile())
            return RETURN;

        Vector2 currGridPos = getGridPos();
        if (currGridPos.equals(_prevGridPos))
            return RETURN;

        _prevGridPos = currGridPos;
        if (_nextDirections.isEmpty())
            _nextDirections.addAll(findPathToPacman(currGridPos, _Pacman.getNearestMovableGridPos()));
        
        setDirection(_nextDirections.poll());
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
        if (_nextDirections.isEmpty())
            _nextDirections.addAll(findPathToRunaway(currGridPos, _Pacman.getNearestMovableGridPos()));
        setDirection(_nextDirections.poll());
        return RETURN;
    }

    private boolean updateDead() {
        if (!isCenteredInTile())
            return false;

        Vector2 currGridPos = getGridPos();
        if (currGridPos.equals(getHomeGridPos())) {
            enterStateInternal(Entity.State.HUNTER);
            return true;
        }

        final boolean RETURN = false;
        if (currGridPos.equals(_prevGridPos))
            return RETURN;
        
        _prevGridPos = currGridPos;
        if (_nextDirections.isEmpty())
            _nextDirections.addAll(findPathToHome(currGridPos, _Pacman.getNearestMovableGridPos()));
        setDirection(_nextDirections.poll());
        setSpritesOffset(getDirection().ordinal());
        return RETURN;
    }

    private final ImmutableEntity _Pacman;
    private Vector2 _prevGridPos;
    private Queue<Direction> _nextDirections;
    private boolean _caughtPacman;

}
