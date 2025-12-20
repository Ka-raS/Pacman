package com.karas.pacman.entities.ghosts;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.SpriteID;

public final class Inky extends Ghost {
    
    public Inky(ImmutableEntity PacmanRef, ImmutableMap MapRef, ImmutableEntity BlinkyRef, ResourcesManager ResourcesMgr) {
        super(
            Constants.Grid.INKY_POSITION,
            Direction.DOWN,
            Constants.Pixel.INKY_SPEED,
            SpriteID.INKY,
            PacmanRef, MapRef, ResourcesMgr
        );
        _Blinky = BlinkyRef;
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        Direction pacmanDir = PacmanRef.getDirection();
        Vector2 predict = PacmanRef.getGridPosition().add(pacmanDir.toVector2().mul(2));
        Vector2 target = predict.mul(2).sub(_Blinky.getGridPosition());
        return target;
    }


    private final ImmutableEntity _Blinky;
    
}
