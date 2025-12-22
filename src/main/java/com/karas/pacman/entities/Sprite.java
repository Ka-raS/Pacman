package com.karas.pacman.entities;

import java.awt.image.BufferedImage;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;

public final class Sprite {
    
    public Sprite(BufferedImage[] Images) {
        _Images = Images;
        _length = Images.length;
        _timer  = _index = _offset = 0;
    }

    public Sprite(BufferedImage[] Images, Direction direction) {
        _Images = Images;
        _length = Images.length / Direction.values().length;
        _timer  = _index = 0;
        _offset = direction.ordinal() * _length;
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

    public BufferedImage getFrame() {
        return _Images[_index + _offset];
    }

    public void update(double deltaTime) {
        _timer += deltaTime;
        if (_timer >= Constants.Time.SPRITE_INTERVAL) {
            _timer = 0.0;
            _index = (_index + 1) % _length;
        }
    }


    private final BufferedImage[] _Images;

    private final int _length;
    private double _timer;
    private int _index, _offset;

}
