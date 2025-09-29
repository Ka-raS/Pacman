package com.karas.common;

public class Directions {
    public static final int RIGHT = 0;
    public static final int LEFT  = 1;
    public static final int UP    = 2;
    public static final int DOWN  = 3;

    public static final Vector2[] VECTORS = {
        new Vector2(1, 0),
        new Vector2(-1, 0),
        new Vector2(0, -1),
        new Vector2(0, 1)
    };

    public static final int[] SPRITES = {0, 2, 4, 6};
}
