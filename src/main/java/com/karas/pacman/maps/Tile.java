package com.karas.pacman.maps;

public enum Tile {

    NONE, WALL, DOT, POWERUP;
    
    public static Tile fromChar(char value) {
        return switch (value) {
            case ' ' -> NONE;
            case '#' -> WALL;
            case '.' -> DOT;
            case 'o' -> POWERUP;

            default  -> null;
        };
    }

}
