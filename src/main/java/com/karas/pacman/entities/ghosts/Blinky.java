package com.karas.pacman.entities.ghosts;

import java.util.Collection;
import java.util.Collections;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.Pacman;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.SpriteSheet;

public class Blinky extends Ghost {
    
    public Blinky(Pacman pacmanRef, ImmutableMap mapRef) {
        super(
            Map.toPixelVector2(Configs.Grid.BLINKY_POS),
            Direction.RIGHT,
            Configs.PX.BLINKY_SPEED,
            SpriteSheet.BLINKY,
            pacmanRef,
            mapRef
        );
    }

    // TODO: In construction

    @Override
    protected Collection<Direction> findPathToPacman(Vector2 pacmanPos) {
        return Collections.singleton(Direction.RIGHT);
    }

    @Override
    protected Collection<Direction> findPathToRunaway(Vector2 pacmanPos) {
        return Collections.singleton(Direction.RIGHT);
    }
}
