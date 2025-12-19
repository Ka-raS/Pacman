package com.karas.pacman.entities;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.Sound;
import com.karas.pacman.resources.SpriteID;

public class Pacman extends Entity {

    public Pacman(ImmutableMap MapRef, ResourcesManager ResourcesMgr) {
        super(
            Configs.Grid.PACMAN_POSITION,
            Direction.RIGHT,
            Configs.PX.PACMAN_SPEED,
            MapRef
        );
        _nextDirection = Direction.RIGHT;
        _baseSprite = new Sprite(ResourcesMgr.getSprite(SpriteID.PACMAN), Direction.RIGHT);
        _deathSprite = new Sprite(ResourcesMgr.getSprite(SpriteID.DEAD_PACMAN));
        _DeathSound = ResourcesMgr.getSound(ResourceID.PACMAN_DEATH_SOUND);
        setSprite(_baseSprite);
        enterState(State.PREY);
    }

    public void setNextDirection(Direction d) {
        _nextDirection = d;
    }

    @Override
    public void update(double deltaTime) {
        switch (getState()) {
            case PREY, HUNTER:
                if (canMoveInDirection(_nextDirection))
                    setDirection(_nextDirection);
                move(deltaTime);

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
        setGridPosition(Configs.Grid.PACMAN_POSITION);
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
            case PREY   -> setSpeed(Configs.PX.PACMAN_SPEED);
            case HUNTER -> setSpeed(Configs.PX.PACMAN_HUNTER_SPEED);
            case DEAD   -> {
                setSprite(_deathSprite);
                _DeathSound.play();
            }
        }
    }


    private final Sound _DeathSound;
    
    private final Sprite _baseSprite, _deathSprite;
    private volatile Direction _nextDirection;

}
