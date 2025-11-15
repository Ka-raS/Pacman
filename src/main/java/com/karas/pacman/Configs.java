package com.karas.pacman;

import java.awt.Color;
import java.awt.Dimension;

import com.karas.pacman.commons.Vector2;

public class Configs {

    public static final int UPS_TARGET = 60;    // 60 <= UPS_TARGET <= 2e7
    public static final int FPS_TARGET = 60;
    public static final double SCALING = 6.0;   // CHANGE THIS
    public static final boolean DEBUG  = true;
    public static final Color BACKGROUND_COLOR = Color.BLACK;

    public static final String TITLE             = "Pacman";
    public static final String MAP_PATH          = "/images/map.png";
    public static final String TILEMAP_PATH      = "/tilemap.txt";
    public static final String WINDOW_ICON_PATH  = "/images/icon.png";
    public static final String SPRITE_SHEET_PATH = "/images/spritesheet.png";

    public static final double SPRITE_INTERVAL  = 0.133;     // seconds per sprite change
    public static final double IDLE_DURATION    = 1.0;
    public static final double POWERUP_DURATION = 8.0;
    public static final double GHOST_FLASH_TIME = 2.0;      // when powerup is expiring
    public static final double ENDGAME_DURATION = 4.0;

    public static final class Grid {

        public static final Vector2 MAP_SIZE   = new Vector2(28,   31);
        public static final Vector2 PACMAN_POS = new Vector2(13.5, 23);
        public static final Vector2 BLINKY_POS = new Vector2(15, 11);
        public static final Vector2 PINKY_POS  = new Vector2(12,   14);
        public static final Vector2 INKY_POS   = new Vector2(15,   14);
        public static final Vector2 CLYDE_POS  = new Vector2(12, 11);


        private Grid() {}

    }

    public static final class PX {

        public static final int DOT_SIZE     = 2;
        public static final int TILE_SIZE    = 8;
        public static final int SPRITE_SIZE  = 16;
        public static final int POWERUP_SIZE = 6;
        public static final Vector2 MAP_SIZE = Grid.MAP_SIZE.mul(TILE_SIZE);
        
        // per second
        public static final int PACMAN_SPEED = 54; 
        public static final int BLINKY_SPEED = 54;
        public static final int PINKY_SPEED  = 50;
        public static final int INKY_SPEED   = 50;
        public static final int CLYDE_SPEED  = 46;


        private PX() {}

    }

    public static final class UI {

        public static final int DOT_SIZE     = (int) (PX.DOT_SIZE     * SCALING);
        public static final int TILE_SIZE    = (int) (PX.TILE_SIZE    * SCALING);
        public static final int SPRITE_SIZE  = (int) (PX.SPRITE_SIZE  * SCALING);
        public static final int POWERUP_SIZE = (int) (PX.POWERUP_SIZE * SCALING);
        public static final Vector2 MAP_SIZE = PX.MAP_SIZE.mul(SCALING);


        private UI() {}

    }

    public static final Dimension WINDOW_SIZE = new Dimension(UI.MAP_SIZE.ix(), UI.MAP_SIZE.iy()); // TODO: temporary


    private Configs() {}

}
