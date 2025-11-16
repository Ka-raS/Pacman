package com.karas.pacman.entities.ghosts;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.SpriteSheet;

public class Blinky extends Ghost {
    
    public Blinky(ImmutableEntity pacman, ImmutableMap map) {
        super(
            Configs.Grid.BLINKY_POS,
            Direction.RIGHT,
            Configs.PX.BLINKY_SPEED,
            SpriteSheet.BLINKY,
            pacman,
            map
        );
    }

    @Override
    protected Vector2 findHunterTarget(ImmutableEntity pacman) {
        return pacman.getGridPos();
    }
    
}
