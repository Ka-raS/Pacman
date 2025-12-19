package com.karas.pacman.resources;

import java.util.EnumSet;

import com.karas.pacman.Configs;

public enum ResourceID {
    
    WINDOW_ICON(       Configs.Path.WINDOW_ICON),
    TITLE_IMAGE(       Configs.Path.TITLE_IMAGE),
    HIGHSCORE_IMAGE(   Configs.Path.HIGHSCORE_IMAGE),
    MAP_IMAGE(         Configs.Path.MAP_IMAGE),
    SPRITE_SHEET(      Configs.Path.SPRITE_SHEET),
    TILEMAP(           Configs.Path.TILEMAP),
    FONT(              Configs.Path.FONT),
    DATABASE_FILE(     Configs.Path.DATABASE_FILE),

    EAT_WA_SOUND(      Configs.Path.EAT_WA_SOUND),
    EAT_KA_SOUND(      Configs.Path.EAT_KA_SOUND),
    PACMAN_DEATH_SOUND(Configs.Path.PACMAN_DEATH_SOUND),
    GHOST_DEATH_SOUND( Configs.Path.GHOST_DEATH_SOUND),
    GAME_START_SOUND(  Configs.Path.GAME_START_SOUND),
    GAME_NORMAL_SOUND( Configs.Path.GAME_NORMAL_SOUND),
    GAME_POWERUP_SOUND(Configs.Path.GAME_POWERUP_SOUND),
    GAME_WON_SOUND(    Configs.Path.GAME_WON_SOUND);

    public String getPath()     { return _path; }
    public boolean isCritical() { return _criticalResources.contains(this); }


    private static EnumSet<ResourceID> _criticalResources = EnumSet.of(
        MAP_IMAGE, SPRITE_SHEET, TILEMAP
    );

    private ResourceID(String path) {
        _path = path;
    }

    private final String _path;

}
