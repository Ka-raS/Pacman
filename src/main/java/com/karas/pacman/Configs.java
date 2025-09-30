package com.karas.pacman;

import java.net.URL;

public class Configs {

    public static final String TITLE = "Pacman";

    public static final URL MAP_PATH = Configs.class.getResource("/map.png");
    public static final URL WINDOW_ICON_PATH = Configs.class.getResource("/icon.png");
    public static final URL SPRITE_SHEET_PATH = Configs.class.getResource("/spritesheet.png");

    public static final int SCALING = 4;
    public static final int UPS_TARGET = 60; // updates per second

    public static final int TILE_SIZE_PX = 8;
    public static final int SPRITE_SIZE_PX = 16;
    public static final int TILE_SIZE = TILE_SIZE_PX * SCALING;
    public static final int SPRITE_SIZE = SPRITE_SIZE_PX * SCALING;

    public static final int MAP_ROWS = 29;
    public static final int MAP_COLS = 26; 

    // Offset from 0,0
    public static final int MAP_X_OFFSET = 32;
    public static final int MAP_Y_OFFSET = 32;

    public static final int PACMAN_SPEED = 360 / UPS_TARGET; // pixels per update
    public static final int SPRITE_INTERVAL = UPS_TARGET / 6;

}
