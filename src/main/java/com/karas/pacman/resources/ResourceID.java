package com.karas.pacman.resources;

public enum ResourceID {
    
    WINDOW_ICON(    Type.IMAGE, "/images/icon.png"),
    TITLE_IMAGE(    Type.IMAGE, "/images/game_title.png"),
    HIGHSCORE_IMAGE(Type.IMAGE, "/images/highscore.png"),
    SPRITE_SHEET(   Type.IMAGE, "/images/spritesheet.png"),
    MAP_IMAGE(      Type.IMAGE, "/images/map.png"),

    EAT_WA_SOUND(      Type.SOUND, "/sounds/eat_wa.wav"),
    EAT_KA_SOUND(      Type.SOUND, "/sounds/eat_ka.wav"),
    PACMAN_DEATH_SOUND(Type.SOUND, "/sounds/pacman_death.wav"),
    GHOST_DEATH_SOUND( Type.SOUND, "/sounds/ghost_death.wav"),
    GAME_START_SOUND(  Type.SOUND, "/sounds/game_start.wav"),
    GAME_NORMAL_SOUND( Type.SOUND, "/sounds/game_normal.wav"),
    GAME_POWERUP_SOUND(Type.SOUND, "/sounds/game_powerup.wav"),
    GAME_WON_SOUND(    Type.SOUND, "/sounds/game_won.wav"),

    FONT(    Type.FONT, "/fonts/Emulogic-zrEw.ttf"),
    TILEMAP( Type.TEXT, "/tilemap.txt"),
    DATABASE(Type.TEXT, "./highscores.csv");

    public static enum Type {
        IMAGE, SOUND, FONT, TEXT
    }

    public String path() { return _path; }
    public Type   type() { return _type; }


    private ResourceID(Type type, String path) {
        _type = type; _path = path; 
    }

    private final String _path;
    private final Type _type;

}
