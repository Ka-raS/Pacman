package com.karas.pacman.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.karas.pacman.common.Direction;
import com.karas.pacman.entity.GhostGang;
import com.karas.pacman.entity.Pacman;
import com.karas.pacman.map.Map;
import com.karas.pacman.map.Tile;

public class Playing implements State {

    public Playing() {
        _map = new Map();
        _pacman = new Pacman();
        _ghostGang = new GhostGang();
    }

    @Override
    public void enter() {
        _nextState = this;
        _idleTimer = 1.0;
    }

    @Override
    public void exit() {
    }

    @Override
    public State update(double deltaTime) {
        if (_nextState != this)
            return _nextState;
        
        boolean isIdling = _idleTimer > 0.0;
        if (isIdling)
            _idleTimer -= deltaTime;
        _pacman.update(deltaTime, _map, isIdling);
        _ghostGang.update(deltaTime, _map, isIdling);
        
        if (_ghostGang.caughtPacman(_pacman)) {
            System.out.println("Game lost!");
            return null;
        }

        Tile eaten = _map.tryEatAt(_pacman.getPosition());
        if (eaten == Tile.POWERUP)
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
        _ghostGang.repaint(g);
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

    private Map _map;
    private Pacman _pacman;
    private GhostGang _ghostGang;
    private double _idleTimer;
    private volatile State _nextState;

}
