package com.karas.pacman.entities.ghosts;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourceManager;
import com.karas.pacman.resources.SpriteID;

public final class Clyde extends Ghost {
    
    public Clyde(ImmutableEntity PacmanRef, ImmutableMap MapRef, ResourceManager ResourceMgr) {
        super(
            Constants.Grid.CLYDE_POSITION,
            Direction.UP,
            Constants.Pixel.CLYDE_SPEED,
            SpriteID.CLYDE,
            PacmanRef, MapRef, ResourceMgr
        );
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        Vector2 pacmanPos = PacmanRef.getGridPosition();
        double distance = getGridPosition().distance(pacmanPos);
        return distance > Constants.Grid.CLYDE_TARGET_DISTANCE ? pacmanPos : Constants.Grid.CLYDE_SCATTER_POSITION;
    }
    
}
