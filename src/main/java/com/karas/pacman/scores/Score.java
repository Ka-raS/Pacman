package com.karas.pacman.scores;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;

public class Score implements Paintable {
    
    public Score(BufferedImage image) {
        _Image = image;
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

    @Override
    public void repaint(Graphics2D G) {
        if (_duration > 0.0)
            G.drawImage(_Image, _position.ix(), _position.iy(), null);
    }


    private final BufferedImage _Image;

    private double _duration;
    private Vector2 _position;

}
