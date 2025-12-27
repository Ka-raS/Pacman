package com.karas.pacman.commons;

public enum Direction {

    UP, RIGHT, DOWN, LEFT;

    public boolean isSameAxis(Direction other) {
        return (ordinal() & 1) == (other.ordinal() & 1);
    }

    public Direction getOpposite() {
        return values()[(ordinal() + 2) & 3];
    }

    public Vector2 toVector2() { 
        return _VECTORS[ordinal()]; 
    }


    private static final Vector2[] _VECTORS = {
        new Vector2( 0.0, -1.0), 
        new Vector2( 1.0,  0.0),
        new Vector2( 0.0,  1.0), 
        new Vector2(-1.0,  0.0)
    };

}
