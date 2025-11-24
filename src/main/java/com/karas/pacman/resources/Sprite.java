package com.karas.pacman.resources;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;

public class Sprite implements Paintable {
    
    public Sprite(BufferedImage[] Images, int offset, int frameCount) {
        _Images = Images;
        _timer = _index = 0;
        setOffset(offset);
        _frameCount = Images.length - _offset;
        setFrameCount(frameCount);
        _position = new Vector2(0, 0);
    }

    public boolean isAnimationEnded() {
        return _index == _frameCount - 1;
    }

    public void setOffset(int offset) {
        if (0 <= offset && offset <= _Images.length)
            _offset = offset;
    }

    public void setFrameCount(int frameCount) {
        if (0 < frameCount && frameCount <= _Images.length - _offset)
            _frameCount = frameCount;
    }

    public void setPosition(Vector2 position) {
        if (position != null)
            _position = position;
    }

    public void update(double deltaTime) {
        _timer += deltaTime;
        if (_timer > Configs.Time.SPRITE_INTERVAL) {
            _timer = 0.0;
            _index = (_index + 1) % _frameCount;
        }
    }

    @Override
    public void repaint(Graphics2D G) {
        Vector2 p = _position.mul(Configs.SCALING);
        G.drawImage(_Images[_index + _offset], p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }


    private final BufferedImage[] _Images;

    private double _timer;
    private int _index, _offset, _frameCount;
    private Vector2 _position;

}
