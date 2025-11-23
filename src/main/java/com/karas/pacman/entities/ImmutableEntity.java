package com.karas.pacman.entities;

import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;

public interface ImmutableEntity {
    
    Vector2 getPosition();

    Direction getDirection();

    Vector2 getGridPos();

}
