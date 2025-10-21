package com.karas.pacman.entity;

import com.karas.pacman.Configs;
import com.karas.pacman.common.Direction;
import com.karas.pacman.common.Vector2;
import com.karas.pacman.map.Map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Entity {

    public Entity(Vector2 gridPos, double speed, BufferedImage[] sprites, Direction direction) {
        _position = Map.toPixelVector2(gridPos);
        _speed = speed;
        _direction = _nextDirection = direction;

        _spriteTimer = 0;
        _sprites = sprites;
        _spritePos = Direction.toSpritePos(_direction);
    }

    public void setNextDirection(Direction d) {
        _nextDirection = d;
    }

    public Vector2 getPosition() {
        return _position;
    }

    public void repaint(Graphics2D g) {
        Vector2 p = _position.mul(Configs.SCALING);
        g.drawImage(_sprites[_spritePos], p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }

    public boolean isCollides(Entity other) {
        final double BOUND = Configs.PX.SPRITE_SIZE * 0.7;
        Vector2 delta = _position.sub(other._position);
        return Math.abs(delta.x()) < BOUND && Math.abs(delta.y()) < BOUND;
    }

    protected double _speed;
    protected Direction _direction;
    protected Direction _nextDirection;
    protected Vector2 _position;
    
    protected int _spritePos;
    protected double _spriteTimer;
    protected BufferedImage[] _sprites;

}
