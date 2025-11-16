package com.karas.pacman.entities;

import java.awt.Graphics2D;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.Sprites;

public abstract class Entity implements ImmutableEntity {
    
    public static enum State {
        HUNTER, PREY, DEAD;
    }

    public abstract void enterState(State nextState);

    public abstract void update(double deltaTime);

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
        final double BOUND = Configs.PX.SPRITE_SIZE * 0.65;
        Vector2 delta = _position.sub(other.getPosition()).abs();
        return delta.x() < BOUND && delta.y() < BOUND;
    }

    public void toggleIdle() {
        _isIdle = !_isIdle;
    }

    public void repaint(Graphics2D g) {
        Vector2 p = _position.mul(Configs.SCALING);
        g.drawImage(_sprites.getFrame(), p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }


    protected Entity(Vector2 position, Direction direction, double speed, Sprites sprites, ImmutableMap map) {
        _position = position;
        _direction = direction;
        _speed = speed;
        _sprites = sprites;
        _isIdle = false;
        _Map = map;
    }

    protected boolean isIdle() {
        return _isIdle;
    }

    protected boolean isCenteredInTile() {
        return Map.isCenteredInTile(_position);
    }

    protected boolean validDirection(Direction d) {
        return _Map.validDirection(_position, d);
    }

    protected Vector2 getTunnelExit() {
        return _Map.getTunnelExit(_position);
    }

    protected Entity.State getState() {
        return _state;
    }

    protected void setState(Entity.State state) {
        if (state != null)
            _state = state;
    }

    protected void setDirection(Direction d) {
        if (d != null && _direction != d && validDirection(d))
            _direction = d;
    }

    protected void setPosition(Vector2 p) {
        if (_Map.checkWall(Map.toGridVector2(p)))
            _position = p;
    }

    protected void move(double deltaTime) {
        if (validDirection(_direction))
            _position = _position.add(_direction.toVector2().mul(deltaTime * _speed));
    }

    protected void setSprites(Sprites sprites) {
        if (sprites != null)
            _sprites = sprites;
    }

    protected void setSpritesOffset(int offset) {
        _sprites.setOffset(offset);
    }

    protected void setSpritesFrameCount(int count) {
        _sprites.setFrameCount(count);
    }

    protected void updateSprites(double deltaTime) {
        _sprites.update(deltaTime);
    }


    private final ImmutableMap _Map;
    private double _speed;
    private Vector2 _position;
    private Direction _direction;
    private Entity.State _state;
    private Sprites _sprites;
    private boolean _isIdle;

}
