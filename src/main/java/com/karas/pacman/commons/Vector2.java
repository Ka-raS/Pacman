package com.karas.pacman.commons;

public record Vector2(double x, double y) {

    public int ix()   { return (int) x; }
    public int iy()   { return (int) y; }

    public Vector2 abs()  { return new Vector2(Math.abs(x),   Math.abs(y)); }
    public Vector2 ceil() { return new Vector2(Math.ceil(x),  Math.ceil(y)); }

    public Vector2 add(double n) { return new Vector2(x + n, y + n); }
    public Vector2 sub(double n) { return new Vector2(x - n, y - n); }
    public Vector2 mul(double n) { return new Vector2(x * n, y * n); }
    public Vector2 div(double n) { return new Vector2(x / n, y / n); }
    public Vector2 mod(double n) { return new Vector2(x % n, y % n); }

    public Vector2 add(Vector2 other) { return new Vector2(x + other.x, y + other.y); }
    public Vector2 sub(Vector2 other) { return new Vector2(x - other.x, y - other.y); }

}
