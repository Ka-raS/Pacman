package com.karas.pacman.maps;

public enum Tile {

    NONE, WALL, DOT, POWERUP, PORTAL;
    
    public static Tile fromChar(char value) {
        return switch (value) {
            case ' ' -> NONE;
            case '#' -> WALL;
            case '.' -> DOT;
            case 'o' -> POWERUP;
            case 'p' -> PORTAL;

            default  -> null;
        };
    }

}
