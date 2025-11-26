package com.karas.pacman.entities.ghosts;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.audio.Sound;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;

public class Blinky extends Ghost {
    
    public Blinky(ImmutableEntity PacmanRef, ImmutableMap MapRef, BufferedImage[] BaseImages, 
                  BufferedImage[] PreyImages, BufferedImage[] DeathImages, Sound DeathSound) {
        super(
            Configs.Grid.BLINKY_POSITION,
            Direction.LEFT,
            Configs.PX.BLINKY_SPEED,
            BaseImages, PreyImages, DeathImages, DeathSound,
            PacmanRef, MapRef
        );
    }

    @Override
    public void reset() {
        setGridPosition(Configs.Grid.BLINKY_POSITION);
        setDirection(Direction.LEFT);
        enterState(State.HUNTER);
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        return PacmanRef.getGridPosition();
    }
    
}
