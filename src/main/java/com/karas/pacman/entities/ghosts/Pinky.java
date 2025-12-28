package com.karas.pacman.entities.ghosts;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourceManager;
import com.karas.pacman.resources.SpriteID;

public final class Pinky extends Ghost {
    
    public Pinky(ImmutableEntity PacmanRef, ImmutableMap MapRef, ResourceManager ResourceMgr) {
        super(
            Constants.Grid.PINKY_POSITION,
            Direction.UP,
            Constants.Pixel.PINKY_SPEED,
            SpriteID.PINKY,
            PacmanRef, MapRef, ResourceMgr
        );
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        Vector2 pacmanDir = PacmanRef.getDirection().vector2();
        Vector2 pacmanPredict = PacmanRef.getGridPosition().add(pacmanDir.mul(4));
        return pacmanPredict;
    }

}
