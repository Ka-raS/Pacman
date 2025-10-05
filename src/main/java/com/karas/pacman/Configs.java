package com.karas.pacman;

import java.awt.Color;
import java.io.InputStream;

public class Configs {

    public static final String TITLE = "Pacman";
    public static final Color BACKGROUND_COLOR = Color.BLACK;

    public static final InputStream MAP_PATH = Configs.class.getResourceAsStream("/map.png");
    public static final InputStream WINDOW_ICON_PATH = Configs.class.getResourceAsStream("/icon.png");
    public static final InputStream SPRITE_SHEET_PATH = Configs.class.getResourceAsStream("/spritesheet.png");

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
