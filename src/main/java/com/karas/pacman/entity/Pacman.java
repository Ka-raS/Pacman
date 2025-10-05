package com.karas.pacman.entity;

import com.karas.pacman.Configs;
import com.karas.pacman.common.Direction;
import com.karas.pacman.common.Vector2;
import com.karas.pacman.graphics.ImageLoader;

public class Pacman extends Entity {

    public Pacman() {
        _position = START_POS;
        _speed = Configs.PACMAN_SPEED;
        _direction = _nextDirection = Direction.getRandom();
        _sprites = ImageLoader.getPacman();
        _spritePos = Direction.toSpritePos(_direction);
        _spriteCounter = 0;
        // _deathSprites = SpriteSheet.getPacmanDeath();
    }

    private static final Vector2 START_POS = new Vector2(128, 128);

    // private BufferedImage[] _deathSprites;

}
