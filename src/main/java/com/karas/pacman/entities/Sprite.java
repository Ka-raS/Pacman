package com.karas.pacman.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;

public class Sprite implements Paintable {
    
    public Sprite(BufferedImage[] Images) {
        _Images = Images;
        _length = Images.length;
        _timer  = _index = _offset = 0;
        _position = new Vector2(0, 0);
    }

    public Sprite(BufferedImage[] Images, Direction direction) {
        _Images = Images;
        _length = Images.length / Direction.values().length;
        _timer  = _index = 0;
        _offset = direction.ordinal() * _length;
        _position = new Vector2(0, 0);
    }

    public boolean isAnimationEnded() {
        return _index == _length - 1;
    }

    public void reset() {
        _timer = _index = 0;
    }

    public void setDirection(Direction direction) {
        if (_length != _Images.length)
            _offset = direction.ordinal() * _length;
    }

    public void setPosition(Vector2 position) {
        _position = position;
    }

    public void update(double deltaTime) {
        _timer += deltaTime;
        if (_timer >= Configs.Time.SPRITE_INTERVAL) {
            _timer = 0.0;
            _index = (_index + 1) % _length;
        }
    }

    @Override
    public void repaint(Graphics2D G) {
        G.drawImage(_Images[_index + _offset], _position.ix(), _position.iy(), null);
    }


    private final BufferedImage[] _Images;

    private double _timer;
    private Vector2 _position;
    private int _index, _offset;
    private final int _length;

}
