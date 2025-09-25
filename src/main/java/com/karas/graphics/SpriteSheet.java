package com.karas.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.karas.pacman.Configs;

public final class SpriteSheet {

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
        BufferedImage[] sprites = new BufferedImage[count];
        for (int i = 0; i < count; ++i)
            sprites[i] = m_spriteSheet.getSubimage(
                (col + i) * Configs.SPRITE_SIZE_PX, row * Configs.SPRITE_SIZE_PX, Configs.SPRITE_SIZE_PX, Configs.SPRITE_SIZE_PX
            );
        return sprites;
    }

    private SpriteSheet() {};

    private static final BufferedImage m_spriteSheet;

    static {
        BufferedImage result = null;
        try {
            result = javax.imageio.ImageIO.read(SpriteSheet.class.getResourceAsStream("/spritesheet.png"));
        } catch (IOException e) {
            System.err.println("Load spritesheet failed: " + e.getMessage());
            System.exit(1);
        }
        m_spriteSheet = result;
    }
}
