package com.karas.pacman.maps;

import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;

public interface ImmutableMap {
    
    int getPelletCounts();

    boolean isNotWall(Vector2 gridPosition);

    boolean isValidDirection(Vector2 position, Direction nextDirection);

    Vector2 tryTunneling(Vector2 position, Direction direction);

}
