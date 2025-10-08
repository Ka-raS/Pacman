package com.karas.pacman.resources;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.karas.pacman.Configs;

public class ImageLoader {

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

    public static Image getWindowIcon() {
        return WINDOW_ICON;
    }

    public static BufferedImage getMap() {
        return GAME_MAP;
    }

    private static BufferedImage[] getSprites(int row, int col, int count) {
        BufferedImage[] sprites = new BufferedImage[count];
        for (int i = 0; i < count; ++i)
            sprites[i] = SPRITE_SHEET.getSubimage(
                (col + i) * Configs.SPRITE_SIZE_PX, row * Configs.SPRITE_SIZE_PX, Configs.SPRITE_SIZE_PX, Configs.SPRITE_SIZE_PX
            );
        return sprites;
    }

    private static BufferedImage loadImage(InputStream stream, boolean exitFail) {
        BufferedImage result = null;
        try {
            result = ImageIO.read(stream);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Load image failed: " + e.getMessage());
            if (exitFail)
                System.exit(1);
        }
        return result;
    }

    private static final BufferedImage WINDOW_ICON = loadImage(Configs.WINDOW_ICON_PATH, false);
    private static final BufferedImage GAME_MAP = loadImage(Configs.MAP_PATH, true);
    private static final BufferedImage SPRITE_SHEET = loadImage(Configs.SPRITE_SHEET_PATH, true);

}
