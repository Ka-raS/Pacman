package com.karas.pacman.common;

public class Vector2 {
    
    public Vector2(int x, int y) {
        _x = x;
        _y = y;
    }

    public int x() {
        return _x;
    }

    public int y() {
        return _y;
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(_x + other._x, _y + other._y);
    }

    public Vector2 sub(Vector2 other) {
        return new Vector2(_x - other._x, _y - other._y);
    }

    public Vector2 mul(int n) {
        return new Vector2(_x * n, _y * n);
    }

    private int _x, _y;
}
