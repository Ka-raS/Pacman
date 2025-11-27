package com.karas.pacman.entities.ghosts;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.audio.Sound;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;

public class Pinky extends Ghost {
    
    public Pinky(ImmutableEntity PacmanRef, ImmutableMap MapRef, BufferedImage[] BaseImages, 
                 BufferedImage[] PreyImages, BufferedImage[] DeathImages, Sound DeathSound) {
        super(
            Configs.Grid.PINKY_POSITION,
            Direction.UP,
            Configs.PX.PINKY_SPEED,
            BaseImages, PreyImages, DeathImages, DeathSound,
            PacmanRef, MapRef
        );
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        Direction pacmanDir = PacmanRef.getDirection();
        Vector2 pacmanPredict = PacmanRef.getGridPosition().add(pacmanDir.toVector2().mul(4));
        return pacmanPredict;
    }

}
