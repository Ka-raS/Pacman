package com.karas.pacman.resources;

public enum ResourceID {
    
    WINDOW_ICON(    Type.IMAGE, false, "/images/icon.png"),
    TITLE_IMAGE(    Type.IMAGE, false, "/images/game_title.png"),
    HIGHSCORE_IMAGE(Type.IMAGE, false, "/images/highscore.png"),
    SPRITE_SHEET(   Type.IMAGE, true,  "/images/spritesheet.png"),
    MAP_IMAGE(      Type.IMAGE, true,  "/images/map.png"),

    EAT_WA_SOUND(      Type.SOUND, false, "/sounds/eat_wa.wav"),
    EAT_KA_SOUND(      Type.SOUND, false, "/sounds/eat_ka.wav"),
    PACMAN_DEATH_SOUND(Type.SOUND, false, "/sounds/pacman_death.wav"),
    GHOST_DEATH_SOUND( Type.SOUND, false, "/sounds/ghost_death.wav"),
    GAME_START_SOUND(  Type.SOUND, false, "/sounds/game_start.wav"),
    GAME_NORMAL_SOUND( Type.SOUND, false, "/sounds/game_normal.wav"),
    GAME_POWERUP_SOUND(Type.SOUND, false, "/sounds/game_powerup.wav"),
    GAME_WON_SOUND(    Type.SOUND, false, "/sounds/game_won.wav"),

    FONT(         Type.FONT, false, "/fonts/Emulogic-zrEw.ttf"),
    TILEMAP(      Type.TEXT, true,  "/tilemap.txt"),
    DATABASE_FILE(Type.TEXT, false, "/highscores.csv");


    public static enum Type {
        IMAGE, SOUND, FONT, TEXT
    }

    public String  getPath()    { return _path; }
    public Type    getType()    { return _type; }
    public boolean isCritical() { return _critical; }


    private ResourceID(Type type, boolean critical, String path) {
        _type = type; _critical = critical; _path = path; 
    }

    private final String _path;
    private final Type _type;
    private final boolean _critical;

}
