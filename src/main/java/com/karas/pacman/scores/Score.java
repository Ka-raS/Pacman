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
        if (_duration <= 0.0)
            return;

        Vector2 p = _position.mul(Configs.SCALING);
        G.drawImage(_Image, p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }


    private final BufferedImage _Image;

    private double _duration;
    private Vector2 _position;

}
