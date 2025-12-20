package com.karas.pacman.entities.ghosts;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.SpriteID;

public final class Blinky extends Ghost {
    
    public Blinky(ImmutableEntity PacmanRef, ImmutableMap MapRef, ResourcesManager ResourcesMgr) {
        super(
            Constants.Grid.BLINKY_POSITION,
            Direction.LEFT,
            Constants.Pixel.BLINKY_SPEED,
            SpriteID.BLINKY,
            PacmanRef, MapRef, ResourcesMgr
        );
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        return PacmanRef.getGridPosition();
    }
    
}
