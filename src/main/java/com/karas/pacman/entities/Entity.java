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
        HUNTER, PREY, DEAD;
    }

    public abstract void enterState(State nextState);

    public abstract void update(double deltaTime);

    public abstract void reset();

    @Override
    public Vector2 getPosition() {
        return _position;
    } 

    @Override
    public Direction getDirection() {
        return _direction;
    }

    @Override
    public Vector2 getGridPos() {
        return Map.toGridVector2(_position);
    }

    @Override
    public boolean collidesWith(ImmutableEntity other) {
        double delta = _position.distance(other.getPosition());
        return delta < Configs.PX.SPRITE_SIZE * 0.7;
    }

    public void setIdle(boolean isIdle) {
        _isIdling = isIdle;
    }

    public void repaint(Graphics2D g) {
        Vector2 p = _position.mul(Configs.SCALING);
        g.drawImage(_sprite.getFrame(), p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }


    protected Entity(Vector2 position, Direction direction, int speed, Sprite sprite, ImmutableMap map) {
        _position = position;
        _direction = direction;
        _speed = speed;
        _sprite = sprite;
        _isIdling = true;
        _Map = map;
    }

    protected boolean isIdle() {
        return _isIdling;
    }

    protected boolean isCenteredInTile() {
        return Map.isCenteredInTile(_position);
    }

    protected boolean isValidDirection(Direction d) {
        return _Map.isValidDirection(_position, d);
    }

    protected Entity.State getState() {
        return _state;
    }

    protected void setState(Entity.State state) {
        if (state != null)
            _state = state;
    }

    protected void setDirection(Direction d) {
        if (_direction != d && isValidDirection(d))
            _direction = d;
    }

    protected void setPosition(Vector2 p) {
        if (_Map.isNotWall(Map.toGridVector2(p)))
            _position = p;
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
    private Entity.State _state;
    private Sprite _sprite;
    private boolean _isIdling;

}
