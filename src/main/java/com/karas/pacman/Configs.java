package com.karas.pacman;

import java.awt.Color;
import java.awt.Dimension;

import com.karas.pacman.common.Vector2;

public class Configs {

    public static final String TITLE = "Pacman";
    public static final int SCALING = 3;
    public static final Color BACKGROUND_COLOR = Color.BLACK;

    public static final String MAP_PATH = "/images/map.png";
    public static final String MAPTILE_PATH = "/maptile.txt";
    public static final String WINDOW_ICON_PATH = "/images/icon.png";
    public static final String SPRITE_SHEET_PATH = "/images/spritesheet.png";

    public static final Vector2 MAP_GRID = new Vector2(28, 31);
    public static final Vector2 PACMAN_POS_GRID = new Vector2(13.5, 23);

    public static final double SPRITE_INTERVAL = 0.133; // seconds per sprite change
    public static final int PACMAN_SPEED_PX = 54; // pixels per second

    public class PX {
        public static final int TILE_SIZE = 8;
        public static final int SPRITE_SIZE = 16;
        public static final Vector2 MAP_SIZE = MAP_GRID.mul(TILE_SIZE);

    }

    public class UI {
        public static final int TILE_SIZE = PX.TILE_SIZE * SCALING;
        public static final int SPRITE_SIZE = PX.SPRITE_SIZE * SCALING;
        public static final Vector2 MAP_SIZE = PX.MAP_SIZE.mul(SCALING);
    }

    public static final Dimension WINDOW_SIZE = new Dimension(UI.MAP_SIZE.ix(), UI.MAP_SIZE.iy()); // TODO: temporary

}
