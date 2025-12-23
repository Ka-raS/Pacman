package com.karas.pacman.maps;

public enum Tile {

    NONE, WALL, PELLET, POWERUP, GATE, TUNNEL;
    
    public static Tile of(char value) {
        return switch (value) {
            case '#' -> WALL;
            case '.' -> PELLET;
            case 'o' -> POWERUP;
            case 'g' -> GATE;
            case 't' -> TUNNEL;
            default  -> NONE;
        };
    }

}
