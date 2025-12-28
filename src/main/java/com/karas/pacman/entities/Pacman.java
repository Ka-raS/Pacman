package com.karas.pacman.entities;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourceManager;
import com.karas.pacman.resources.Sound;
import com.karas.pacman.resources.SpriteID;

public final class Pacman extends Entity {

    public Pacman(ImmutableMap MapRef, ResourceManager ResourceMgr) {
        super(
            Constants.Grid.PACMAN_POSITION,
            Direction.RIGHT,
            Constants.Pixel.PACMAN_SPEED,
            MapRef
        );
        _nextDirection = Direction.RIGHT;
        _baseSprite = new Sprite(ResourceMgr.getSprite(SpriteID.PACMAN), Direction.RIGHT);
        _deathSprite = new Sprite(ResourceMgr.getSprite(SpriteID.DEAD_PACMAN));
        _DeathSound = ResourceMgr.getSound(ResourceID.PACMAN_DEATH_SOUND);
        setSprite(_baseSprite);
        enterState(State.PREY);
    }

    public void setNextDirection(Direction direction) {
        _nextDirection = direction;
    }

    @Override
    public void update(double deltaTime) {
        switch (getState()) {
            case PREY, HUNTER:
                if (isValidNextDirection())
                    setDirection(_nextDirection);
                updatePosition(deltaTime);

            case IDLE:
                updateSprite(deltaTime);
                break;
            
            case DEAD:
                if (!_deathSprite.isAnimationEnded())
                    updateSprite(deltaTime);
        }
    }

    @Override
    public void reset() {
        setGridPosition(Constants.Grid.PACMAN_POSITION);
        setDirection(Direction.RIGHT);
        setSprite(_baseSprite);
        _nextDirection = Direction.RIGHT;
        _deathSprite.reset();
        _DeathSound.reset();
        enterState(State.PREY);
    }


    @Override
    protected void handleStateTransition(State nextState) {
        switch (nextState) {
            case IDLE   -> _DeathSound.pause();
            case PREY   -> setSpeed(Constants.Pixel.PACMAN_SPEED);
            case HUNTER -> setSpeed(Constants.Pixel.PACMAN_HUNTER_SPEED);
            case DEAD   -> {
                setSprite(_deathSprite);
                _DeathSound.play();
            }
        }
    }


    private boolean isValidNextDirection() {
        if (getDirection().isSameAxis(_nextDirection))
            return true;
        if (!isCenteredInTile())
            return false;
        return isValidDirection(_nextDirection);
    }

    private final Sound _DeathSound;
    
    private final Sprite _baseSprite, _deathSprite;
    private volatile Direction _nextDirection;

}
