package com.karas.pacman.commons;

public record Vector2(double x, double y) {

    public int ix() { return (int) x; }
    public int iy() { return (int) y; }

    public Vector2 abs()  { return new Vector2(Math.abs(x),  Math.abs(y)); }
    public Vector2 ceil() { return new Vector2(Math.ceil(x), Math.ceil(y)); }

    public Vector2 add(double n) { return new Vector2(x + n, y + n); }
    public Vector2 sub(double n) { return new Vector2(x - n, y - n); }
    public Vector2 mul(double n) { return new Vector2(x * n, y * n); }
    public Vector2 div(double n) { return new Vector2(x / n, y / n); }
    public Vector2 mod(double n) { return new Vector2(x % n, y % n); }

    public Vector2 add(Vector2 other) { return new Vector2(x + other.x, y + other.y); }
    public Vector2 sub(Vector2 other) { return new Vector2(x - other.x, y - other.y); }

    public double heuristic(Vector2 other) { 
        return Math.abs(x - other.x) + Math.abs(y - other.y); 
    }
    
    public Direction toDirection() {
        for (int pos = 0; pos < Direction.VECTORS.length; ++pos)
            if (Direction.VECTORS[pos].equals(this))
                return Direction.values()[pos];
        return null;
    }

    public Direction toDirection(Vector2 target) {
        return target.sub(this).toDirection();
    }

}
