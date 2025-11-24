package com.karas.pacman.entities;

import java.awt.Graphics2D;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.Sprite;

public abstract class Entity implements ImmutableEntity {
    
    public static enum State {
        IDLE, HUNTER, PREY, DEAD;
    }

    public abstract void update(double deltaTime);

    public abstract void reset();

    public State getState() {
        return _state;
    }

    public void enterState(State nextState) {
        if (nextState == null)
            return;

        handleStateTransition(nextState);
        if (_state != State.IDLE)
            _preIdleState = _state;
        _state = nextState;
    }

    @Override
    public Vector2 getPosition() {
        return _position;
    } 

    @Override
    public Direction getDirection() {
        return _direction;
    }

    @Override
    public Vector2 getGridPosition() {
        return Map.toGridVector2(_position);
    }

    public boolean collidesWith(Entity other) {
        double delta = _position.distance(other._position);
        return delta < Configs.PX.SPRITE_SIZE * 0.7;
    }

    public void enterPreIdleState() {
        enterState(_preIdleState);
    }

    public void repaint(Graphics2D g) {
        Vector2 p = _position.mul(Configs.SCALING);
        g.drawImage(_sprite.getFrame(), p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }


    protected Entity(Vector2 gridPosition, Direction direction, int speed, Sprite sprite, ImmutableMap map) {
        _position = Map.toPixelVector2(gridPosition);
        _direction = direction;
        _speed = speed;
        _sprite = sprite;
        _Map = map;
    }

    protected abstract void handleStateTransition(State nextState);

    protected boolean isCenteredInTile() {
        return Map.isCenteredInTile(_position);
    }

    protected boolean isValidDirection(Direction d) {
        return _Map.isValidDirection(_position, d);
    }

    protected void setDirection(Direction d) {
        if (_direction != d && isValidDirection(d))
            _direction = d;
    }

    protected void setGridPosition(Vector2 gridPosition) {
        if (_Map.isNotWall(gridPosition))
            _position = Map.toPixelVector2(gridPosition);
    }

    protected void move(double deltaTime) {
        if (isValidDirection(_direction))
            _position = _position.add(_direction.toVector2().mul(deltaTime * _speed));
        
        Vector2 tunneled = _Map.tryTunneling(_position, _direction);
        if (tunneled != null)
            _position = tunneled;
    }

    protected void setSpeed(int speed) {
        if (speed >= 0)
            _speed = speed;
    }

    protected Sprite getSprite() {
        return _sprite;
    }

    protected void setSprite(Sprite sprite) {
        if (sprite != null)
            _sprite = sprite;
    }


    private final ImmutableMap _Map;
    private int _speed;
    private Vector2 _position;
    private Direction _direction;
    private State _state, _preIdleState;
    private Sprite _sprite;

}
