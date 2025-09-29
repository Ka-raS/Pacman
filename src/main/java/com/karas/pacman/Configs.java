package com.karas.pacman;

public class Configs {

    public static String TITLE = "Pacman";
    public static int SCALING = 4;
    public static int UPS_TARGET = 60; // Updates per second

    public static int TILE_SIZE_PX = 8;
    public static int SPRITE_SIZE_PX = 16;
    public static int TILE_SIZE = TILE_SIZE_PX * SCALING;
    public static int SPRITE_SIZE = SPRITE_SIZE_PX * SCALING;

    public static int MAP_ROWS = 29;
    public static int MAP_COLS = 26; 

    // Offset from 0,0
    public static int MAP_X_OFFSET = 32;
    public static int MAP_Y_OFFSET = 32;

    public static int PACMAN_SPEED = 360 / UPS_TARGET; // pixels per update
    public static int SPRITE_INTERVAL = UPS_TARGET / 6;

}
