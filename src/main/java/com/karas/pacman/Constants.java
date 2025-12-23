package com.karas.pacman;

import com.karas.pacman.commons.Vector2;

public final class Constants {

    public static final int UPS_TARGET = 144;
    public static final int FPS_TARGET = Integer.MAX_VALUE;
    public static final String TITLE   = "Pacman";
    public static final double DEFAULT_SCALE = 3.0;

    public static final class Score {

        public static final int PELLET  = 10;
        public static final int POWERUP = 50;
        public static final int GHOST   = 200;

        private Score() {}

    }

    public static final class Color {

        public static final java.awt.Color BACKGROUND = java.awt.Color.BLACK;
        public static final java.awt.Color PELLET     = java.awt.Color.WHITE;
        public static final java.awt.Color SCORE      = java.awt.Color.CYAN;
        public static final java.awt.Color TEXT       = java.awt.Color.WHITE;
        public static final java.awt.Color HIGHLIGHT  = java.awt.Color.YELLOW;
        
        public static final java.awt.Color MAIN_MENU  = new java.awt.Color(0, 16, 48);
        public static final java.awt.Color PAUSE_MENU = new java.awt.Color(0, 0, 0, 192);

        private Color() {}

    }

    public static final class Time {

        public static final double UPDATE_INTERVAL      = 1.0 / UPS_TARGET;
        public static final double REPAINT_INTERVAL     = 1.0 / FPS_TARGET;
        public static final double SPRITE_INTERVAL      = 0.133;  // per sprite change
        public static final double SCORE_DURATION       = 1.0;
        public static final double POWERUP_DURATION     = 8.0;
        public static final double GHOST_FLASH_DURATION = POWERUP_DURATION * 0.25; // when powerup is expiring
        public static final double STARTING_DURATION    = 4.0;
        public static final double GAMELOST_DURATION    = 2.0;
        public static final double GAMEWON_DURATION     = 5.2;

    }

    public static final class Grid {

        public static final Vector2 MAP_SIZE        = new Vector2(28,   31);
        public static final Vector2 PACMAN_POSITION = new Vector2(13.5, 23);
        public static final Vector2 BLINKY_POSITION = new Vector2(13.5, 11);
        public static final Vector2 PINKY_POSITION  = new Vector2(11.5, 14); // is squished between a wall and a walkable tile
        public static final Vector2 INKY_POSITION   = new Vector2(13.5, 14);
        public static final Vector2 CLYDE_POSITION  = new Vector2(15.5, 14); // same goes for this one
        public static final Vector2 GHOST_HOME      = new Vector2(14  , 14);

        public static final int CLYDE_TARGET_DISTANCE = 8;
        public static final Vector2 CLYDE_SCATTER_POSITION  = new Vector2(0, MAP_SIZE.iy() - 1);

        private Grid() {}

    }

    public static final class Pixel {

        public static final int TILE_SIZE    = 8;
        public static final int PELLET_SIZE  = 2;
        public static final int SPRITE_SIZE  = 16;
        public static final int POWERUP_SIZE = 6;
        
        // per second
        public static final int PACMAN_SPEED = 56; 
        public static final int BLINKY_SPEED = 52;
        public static final int PINKY_SPEED  = 48;
        public static final int INKY_SPEED   = 48;
        public static final int CLYDE_SPEED  = 44;

        public static final int PACMAN_HUNTER_SPEED = 64;
        public static final int GHOST_PREY_SPEED    = 36;
        public static final int GHOST_DEAD_SPEED    = 98;

        public static final int FONT_SIZE_SMALL  = 6;
        public static final int FONT_SIZE_MEDIUM = 8;
        public static final int FONT_SIZE_LARGE  = 12;
        public static final Vector2 WINDOW_SIZE  = Grid.MAP_SIZE.mul(TILE_SIZE);

        private Pixel() {}

    }

    private Constants() {}

}
