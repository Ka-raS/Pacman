package com.karas.pacman.resources;

import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;

public class Sprites {
    
    public Sprites(SpriteSheet name, int offset, int frameCount) {
        _timer = _index = 0;
        _offset = offset;
        _images = name.getSprites();
        _frameCount = frameCount;
    }

    public BufferedImage getFrame() {
        return _images[_index + _offset];
    }

    public void setOffset(int offset) {
        if (0 <= offset && offset <= _images.length - _frameCount)
            _offset = offset;
    }

    public void setFrameCount(int frameCount) {
        if (0 < frameCount && frameCount <= _images.length)
            _frameCount = frameCount;
    }

    public void update(double deltaTime) {
        _timer += deltaTime;
        if (_timer > Configs.SPRITE_INTERVAL) {
            _timer = 0.0;
            _index = (_index + 1) % _frameCount;
        }
    }

    private double _timer;
    private BufferedImage[] _images;
    private int _index, _offset, _frameCount;

}
