package com.karas.pacman.entity;

import com.karas.pacman.Configs;
import com.karas.pacman.common.Direction;
import com.karas.pacman.common.Vector2;
import com.karas.pacman.map.Map;
import com.karas.pacman.resources.ImageLoader;

public class Pacman extends Entity {

    public Pacman() {
        super(Configs.GRID.PACMAN_POS, Configs.PX.PACMAN_SPEED, ImageLoader.getPacman(), Direction.RIGHT);
        // _deathSprites = SpriteSheet.getPacmanDeath();
    }

    public void update(double deltaTime, Map map, boolean isIdling) {
        boolean changeDirection = _direction != _nextDirection && !map.willHitWall(_position, _nextDirection) && Map.isCenteredInTile(_position);
        if (changeDirection)
            _direction = _nextDirection;

        if (!isIdling && !map.willHitWall(_position, _direction)) {
            Vector2 delta = Direction.toVector2(_direction).mul(deltaTime * _speed);
            _position = _position.add(delta);
        }

        _spriteTimer += deltaTime;
        if (changeDirection || _spriteTimer > Configs.SPRITE_INTERVAL) {
            _spritePos = Direction.toSpritePos(_direction) + (_spritePos + 1) % 2;
            _spriteTimer = 0;
        }
    }

    // private BufferedImage[] _deathSprites;

}
