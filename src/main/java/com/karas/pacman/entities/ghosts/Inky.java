package com.karas.pacman.entities.ghosts;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.audio.Sound;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;

public class Inky extends Ghost {
    
    public Inky(ImmutableEntity PacmanRef, ImmutableMap MapRef, ImmutableEntity BlinkyRef, 
                BufferedImage[] BaseImages, BufferedImage[] PreyImages, BufferedImage[] DeathImages, Sound DeathSound) {
        super(
            Configs.Grid.INKY_POSITION,
            Direction.DOWN,
            Configs.PX.INKY_SPEED,
            BaseImages, PreyImages, DeathImages, DeathSound,
            PacmanRef, MapRef
        );
        _Blinky = BlinkyRef;
    }

    @Override
    public void reset() {
        setGridPosition(Configs.Grid.INKY_POSITION);
        setDirection(Direction.DOWN);
        enterState(State.HUNTER);
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        Direction pacmanDir = PacmanRef.getDirection();
        Vector2 predict = PacmanRef.getGridPosition().add(pacmanDir.toVector2().mul(2));
        Vector2 target = predict.mul(2).sub(_Blinky.getGridPosition());
        return target;
    }


    private final ImmutableEntity _Blinky;
    
}
