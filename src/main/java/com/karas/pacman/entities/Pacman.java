package com.karas.pacman.entities;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
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
        _tunnelCooldown = 0.0;
        _nextDirection = Direction.RIGHT;

        _BaseSprite = getSprite();
        _DeathSprite = new Sprite(deathSprite, 0, 8);
        _DeathSound = deathSound;
        enterState(Entity.State.PREY);
    }

    public void setNextDirection(Direction d) {
        if (d != null)
            _nextDirection = d;
    }

    @Override
    public void enterState(Entity.State nextState) {
        switch (nextState) {
            case DEAD:
                setSprite(_DeathSprite);
                _DeathSound.play();
                break;
        
            case PREY, HUNTER:
                _BaseSprite.setOffset(getDirection().ordinal() * 2);
                setSprite(_BaseSprite);
                break;
        }
        setState(nextState);
    }

    @Override
    public void update(double deltaTime) {
        if (getState() == Entity.State.DEAD) {
            if (!getSprite().isAnimationEnded())
                getSprite().update(deltaTime);
            return;
        }

        // PREY | HUNTER
        if (!isIdle()) {
            setDirection(_nextDirection);
            move(deltaTime);
            getSprite().setOffset(getDirection().ordinal() * 2);
        }
        getSprite().update(deltaTime);
    }

    @Override
    public void reset() {
        setPosition(Map.toPixelVector2(Configs.Grid.PACMAN_POS));
        setDirection(Direction.RIGHT);
        _nextDirection = Direction.RIGHT;
        _tunnelCooldown = 0.0;
        enterState(Entity.State.PREY);
    }

    @Override
    protected void move(double deltaTime) {
        super.move(deltaTime);
        if (_tunnelCooldown > 0.0) {
            _tunnelCooldown -= deltaTime;
            return;
        }
        Vector2 tunnel = getTunnelExit();
        if (tunnel != null) {
            setPosition(tunnel);
            _tunnelCooldown = Configs.Time.TUNNEL_COOLDOWN;
        }
    }

    private final Sprite _BaseSprite, _DeathSprite;
    private final Sound _DeathSound;
    private double _tunnelCooldown;
    private volatile Direction _nextDirection;

}
