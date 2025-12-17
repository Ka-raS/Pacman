package com.karas.pacman.entities.ghosts;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.SpriteSheet;

public class Clyde extends Ghost {
    
    public Clyde(ImmutableEntity PacmanRef, ImmutableMap MapRef, ResourcesManager ResourcesMgr) {
        super(
            Configs.Grid.CLYDE_POSITION,
            Direction.UP,
            Configs.PX.CLYDE_SPEED,
            SpriteSheet.CLYDE,
            PacmanRef, MapRef, ResourcesMgr
        );
    }


    @Override
    protected Vector2 findHunterTarget(ImmutableEntity PacmanRef) {
        Vector2 pacmanPos = PacmanRef.getGridPosition();
        Vector2 currPos = getGridPosition();
        double distance = currPos.distance(pacmanPos);
        return distance > Configs.Grid.CLYDE_TARGET_DISTANCE ? pacmanPos : Configs.Grid.CLYDE_SCATTER_POSITION;
    }
    
}
