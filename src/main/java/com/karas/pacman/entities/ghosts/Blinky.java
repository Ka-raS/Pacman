package com.karas.pacman.entities.ghosts;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.Sound;

public class Blinky extends Ghost {
    
    public Blinky(ImmutableEntity pacman, ImmutableMap map, BufferedImage[] baseSprite, 
                  BufferedImage[] preySprite, BufferedImage[] deathSprite, Sound deathSound) {
        super(
            Configs.Grid.BLINKY_POSITION,
            Direction.LEFT,
            Configs.PX.BLINKY_SPEED,
            baseSprite, preySprite, deathSprite, deathSound,
            pacman, map
        );
    }

    @Override
    public void reset() {
        setGridPosition(Configs.Grid.BLINKY_POSITION);
        setDirection(Direction.LEFT);
        enterState(State.HUNTER);
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity pacman) {
        return pacman.getGridPosition();
    }
    
}
