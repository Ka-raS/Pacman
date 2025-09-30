package com.karas.pacman.entity;

import java.util.concurrent.ThreadLocalRandom;

import com.karas.pacman.Configs;
import com.karas.pacman.common.Vector2;
import com.karas.pacman.graphics.ImageLoader;

public class Pacman extends Entity {

    public Pacman() {
        super(
            START_POS, Configs.PACMAN_SPEED, ThreadLocalRandom.current().nextInt(4), ImageLoader.getPacman()
        );
        // _deathSprites = SpriteSheet.getPacmanDeath();
    }

    private static final Vector2 START_POS = new Vector2(128, 128);

    // private BufferedImage[] _deathSprites;

}
