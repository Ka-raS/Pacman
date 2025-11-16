package com.karas.pacman.commons;

public record Vector2(double x, double y) {

    public int ix() { return (int) x; }
    public int iy() { return (int) y; }

    public Vector2 abs()   { return new Vector2(Math.abs(x),   Math.abs(y)); }
    public Vector2 ceil()  { return new Vector2(Math.ceil(x),  Math.ceil(y)); }
    public Vector2 floor() { return new Vector2(Math.floor(x), Math.floor(y)); }

    public Vector2 add(double n) { return new Vector2(x + n, y + n); }
    public Vector2 sub(double n) { return new Vector2(x - n, y - n); }
    public Vector2 mul(double n) { return new Vector2(x * n, y * n); }
    public Vector2 div(double n) { return new Vector2(x / n, y / n); }
    public Vector2 mod(double n) { return new Vector2(x % n, y % n); }

    public Vector2 add(Vector2 other) { return new Vector2(x + other.x, y + other.y); }
    public Vector2 sub(Vector2 other) { return new Vector2(x - other.x, y - other.y); }

    public double distance(Vector2 other) {   // Manhattan
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
    
    public Direction furthestFrom(Vector2 other, Iterable<Direction> directions) {
        Direction result = null;
        double maxDistance = -1.0;
        for (Direction dir : directions) {
            Vector2 next = this.add(dir.toVector2());
            double distance = other.distance(next);
            if (maxDistance < distance) {
                maxDistance = distance;
                result = dir;
            }
        }
        return result;
    }

    public Direction closestTo(Vector2 other, Iterable<Direction> directions) {
        Direction result = null;
        double minDistance = Double.MAX_VALUE;
        for (Direction dir : directions) {
            Vector2 next = this.add(dir.toVector2());
            double distance = other.distance(next);
            if (minDistance > distance) {
                minDistance = distance;
                result = dir;
            }
        }
        return result;
    }

}
