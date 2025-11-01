package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Paused implements Screen {
    
    public Paused(Screen lastScreen) {
        _lastScreen = lastScreen;
        _nextScreen = this;
    }

    @Override
    public void enter() {
    }

    @Override
    public Screen update(double deltaTime) {
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D g) {
        _lastScreen.repaint(g);
    }

    @Override
    public void input(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getID() == KeyEvent.KEY_PRESSED)
            _nextScreen = _lastScreen;
    }
    
    
    private Screen _lastScreen;
    private volatile Screen _nextScreen;

}
