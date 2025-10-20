package com.karas.pacman.common;

public class Vector2 {
    
    public Vector2(double x, double y) {
        _x = x;
        _y = y;
    }

    public double x() {
        return _x;
    }

    public double y() {
        return _y;
    }

    public int ix() {
        return (int) _x;
    }

    public int iy() {
        return (int) _y;
    }

    public Vector2 ceil() {
        return new Vector2(Math.ceil(_x), Math.ceil(_y));
    }

    public Vector2 floor() {
        return new Vector2(Math.floor(_x), Math.floor(_y));
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(_x + other._x, _y + other._y);
    }

    public Vector2 add(double n) {
        return new Vector2(_x + n, _y + n);
    }

    public Vector2 sub(Vector2 other) {
        return new Vector2(_x - other._x, _y - other._y);
    }

    public Vector2 sub(double n) {
        return new Vector2(_x - n, _y - n);
    }

    public Vector2 mul(double n) {
        return new Vector2(_x * n, _y * n);
    }

    public Vector2 div(double n) {
        return new Vector2(_x / n, _y / n);
    }

    public Vector2 mod(double n) {
        return new Vector2(_x % n, _y % n);
    }

    private double _x, _y;
}
