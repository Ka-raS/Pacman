package com.karas.pacman.entities.ghosts;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.SpriteSheet;

public class Pinky extends Ghost {
    
    public Pinky(ImmutableEntity pacman, ImmutableMap map) {
        super(
            Configs.Grid.PINKY_POS,
            Direction.LEFT,
            Configs.PX.PINKY_SPEED,
            SpriteSheet.PINKY,
            pacman,
            map
        );
    }

    @Override
    protected Vector2 findHunterTarget(ImmutableEntity pacman) {
        Direction pacmanDir = pacman.getDirection();
        Vector2 pacmanPredict = pacman.getGridPos().add(pacmanDir.toVector2().mul(4));
        return pacmanPredict;
    }

}
