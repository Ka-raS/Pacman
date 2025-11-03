package com.karas.pacman.maps;

import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;

public interface ImmutableMap {
    
    int getDotCounts();

    boolean validDirection(Vector2 position, Direction nextDirection);

    boolean validGridDirection(Vector2 gridPos, Direction nextDirection);

    Vector2 nearestMovableGridPos(Vector2 position);

}
