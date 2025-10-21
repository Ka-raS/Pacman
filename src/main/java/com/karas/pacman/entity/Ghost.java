package com.karas.pacman.entity;

import java.awt.image.BufferedImage;

import com.karas.pacman.common.Direction;
import com.karas.pacman.common.Vector2;

public class Ghost extends Entity {

    public Ghost(Vector2 gridPos, double speed, BufferedImage[] sprites, Direction direction) {
        super(gridPos, speed, sprites, direction);
    }

    // private BufferedImage[] _scaredSprites;

}
