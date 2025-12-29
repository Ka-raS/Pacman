package com.karas.pacman.maps;

import com.karas.pacman.commons.Vector2;

public interface ImmutableMap {

    Tile tileAt(Vector2 gridPosition);

    /** @return grid position, or {@code null} if no tunnel */
    Vector2 useTunnel(Vector2 gridPosition);

}
