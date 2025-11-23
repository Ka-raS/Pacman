package com.karas.pacman.maps;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Vector2;

public class Score {
    
    public Score(BufferedImage sprite) {
        _sprite = sprite;
        _duration = 0.0;
        _position = null;
    }

    public void setDisplay(Vector2 position) {
        if (position == null)
            return;
        _position = position;
        _duration = Configs.Time.SCORE_DURATION;
    }

    public void update(double deltaTime) {
        if (_duration > 0.0)
            _duration -= deltaTime;
    }

    public void repaint(Graphics2D g) {
        if (_duration <= 0.0)
            return;

        Vector2 p = _position.mul(Configs.SCALING);
        g.drawImage(_sprite, p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }


    private BufferedImage _sprite;
    private double _duration;
    private Vector2 _position;

}
