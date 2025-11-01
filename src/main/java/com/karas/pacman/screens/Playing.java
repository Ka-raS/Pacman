package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.Entity;
import com.karas.pacman.entities.Pacman;
import com.karas.pacman.entities.ghosts.Blinky;
import com.karas.pacman.maps.Map;
import com.karas.pacman.maps.Tile;

public class Playing implements Screen {

    public Playing() {
        _map = new Map();
        _pacman = new Pacman(_map);
        _ghosts = List.of(new Blinky(_pacman, _map));
        _state = State.IDLE;
        _preIdleState = State.NORMAL;
    }

    @Override
    public void enter() {
        _nextScreen = this;
        enterState(State.IDLE);
    }

    @Override
    public Screen update(double deltaTime) {
        _pacman.update(deltaTime);
        _ghosts.forEach(ghost -> ghost.update(deltaTime));
        updateState(deltaTime);
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D g) {
        _map.repaint(g);
        _pacman.repaint(g);
        _ghosts.forEach(ghost -> ghost.repaint(g));
    }

    @Override
    public void input(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED)
            return;

        Direction d = null;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP,    KeyEvent.VK_W -> d = Direction.UP;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> d = Direction.RIGHT;
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> d = Direction.DOWN;
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> d = Direction.LEFT;

            case KeyEvent.VK_ESCAPE -> {
                if (_state != State.LOST && _state != State.WON)
                    _nextScreen = new Paused(this);
            }
        }
        if (d != null)
            _pacman.setNextDirection(d);
    }

    
    private enum State {
        IDLE, NORMAL, POWERUP, LOST, WON
    }

    private void enterState(State nextState) {
        switch (nextState) {
            case IDLE:
                _stateTimer = Configs.IDLE_DURATION;
                if (_state != State.IDLE)
                    _preIdleState = _state;
                _pacman.enterState(Pacman.State.IDLE);
                _ghosts.forEach(ghost -> ghost.enterState(Entity.State.IDLE));
                break;

            case NORMAL:
                _pacman.enterState(Pacman.State.PREY);
                _ghosts.forEach(ghost -> ghost.enterState(Entity.State.HUNTER));
                break;

            case POWERUP:
                System.out.println("Powerup eaten!");
                _stateTimer = Configs.POWERUP_DURATION;
                _pacman.enterState(Pacman.State.HUNTER);
                _ghosts.forEach(ghost -> ghost.enterState(Entity.State.PREY));
                break;

            case LOST:
                System.out.println("Game lost!");
                _stateTimer = Configs.ENDGAME_DURATION;
                _pacman.enterState(Pacman.State.DEAD);
                _ghosts.forEach(ghost -> ghost.enterState(Entity.State.IDLE));
                break;

            case WON:
                System.out.println("Game won!");
                _stateTimer = Configs.ENDGAME_DURATION;
                _pacman.enterState(Pacman.State.IDLE);
                _ghosts.forEach(ghost -> ghost.enterState(Entity.State.IDLE));
                break;
        }
        _state = nextState;
    }

    private void updateState(double deltaTime) {
        if (_stateTimer > 0.0)
            _stateTimer -= deltaTime;

            switch (_state) {
            case IDLE:
                if (_stateTimer <= 0.0)
                    enterState(_preIdleState);
                break;

            case POWERUP:
                if (_stateTimer <= 0.0)
                    enterState(State.NORMAL);
                else if (_stateTimer < Configs.GHOST_FLASH_TIME)    // TODO: not the best tbh
                    _ghosts.forEach(Ghost::handlePowerupExpiring);

            case NORMAL:
                if (_ghosts.stream().anyMatch(Ghost::hasCaughtPacman))
                    enterState(State.LOST);
                else if (_map.getDotCounts() == 0)
                    enterState(State.WON);
                else if (_map.tryEatAt(_pacman.getPosition()) == Tile.POWERUP)
                    enterState(State.POWERUP);
                break;

            case LOST, WON:
                if (_stateTimer < 0.0)
                    _nextScreen = null; // TODO: _nextScreen = new GameOver();
                break;
        }
    }

    private Map _map;
    private Pacman _pacman;
    private List<Ghost> _ghosts;
    private volatile State _state;
    private State _preIdleState;
    private double _stateTimer;
    private volatile Screen _nextScreen;

}
