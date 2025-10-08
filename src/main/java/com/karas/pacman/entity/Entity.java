package com.karas.pacman.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.common.Direction;
import com.karas.pacman.common.Vector2;

public abstract class Entity {

    public void setNextDirection(Direction d) {
        _nextDirection = d;
    }

    public boolean isCollide(Entity other) {
        final float BOUND = Configs.SPRITE_SIZE_UI * 0.9f;
        Vector2 delta = _position.sub(other._position);
        return Math.abs(delta.x()) < BOUND && Math.abs(delta.y()) < BOUND;
    }
    
    public void repaint(Graphics2D g) {
        g.drawImage(
            _sprites[_spritePos], _position.x(), _position.y(), Configs.SPRITE_SIZE_UI, Configs.SPRITE_SIZE_UI, null
        );
    }

    public void update() {
        boolean directionChanged = false;
        if (_direction != _nextDirection && isValidNextDirection()) {
            _direction = _nextDirection;
            directionChanged = true;
        }

        ++_spriteCounter;
        if (directionChanged || _spriteCounter >= Configs.SPRITE_INTERVAL) {
            _spritePos = Direction.toSpritePos(_direction) + (_spritePos + 1) % 2;
            _spriteCounter = 0;
        }

        _position = _position.add(Direction.toVector2(_direction).mul(_speed));
    }

    // TODO
    private boolean isValidNextDirection() {
        return true;
    }
    
    protected int _speed;
    protected Direction _direction;
    protected Direction _nextDirection;
    protected Vector2 _position;
    protected int _spritePos;
    protected int _spriteCounter;
    protected BufferedImage[] _sprites;

}
