package com.karas.pacman.maps;

import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;

public interface ImmutableMap {
    
    int getDotCounts();

    boolean checkWall(Vector2 gridPos);

    boolean validDirection(Vector2 position, Direction nextDirection);

    Vector2 getTunnelExit(Vector2 position);

}
