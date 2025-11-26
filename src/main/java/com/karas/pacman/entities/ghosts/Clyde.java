package com.karas.pacman.entities.ghosts;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.audio.Sound;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;

public class Clyde extends Ghost {
    
    public Clyde(ImmutableEntity PacmanRef, ImmutableMap MapRef, BufferedImage[] BaseImages, 
                 BufferedImage[] PreyImages, BufferedImage[] DeathImages, Sound DeathSound) {
        super(
            Configs.Grid.CLYDE_POSITION,
            Direction.UP,
            Configs.PX.CLYDE_SPEED,
            BaseImages, PreyImages, DeathImages, DeathSound,
            PacmanRef, MapRef
        );
    }

    @Override
    public void reset() {
        setGridPosition(Configs.Grid.CLYDE_POSITION);
        setDirection(Direction.UP);
        enterState(State.HUNTER);
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        Vector2 pacmanPos = PacmanRef.getGridPosition();
        Vector2 currPos = getGridPosition();
        double distance = currPos.distance(pacmanPos);
        return distance > Configs.Grid.CLYDE_TARGET_DISTANCE ? pacmanPos : Configs.Grid.CLYDE_SCATTER_POSITION;
    }
    
}
