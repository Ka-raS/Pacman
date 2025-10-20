package com.karas.pacman.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.karas.pacman.common.Direction;
import com.karas.pacman.entity.Pacman;
import com.karas.pacman.map.Map;

public class Playing implements State {

    public Playing() {
        _map = new Map();
        _pacman = new Pacman();
    }

    @Override
    public void enter() {
        _nextState = null;
    }

    @Override
    public void exit() {
    }

    @Override
    public State update(double deltaTime) {
        if (_nextState != null)
            return _nextState;
        _pacman.update(deltaTime, _map);

        _map.tryEatDotAt(_pacman.getPosition());
        if (_map.tryEatPowerupAt(_pacman.getPosition()))
            System.out.println("Powerup eaten!");
        
        if (_map.getDotCounts() == 0) {
            System.out.println("Game won!");
            return null;
        }
        return this;
    }

    @Override
    public void repaint(Graphics2D g) {
        _map.repaint(g);
        _pacman.repaint(g);
    }

    @Override
    public void input(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED)
            return;

        Direction d = null;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                d = Direction.UP;
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                d = Direction.RIGHT;
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                d = Direction.DOWN;
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                d = Direction.LEFT;
                break;

            case KeyEvent.VK_ESCAPE:
                _nextState = new Paused(this);
                break;

            default:
                break;
        }

        if (d != null)
            _pacman.setNextDirection(d);
    }

    private State _nextState;
    private Map _map;
    private Pacman _pacman;

}
