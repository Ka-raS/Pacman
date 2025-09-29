package com.karas.pacman.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.common.Directions;
import com.karas.common.Vector2;
import com.karas.pacman.Configs;

public abstract class Entity {

    public Entity(Vector2 position, int speed, int direction, BufferedImage[] sprites) {
        _position = position;
        _speed = speed;
        _direction = _nextDirection = direction;
        _sprites = sprites;
        _spritePos = Directions.SPRITES[_direction];
        _spriteCounter = 0;
    }

    public void setNextDirection(int d) {
        _nextDirection = d;
    }

    public boolean isCollide(Entity other) {
        final float BOUND = Configs.SPRITE_SIZE * 0.9f;
        Vector2 v = _position.sub(other._position);
        return Math.abs(v.getX()) < BOUND && Math.abs(v.getY()) < BOUND;
    }
    
    public void repaint(Graphics2D g) {
        g.drawImage(
            _sprites[_spritePos], _position.getX(), _position.getY(), Configs.SPRITE_SIZE, Configs.SPRITE_SIZE, null
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
            _spritePos = Directions.SPRITES[_direction] + (_spritePos + 1) % 2;
            _spriteCounter = 0;
        }

        _position = _position.add(Directions.VECTORS[_direction].mul(_speed));
    }

    // TODO
    private boolean isValidNextDirection() {
        return true;
    }

    private int _speed;
    private int _direction;
    private int _nextDirection;
    private Vector2 _position;
    private int _spritePos;
    private int _spriteCounter;
    private BufferedImage[] _sprites;

}
