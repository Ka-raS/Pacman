package com.karas.pacman.entities.ghosts;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.SpriteSheet;

public class Inky extends Ghost {
    
    public Inky(ImmutableEntity pacman, ImmutableEntity blinky, ImmutableMap map) {
        super(
            Configs.Grid.INKY_POS,
            Direction.RIGHT,
            Configs.PX.INKY_SPEED,
            SpriteSheet.INKY,
            pacman,
            map
        );
        _Blinky = blinky;
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity pacman) {
        Direction pacmanDir = pacman.getDirection();
        Vector2 pacmanPredict = pacman.getGridPos().add(pacmanDir.toVector2().mul(2));
        Vector2 target = pacmanPredict.mul(2).sub(_Blinky.getGridPos());
        return target;
    }


    private final ImmutableEntity _Blinky;
    
}
