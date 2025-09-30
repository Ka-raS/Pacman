package com.karas.pacman.common;

public class Directions {
    public static final int RIGHT = 0;
    public static final int LEFT  = 1;
    public static final int UP    = 2;
    public static final int DOWN  = 3;

    public static Vector2 getVector2(int direction) {
        return VECTORS[direction];
    }

    public static int getSpritePos(int direction) {
        return SPRITES[direction];
    }

    private static final int[] SPRITES = {0, 2, 4, 6};
    
    private static final Vector2[] VECTORS = {
        new Vector2(1, 0),
        new Vector2(-1, 0),
        new Vector2(0, -1),
        new Vector2(0, 1)
    };

}
