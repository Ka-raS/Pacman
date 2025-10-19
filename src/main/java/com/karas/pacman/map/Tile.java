package com.karas.pacman.map;

public enum Tile {

    NONE, WALL, DOT, POWERUP;
    
    public static Tile fromChar(char c) {
        return values()[c - '0'];
    }
    
}
