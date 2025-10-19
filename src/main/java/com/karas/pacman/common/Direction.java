package com.karas.pacman.common;

import java.util.concurrent.ThreadLocalRandom;

public enum Direction {

    RIGHT(0), LEFT(1), UP(2), DOWN(3);

    public static Direction random() {
        Direction[] v = values();
        return v[ThreadLocalRandom.current().nextInt(v.length)];
    }

    public static int toSpritePos(Direction d) {
        return SPRITES[d._value];
    }

    public static Vector2 toVector2(Direction d) {
        return VECTORS[d._value];
    }

    private static final int[] SPRITES = {0, 2, 4, 6};
    
    private static final Vector2[] VECTORS = {
        new Vector2(1, 0),
        new Vector2(-1, 0),
        new Vector2(0, -1),
        new Vector2(0, 1)
    };

    private Direction(int value) {
        _value = value;
    }

    private int _value;

}
