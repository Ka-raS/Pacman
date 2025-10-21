package com.karas.pacman.resources;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.karas.pacman.Configs;
import com.karas.pacman.common.ExceptionHandler;

public class ImageLoader {

    public static Image getWindowIcon() {
        return WINDOW_ICON;
    }

    public static BufferedImage getMap() {
        return GAME_MAP;
    }

    public static BufferedImage[] getPacman() {
        return getSprites(0, 0, 8);
    }

    public static BufferedImage[] getPacmanDeath() {
        return getSprites(1, 0, 8);
    }
    
    public static BufferedImage[] getBlinky() {
        return getSprites(2, 0, 8);
    }
    
    public static BufferedImage[] getPinky() {
        return getSprites(3, 0, 8);
    }

    public static BufferedImage[] getInky() {
        return getSprites(4, 0, 8);
    }

    public static BufferedImage[] getClyde() {
        return getSprites(5, 0, 8);
    }

    private static BufferedImage[] getSprites(int row, int col, int count) {
        final int CELL = Configs.PX.SPRITE_SIZE;
        BufferedImage[] sprites = new BufferedImage[count];
        for (int i = 0; i < count; ++i)
            sprites[i] = SPRITE_SHEET.getSubimage((col + i) * CELL, row * CELL, CELL, CELL);
        return sprites;
    }

    private static BufferedImage loadImage(String path, boolean isCritical) {
        BufferedImage result = null;
        try {
            result = ImageIO.read(ImageLoader.class.getResourceAsStream(path));
        } catch (IOException e) {
            if (isCritical)
                ExceptionHandler.handleCritical(e, path + " Load Failed");
            else
                ExceptionHandler.handleGeneric(e, path + " Load Failed");
        }
        return result;
    }

    private static final BufferedImage WINDOW_ICON = loadImage(Configs.WINDOW_ICON_PATH, false);
    private static final BufferedImage GAME_MAP = loadImage(Configs.MAP_PATH, true);
    private static final BufferedImage SPRITE_SHEET = loadImage(Configs.SPRITE_SHEET_PATH, true);

}
