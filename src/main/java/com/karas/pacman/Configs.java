package com.karas.pacman;

import java.awt.Color;

import com.karas.pacman.commons.Vector2;

public class Configs {

    public static final int UPS_TARGET = 60;
    public static final int FPS_TARGET = 60;
    public static final double SCALING = 3.0;   // CHANGE THIS
    public static final String TITLE   = "Pacman";
    public static final Color BACKGROUND_COLOR = Color.BLACK;

    public static class Path {

        public static final String WINDOW_ICON  = "/images/icon.png";
        public static final String TITLE_IMAGE  = "/images/game_title.png";
        public static final String MAP_IMAGE    = "/images/map.png";
        public static final String SPRITE_SHEET = "/images/spritesheet.png";
        public static final String TILEMAP      = "/tilemap.txt";
        public static final String FONT         = "/fonts/Emulogic-zrEw.ttf";

        public static final String EAT_WA_SOUND       = "/sounds/eat_wa.wav";
        public static final String EAT_KA_SOUND       = "/sounds/eat_ka.wav";
        public static final String PACMAN_DEATH_SOUND = "/sounds/pacman_death.wav";
        public static final String GHOST_DEATH_SOUND  = "/sounds/ghost_death.wav";
        public static final String GAME_START_SOUND   = "/sounds/game_start.wav";
        public static final String GAME_NORMAL_SOUND  = "/sounds/game_normal.wav";
        public static final String GAME_POWERUP_SOUND = "/sounds/game_powerup.wav";

        private Path() {}

    }

    public static class Time {

        public static final double UPDATE_INTERVAL      = 1.0 / UPS_TARGET;
        public static final double REPAINT_INTERVAL     = 1.0 / FPS_TARGET;
        public static final double SPRITE_INTERVAL      = 0.133;  // per sprite change
        public static final double TUNNEL_COOLDOWN      = 1.0;
        public static final double POWERUP_DURATION     = 8.0;
        public static final double GHOST_FLASH_DURATION = POWERUP_DURATION - 6.0; // when powerup is expiring
        public static final double STARTING_DURATION    = 4.0;
        public static final double GAMEOVER_DURATION    = 4.0;

        // TODO
        public static final double GHOST_SCATTER_DURATION = 7.0; 
        public static final double GHOST_SCATTER_INTERVAL = 20.0 + GHOST_SCATTER_DURATION;

        private Time() {}

    }

    public static class Grid {

        public static final Vector2 MAP_SIZE   = new Vector2(28,   31);
        public static final Vector2 PACMAN_POS = new Vector2(13.5, 23);
        public static final Vector2 BLINKY_POS = new Vector2(15,   11);
        public static final Vector2 PINKY_POS  = new Vector2(12,   14);
        public static final Vector2 INKY_POS   = new Vector2(15,   14);
        public static final Vector2 CLYDE_POS  = new Vector2(12,   11);

        public static final int CLYDE_TARGET_DISTANCE = 8;

        // TODO
        public static final Vector2 BLINKY_SCATTER_POS = new Vector2(MAP_SIZE.ix() - 1, 0);
        public static final Vector2 PINKY_SCATTER_POS  = new Vector2(0, 0);
        public static final Vector2 INKY_SCATTER_POS   = new Vector2(MAP_SIZE.ix() - 1, MAP_SIZE.iy() - 1);
        public static final Vector2 CLYDE_SCATTER_POS  = new Vector2(0, MAP_SIZE.iy() - 1);

        private Grid() {}

    }

    public static class PX {

        public static final int TILE_SIZE    = 8;
        public static final int PELLET_SIZE  = 2;
        public static final int SPRITE_SIZE  = 16;
        public static final int POWERUP_SIZE = 6;
        public static final Vector2 MAP_SIZE = Grid.MAP_SIZE.mul(TILE_SIZE);
        
        // per second
        public static final int PACMAN_SPEED = 54; 
        public static final int BLINKY_SPEED = 54;
        public static final int PINKY_SPEED  = 50;
        public static final int INKY_SPEED   = 50;
        public static final int CLYDE_SPEED  = 46;

        public static final float FONT_SIZE_SMALL = 6.0f;
        public static final float FONT_SIZE_BASE  = 8.0f;
        public static final float FONT_SIZE_LARGE = 12.0f;

        private PX() {}

    }

    public static class UI {

        public static final int PELLET_SIZE  = (int) (PX.PELLET_SIZE  * SCALING);
        public static final int TILE_SIZE    = (int) (PX.TILE_SIZE    * SCALING);
        public static final int SPRITE_SIZE  = (int) (PX.SPRITE_SIZE  * SCALING);
        public static final int POWERUP_SIZE = (int) (PX.POWERUP_SIZE * SCALING);
        public static final Vector2 MAP_SIZE = PX.MAP_SIZE.mul(SCALING);
        
        public static final float FONT_SIZE_SMALL = (float) (PX.FONT_SIZE_SMALL * SCALING);
        public static final float FONT_SIZE_BASE  = (float) (PX.FONT_SIZE_BASE  * SCALING);
        public static final float FONT_SIZE_LARGE = (float) (PX.FONT_SIZE_LARGE * SCALING);
        
        public static final Vector2 WINDOW_SIZE = MAP_SIZE; // TODO: temporary
        
        private UI() {}

    }

    private Configs() {}

}
