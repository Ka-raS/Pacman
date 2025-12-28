package com.karas.pacman.commons;

public enum Direction {

    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    public boolean isSameAxis(Direction other) {
        return (ordinal() & 1) == (other.ordinal() & 1);
    }

    public Direction opposite() {
        return values()[(ordinal() + 2) & 3];
    }

    public Vector2 vector2() {
        return _vector2; 
    }


    private Direction(int x, int y) {
        _vector2 = new Vector2(x, y);
    }

    private final Vector2 _vector2;

}
