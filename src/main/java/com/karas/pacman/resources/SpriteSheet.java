package com.karas.pacman.resources;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;

public enum SpriteSheet {
        
    PACMAN(0, 0, 8), DEAD_PACMAN(1, 0, 8), 
    BLINKY(2, 0, 8), PREY_GHOST( 6, 0, 4),
    PINKY( 3, 0, 8), DEAD_GHOST( 6, 4, 4),
    INKY(  4, 0, 8), SCORES(     7, 0, 4),
    CLYDE( 5, 0, 8);

    BufferedImage[] getSprites() {
        return SPRITE_MAP[this.ordinal()];
    }

    
    private static final BufferedImage[][] SPRITE_MAP;

    static {
        final int SIZE = Configs.PX.SPRITE_SIZE;
        BufferedImage SpriteSheetImage = ResourcesLoader.loadImage(Configs.SPRITE_SHEET_PATH, true);
        SPRITE_MAP = new BufferedImage[values().length][];

        for (SpriteSheet sheet : values()) {
            SPRITE_MAP[sheet.ordinal()] = new BufferedImage[sheet._len];
            for (int i = 0; i < sheet._len; ++i)
                SPRITE_MAP[sheet.ordinal()][i] = SpriteSheetImage.getSubimage((sheet._col + i) * SIZE, sheet._row * SIZE, SIZE, SIZE);
        }
    }

    private SpriteSheet(int row, int col, int len) {
        _row = row; _col = col; _len = len;
    }

    private final int _row, _col, _len;

}