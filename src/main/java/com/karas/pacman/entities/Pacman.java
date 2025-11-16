package com.karas.pacman.entities;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.SpriteSheet;
import com.karas.pacman.resources.Sprites;

public class Pacman extends Entity {

    public Pacman(ImmutableMap map) {
        super(
            Map.toPixelVector2(Configs.Grid.PACMAN_POS),
            Direction.RIGHT,
            Configs.PX.PACMAN_SPEED,
            new Sprites(SpriteSheet.PACMAN, 0, 2),    
            map
        );
        _tunnelCooldown = 0.0;
        _nextDirection = Direction.RIGHT;
        enterState(Entity.State.PREY);
    }

    public void setNextDirection(Direction d) {
        if (_nextDirection != null)
            _nextDirection = d;
    }

    @Override
    public void enterState(Entity.State nextState) {
        if (getState() == Entity.State.DEAD)
            return;

        if (nextState == Entity.State.DEAD)
            setSprites(new Sprites(SpriteSheet.DEAD_PACMAN, 0, 8));

        setState(nextState);
    }

    @Override
    public void update(double deltaTime) {
        if (!isIdle() && getState() != Entity.State.DEAD) {
            setDirection(_nextDirection);
            move(deltaTime);
            setSpritesOffset(getDirection().ordinal() * 2);
        }
        updateSprites(deltaTime);
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
            _tunnelCooldown = Configs.TUNNEL_COOLDOWN;
        }
    }

    private double _tunnelCooldown;
    private volatile Direction _nextDirection;

}
