package com.karas.pacman.entities.ghosts;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.Sound;

public class Clyde extends Ghost {
    
    public Clyde(ImmutableEntity pacman, ImmutableMap map, BufferedImage[] baseSprite, 
                 BufferedImage[] preySprite, BufferedImage[] deathSprite, Sound deathSound) {
        super(
            Configs.Grid.CLYDE_POS,
            Direction.LEFT,
            Configs.PX.CLYDE_SPEED,
            baseSprite, preySprite, deathSprite, deathSound,
            pacman, map
        );
    }

    @Override
    protected Vector2 findHunterTarget(ImmutableEntity pacman) {
        Vector2 pacmanPos = pacman.getGridPos();
        Vector2 currPos = getGridPos();
        double distance = currPos.distance(pacmanPos);
        return distance > Configs.Grid.CLYDE_TARGET_DISTANCE ? pacmanPos : Configs.Grid.CLYDE_SCATTER_POS;
    }
    
}
