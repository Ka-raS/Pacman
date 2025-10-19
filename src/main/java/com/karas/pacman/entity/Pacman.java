package com.karas.pacman.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.common.Direction;
import com.karas.pacman.common.Vector2;
import com.karas.pacman.map.Map;
import com.karas.pacman.map.Tile;
import com.karas.pacman.resources.ImageLoader;

public class Pacman extends Entity {

    public Pacman() {
        _position = Map.toPixelVector2(Configs.PACMAN_POS_GRID);
        _speed = Configs.PACMAN_SPEED_PX;
        _direction = _nextDirection = Direction.RIGHT;

        _sprites = ImageLoader.getPacman();
        _spritePos = Direction.toSpritePos(_direction);
        _spriteTimer = 0;
        // _deathSprites = SpriteSheet.getPacmanDeath();
    }

    public void update(double deltaTime, Map map) {
        boolean directionChanged = false;
        if (_direction != _nextDirection && Map.isCenteredInTile(_position)) {
            _direction = _nextDirection;
            directionChanged = true;
        }

        if (!willHitWall(map))
            _position = _position.add(Direction.toVector2(_direction).mul(deltaTime * _speed));

        _spriteTimer += deltaTime;
        if (directionChanged || _spriteTimer > Configs.SPRITE_INTERVAL) {
            _spritePos = Direction.toSpritePos(_direction) + (_spritePos + 1) % 2;
            _spriteTimer = 0;
        }
    }

    public void repaint(Graphics2D g) {
        Vector2 p = _position.mul(Configs.SCALING);
        g.drawImage(_sprites[_spritePos], p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }

    public void setNextDirection(Direction d) {
        _nextDirection = d;
    }

    private boolean willHitWall(Map map) {
        Vector2 delta = Direction.toVector2(_direction).mul(Configs.PX.TILE_SIZE / 2);
        Vector2 testPos = _position.add(delta);
        if (_direction == Direction.LEFT || _direction == Direction.UP)
            testPos = testPos.sub(1); // TODO: ??????????
        return map.getTile(testPos) == Tile.WALL;
    }

    // public boolean isCollide(Entity other) {
    //     final double BOUND = Configs.PIXEL.SPRITE_SIZE * 0.9f;
    //     Vector2 delta = _position.sub(other._position);
    //     return Math.abs(delta.x()) < BOUND && Math.abs(delta.y()) < BOUND;
    // }

    private double _speed;
    private Direction _direction;
    private Direction _nextDirection;
    private Vector2 _position;
    private int _spritePos;
    private double _spriteTimer;
    private BufferedImage[] _sprites;
    // private BufferedImage[] _deathSprites;

}
