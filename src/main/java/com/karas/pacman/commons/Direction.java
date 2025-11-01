package com.karas.pacman.commons;

import java.util.concurrent.ThreadLocalRandom;

public enum Direction {

    UP, RIGHT, DOWN, LEFT;

    public static Direction random() {
        Direction[] v = values();
        int pos = ThreadLocalRandom.current().nextInt(v.length);
        return v[pos];
    }

    public Vector2 toVector2() { 
        return _VECTORS[ordinal()]; 
    }

    public boolean isVertical() {
        return this == UP || this == DOWN;
    }


    private static final Vector2[] _VECTORS = {
        new Vector2( 0.0, -1.0), 
        new Vector2( 1.0,  0.0),
        new Vector2( 0.0,  1.0), 
        new Vector2(-1.0,  0.0)
    };

}
