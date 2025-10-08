package com.karas.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;

import com.karas.pacman.common.Vector2;

public class Configs {

    public static final String TITLE = "Pacman";
    public static final int SCALING = 3;
    public static final int UPS_TARGET = 60; // updates per second
    public static final Color BACKGROUND_COLOR = Color.BLACK;

    public static final InputStream MAP_PATH = Configs.class.getResourceAsStream("/images/map.png");
    public static final InputStream WINDOW_ICON_PATH = Configs.class.getResourceAsStream("/images/icon.png");
    public static final InputStream SPRITE_SHEET_PATH = Configs.class.getResourceAsStream("/images/spritesheet.png");

    public static final int TILE_SIZE_PX = 8;
    public static final int TILE_SIZE_UI = TILE_SIZE_PX * SCALING;
    public static final int SPRITE_SIZE_PX = 16;
    public static final int SPRITE_SIZE_UI = SPRITE_SIZE_PX * SCALING;

    public static final int MAP_BORDER_GRID = 1;
    public static final Vector2 MAP_SIZE_GRID = new Vector2(26, 29);

    public static final Vector2 MAP_SIZE_UI = new Vector2(
        (MAP_SIZE_GRID.x() + 2*MAP_BORDER_GRID) * TILE_SIZE_UI, 
        (MAP_SIZE_GRID.y() + 2*MAP_BORDER_GRID) * TILE_SIZE_UI
    );
    public static final Dimension WINDOW_SIZE = new Dimension(MAP_SIZE_UI.x(), MAP_SIZE_UI.y()); // TODO: temporary

    public static final int PACMAN_SPEED = 60 * SCALING / UPS_TARGET; // pixels per update
    public static final int SPRITE_INTERVAL = UPS_TARGET / 6;

}
