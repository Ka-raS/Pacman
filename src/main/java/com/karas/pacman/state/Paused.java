package com.karas.pacman.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Paused implements State {
    
    public Paused(State lastState) {
        _lastState = lastState;
        _nextState = null;
    }

    @Override
    public void enter() {
    }

    @Override
    public void exit() {
    }

    @Override
    public State update() {
        if (_nextState != null)
            return _nextState;

        return this;
    }

    @Override
    public void repaint(Graphics2D g) {
        _lastState.repaint(g);
    }

    @Override
    public void input(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED)
            return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                _nextState = _lastState;
                break;
        
            default:
                break;
        }
    }
    
    private State _lastState;
    private State _nextState;

}
