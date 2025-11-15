package com.karas.pacman.entities;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
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
        _nextDirection = Direction.RIGHT;
        enterState(Entity.State.PREY);
    }

    public void setNextDirection(Direction d) {
        _nextDirection = d;
    }

    @Override
    public void enterState(Entity.State nextState) {
        if (getState() == Entity.State.DEAD)
            return;

        if (nextState != Entity.State.DEAD)
            setSpritesOffset(getDirection().ordinal() * 2);
        else
            setSprites(new Sprites(SpriteSheet.DEAD_PACMAN, 0, 8));

        setState(nextState);
    }

    @Override
    public void update(double deltaTime) {
        if (!isIdle() && getState() != Entity.State.DEAD) {
            setDirection(_nextDirection);
            setSpritesOffset(getDirection().ordinal() * 2);
            move(deltaTime);
        }
        updateSprites(deltaTime);
    }


    private volatile Direction _nextDirection;

}
