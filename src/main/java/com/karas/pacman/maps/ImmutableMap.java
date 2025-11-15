package com.karas.pacman.maps;

import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;

public interface ImmutableMap {
    
    int getDotCounts();

    boolean isMovable(Vector2 position);

    boolean validDirection(Vector2 position, Direction nextDirection);

    Vector2 nearestMovableGridPos(Vector2 position);

}
