package com.karas.pacman.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.karas.pacman.common.Direction;
import com.karas.pacman.entity.Pacman;

public class Playing implements State {

    public Playing() {
        _pacman = new Pacman();
    }

    @Override
    public void enter() {
    }

    @Override
    public void exit() {
    }

    @Override
    public State update() {
        _pacman.update();
        return this;
    }

    @Override
    public void repaint(Graphics2D g) {
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
            default:
                break;
        }

        if (d != null) {
            _pacman.setNextDirection(d);
        }
    }

    Pacman _pacman;

}
