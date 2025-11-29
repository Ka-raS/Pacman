package com.karas.pacman.resources;

import com.karas.pacman.Configs;

public enum Resource {
    
    WINDOW_ICON(  Configs.Path.WINDOW_ICON,   false),
    TITLE_IMAGE(  Configs.Path.TITLE_IMAGE,   false),
    MAP_IMAGE(    Configs.Path.MAP_IMAGE,     true),
    SPRITE_SHEET( Configs.Path.SPRITE_SHEET,  true),
    TILEMAP(      Configs.Path.TILEMAP,       true),
    FONT(         Configs.Path.FONT,          false),
    DATABASE_FILE(Configs.Path.DATABASE_FILE, false),

    EAT_WA_SOUND(      Configs.Path.EAT_WA_SOUND,       false),
    EAT_KA_SOUND(      Configs.Path.EAT_KA_SOUND,       false),
    PACMAN_DEATH_SOUND(Configs.Path.PACMAN_DEATH_SOUND, false),
    GHOST_DEATH_SOUND( Configs.Path.GHOST_DEATH_SOUND,  false),
    GAME_START_SOUND(  Configs.Path.GAME_START_SOUND,   false),
    GAME_NORMAL_SOUND( Configs.Path.GAME_NORMAL_SOUND,  false),
    GAME_POWERUP_SOUND(Configs.Path.GAME_POWERUP_SOUND, false);

    public String getPath()     { return _path; }
    public boolean isCritical() { return _isCritical; }


    private Resource(String path, boolean isCritical) {
        _path = path;
        _isCritical = isCritical;
    }

    private final String _path;
    private final boolean _isCritical;

}
