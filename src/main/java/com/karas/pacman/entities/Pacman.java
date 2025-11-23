package com.karas.pacman.entities;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.Sound;
import com.karas.pacman.resources.Sprite;

public class Pacman extends Entity {

    public Pacman(ImmutableMap map, BufferedImage[] baseSprite, BufferedImage[] deathSprite, Sound deathSound) {
        super(
            Map.toPixelVector2(Configs.Grid.PACMAN_POS),
            Direction.RIGHT,
            Configs.PX.PACMAN_SPEED,
            new Sprite(baseSprite, Direction.RIGHT.ordinal() * 2, 2),
            map
        );
        _nextDirection = Direction.RIGHT;
        _BaseSprite = getSprite();
        _DeathSprite = new Sprite(deathSprite, 0, 8);
        _DeathSound = deathSound;
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
                setDirection(_nextDirection);
                move(deltaTime);
                getSprite().setOffset(getDirection().ordinal() * 2);

            case IDLE:
                getSprite().update(deltaTime);
                break;
        
            case DEAD:
                if (!getSprite().isAnimationEnded())
                    getSprite().update(deltaTime);
                break;
        }
    }

    @Override
    public void reset() {
        setPosition(Map.toPixelVector2(Configs.Grid.PACMAN_POS));
        setDirection(Direction.RIGHT);
        _nextDirection = Direction.RIGHT;
        _BaseSprite.setOffset(Direction.RIGHT.ordinal() * 2);
        setSprite(_BaseSprite);
        enterState(State.PREY);
    }


    @Override
    protected void handleStateTransition(State nextState) {
        switch (nextState) {
            case IDLE:
                break;

            case DEAD:
                setSprite(_DeathSprite);
                _DeathSound.play();
                break;
            
            case PREY:
                setSpeed(Configs.PX.PACMAN_SPEED);
                getSprite().setOffset(getDirection().ordinal() * 2);
                break;

            case HUNTER:
                setSpeed((int) (Configs.PX.PACMAN_SPEED * 1.25));
                getSprite().setOffset(getDirection().ordinal() * 2);
                break;
        }
    }


    private final Sprite _BaseSprite, _DeathSprite;
    private final Sound _DeathSound;
    private volatile Direction _nextDirection;

}
