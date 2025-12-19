package com.karas.pacman.entities;

import java.awt.Graphics2D;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;

public abstract class Entity implements ImmutableEntity, Paintable {
    
    public static enum State {
        IDLE, HUNTER, PREY, DEAD;
    }

    public abstract void update(double deltaTime);

    public abstract void reset();

    public State getState() {
        return _state;
    }

    public void enterState(State nextState) {
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
        return _position.distance(other._position) < Configs.PX.SPRITE_SIZE * 0.7;
    }

    public void enterPreIdleState() {
        enterState(_preIdleState);
    }

    @Override
    public void repaint(Graphics2D G) {
        _Sprite.setPosition(_position);
        _Sprite.repaint(G);
    }


    protected Entity(Vector2 gridPosition, Direction direction, int speed, ImmutableMap MapRef) {
        _position = Map.toPixelVector2(gridPosition);
        _direction = direction;
        _speed = speed;
        _Map = MapRef;
    }

    protected abstract void handleStateTransition(State nextState);

    protected boolean isCenteredInTile() {
        return Map.isCenteredInTile(_position);
    }

    protected boolean canMoveInDirection(Direction direction) {
        return _Map.canMoveInDirection(_position, direction);
    }

    protected void setDirection(Direction direction) {
        _direction = direction;
        _Sprite.setDirection(_direction);
    }

    protected void setGridPosition(Vector2 gridPosition) {
        _position = Map.toPixelVector2(gridPosition);
    }

    protected void move(double deltaTime) {
        if (_Map.canMoveInDirection(_position, _direction))
            _position = _position.add(_direction.toVector2().mul(deltaTime * _speed));
        
        Vector2 tunneled = _Map.tryTunneling(_position, _direction);
        if (tunneled != null)
            _position = tunneled;
    }

    protected void setSpeed(int speed) {
        _speed = speed;
    }

    protected void updateSprite(double deltaTime) {
        _Sprite.update(deltaTime);
    }

    protected void setSprite(Sprite SpriteRef) {
        _Sprite = SpriteRef;
        _Sprite.setDirection(_direction);
    }


    private final ImmutableMap _Map;
    private Sprite _Sprite;

    private int _speed;
    private Vector2 _position;
    private Direction _direction;
    private State _state, _preIdleState;

}
