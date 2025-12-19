package com.karas.pacman.maps;

public enum Tile {

    NONE, WALL, PELLET, POWERUP, TUNNEL;
    
    public static Tile of(char value) {
        return switch (value) {
            case ' ' -> NONE;
            case '#' -> WALL;
            case '.' -> PELLET;
            case 'o' -> POWERUP;
            case 't' -> TUNNEL;

            default  -> null;
        };
    }

}
