package com.karas.pacman.entities;

import com.karas.pacman.commons.Vector2;

public interface ImmutableEntity {
    
    Vector2 getPosition();

    Vector2 getNearestMovableGridPos();

    boolean collidesWith(ImmutableEntity other);

}
