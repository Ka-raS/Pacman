package com.karas.pacman.resources;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;

public enum SpriteSheet {
        
    PACMAN(0, 0, 8), DEAD_PACMAN(1, 0, 8), 
    BLINKY(2, 0, 8), PREY_GHOST( 6, 0, 4),
    PINKY( 3, 0, 8), DEAD_GHOST( 6, 4, 4),
    INKY(  4, 0, 8), SCORES(     7, 0, 4),
    CLYDE( 5, 0, 8);

    public BufferedImage[] getSprites() {
        final int SIZE = Configs.PX.SPRITE_SIZE;
        BufferedImage[] sprites = new BufferedImage[_len];
        for (int i = 0; i < _len; ++i)
            sprites[i] = SPRITE_SHEET.getSubimage((_col + i) * SIZE, _row * SIZE, SIZE, SIZE);
        return sprites;
    }


    private static final BufferedImage SPRITE_SHEET = ResourcesLoader.loadImage(Configs.SPRITE_SHEET_PATH, true);

    private SpriteSheet(int row, int col, int len) {
        _row = row; _col = col; _len = len;
    }

    private final int _row, _col, _len;

}