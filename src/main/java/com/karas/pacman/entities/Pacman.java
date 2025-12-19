package com.karas.pacman.entities;

import com.karas.pacman.Configs;
import com.karas.pacman.audio.Sound;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.graphics.EntitySprite;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.SpriteID;

public class Pacman extends Entity {

    public Pacman(ImmutableMap MapRef, ResourcesManager ResourcesMgr) {
        super(
            Configs.Grid.PACMAN_POSITION,
            Direction.RIGHT,
            Configs.PX.PACMAN_SPEED,
            new EntitySprite(ResourcesMgr.getSprite(SpriteID.PACMAN), Direction.RIGHT.ordinal() * 2, 2),
            MapRef
        );
        _nextDirection = Direction.RIGHT;
        _baseSprite = getSprite();
        _deathSprite = new EntitySprite(ResourcesMgr.getSprite(SpriteID.DEAD_PACMAN), 0, 8);
        _DeathSound = ResourcesMgr.getSound(ResourceID.PACMAN_DEATH_SOUND);
        enterState(State.PREY);
    }

    public void setNextDirection(Direction d) {
        if (d != null)
            _nextDirection = d;
    }

    @Override
    public void update(double deltaTime) {
        switch (getState()) {
            case PREY, HUNTER:
                if (canMoveInDirection(_nextDirection))
                    setDirection(_nextDirection);
                move(deltaTime);
                getSprite().setOffset(getDirection().ordinal() * 2);

            case IDLE:
                getSprite().update(deltaTime);
                break;
        
            case DEAD:
                if (!getSprite().isAnimationEnded())
                    getSprite().update(deltaTime);
        }
    }

    @Override
    public void reset() {
        setGridPosition(Configs.Grid.PACMAN_POSITION);
        setDirection(Direction.RIGHT);
        _nextDirection = Direction.RIGHT;
        setSprite(_baseSprite);

        if (_deathSprite.isAnimationEnded())
            _deathSprite.update(Configs.Time.SPRITE_INTERVAL);
        _DeathSound.reset();
        enterState(State.PREY);
    }


    @Override
    protected void handleStateTransition(State nextState) {
        switch (nextState) {
            case IDLE:
                _DeathSound.pause();
                break;

            case DEAD:
                _deathSprite.setOffset(0);
                setSprite(_deathSprite);
                _DeathSound.play();
                break;
            
            case PREY:
                setSpeed(Configs.PX.PACMAN_SPEED);
                getSprite().setOffset(getDirection().ordinal() * 2);
                break;

            case HUNTER:
                setSpeed(Configs.PX.PACMAN_HUNTER_SPEED);
                getSprite().setOffset(getDirection().ordinal() * 2);
                break;
        }
    }


    private final Sound _DeathSound;
    
    private final EntitySprite _baseSprite, _deathSprite;

    private volatile Direction _nextDirection;

}
