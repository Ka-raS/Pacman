package com.karas.pacman.entities;

import java.awt.Graphics2D;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.SpriteSheet;
import com.karas.pacman.resources.Sprites;

public abstract class Ghost implements Entity {

    public boolean hasCaughtPacman() {
        return _hasCaughtPacman;
    }

    public void handlePowerupExpiring() {
        if (_state == Entity.State.PREY)
            _sprites.setFrameCount(4);
    }

    public void enterState(Entity.State nextState) {
        if ((_state == Entity.State.PREY && nextState == Entity.State.DEAD) ||
            (_state == Entity.State.DEAD && nextState == Entity.State.HUNTER))
            return;
        if (_state == Entity.State.IDLE && nextState != Entity.State.IDLE)
            nextState = _preIdleState;
        enterStateInternal(nextState);
    }

    @Override
    public void update(double deltaTime) {
        // TODO: in construction

        switch (_state) {
            case IDLE:
                break;

            case HUNTER:
                if (Map.isCenteredInTile(_position)) {
                    if (_nextDirections.isEmpty())
                        _nextDirections.addAll(findPathToPacman(_pacmanRef.getPosition()));
                    _direction = _nextDirections.poll();
                }
                _hasCaughtPacman = collidesWithPacman();
                break;
                
            case PREY:
                if (collidesWithPacman())
                    enterStateInternal(Entity.State.DEAD);
                break;

            case DEAD:
                break;
        }

        _sprites.update(deltaTime);
    }

    @Override
    public void repaint(Graphics2D g) {
        Vector2 p = _position.mul(Configs.SCALING);
        g.drawImage(_sprites.getFrame(), p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }

  
    protected Ghost(Vector2 position, Direction direction, double speed, SpriteSheet spriteName, Pacman pacmanRef, ImmutableMap mapRef) {
        _position = position;
        _direction = direction;
        _speed = speed;
        _nextDirections = new ArrayDeque<>(32);
        _hasCaughtPacman = false;
        
        _mapRef = mapRef;
        _pacmanRef = pacmanRef;
        _hunterSprites = new Sprites(spriteName, 0, 2);
        _preySprites = new Sprites(SpriteSheet.PREY_GHOST, 0, 4);
        _deadSprites = new Sprites(SpriteSheet.DEAD_GHOST, 0, 1);
        enterState(Entity.State.HUNTER);
    }

    protected abstract Collection<Direction> findPathToPacman(Vector2 pacmanPos);
    
    protected abstract Collection<Direction> findPathToRunaway(Vector2 pacmanPos);


    private void enterStateInternal(Entity.State nextState) {
        switch (nextState) {
            case IDLE:
                _preIdleState = _state;
                break;
            
            case HUNTER:
                _sprites = _hunterSprites;
                _sprites.setOffset(_direction.ordinal() * 2);
                break;

            case PREY:
                if (_state == Entity.State.DEAD)
                    return;
                _sprites = _preySprites;
                _sprites.setFrameCount(2);
                break;

            case DEAD:
                if (_state == Entity.State.PREY)
                    System.out.println(getClass().getSimpleName() + " eaten!");
                _sprites = _deadSprites;
                _sprites.setOffset(_direction.ordinal());
                findPathToHome();
                break;
        }
        _state = nextState;
    }

    private boolean collidesWithPacman() {
        final double BOUND = Configs.PX.SPRITE_SIZE * 0.8;
        Vector2 delta = _position.sub(_pacmanRef.getPosition()).abs();
        return delta.x() < BOUND && delta.y() < BOUND;
    }

    private void findPathToHome() {
        _nextDirections.clear();
        // TODO: A* algorithm shortest path to starting position
    }
    

    private final Pacman _pacmanRef;
    private final ImmutableMap _mapRef;
    private final Sprites _hunterSprites, _preySprites, _deadSprites;

    private Vector2 _position;
    private Direction _direction;
    private double _speed;
    private Entity.State _state, _preIdleState;
    private Queue<Direction> _nextDirections;
    private Sprites _sprites;
    private boolean _hasCaughtPacman;

}
