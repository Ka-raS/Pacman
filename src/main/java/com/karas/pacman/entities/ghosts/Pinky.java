package com.karas.pacman.entities.ghosts;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.SpriteID;

public final class Pinky extends Ghost {
    
    public Pinky(ImmutableEntity PacmanRef, ImmutableMap MapRef, ResourcesManager ResourcesMgr) {
        super(
            Constants.Grid.PINKY_POSITION,
            Direction.UP,
            Constants.Pixel.PINKY_SPEED,
            SpriteID.PINKY,
            PacmanRef, MapRef, ResourcesMgr
        );
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        Direction pacmanDir = PacmanRef.getDirection();
        Vector2 pacmanPredict = PacmanRef.getGridPosition().add(pacmanDir.toVector2().mul(4));
        return pacmanPredict;
    }

}
