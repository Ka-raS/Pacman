package com.karas.pacman.entities;

import java.awt.Graphics2D;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Tile;
import com.karas.pacman.maps.GameMap;

public abstract class Entity implements ImmutableEntity, Paintable {
    
    public static enum State {
        IDLE, HUNTER, PREY, DEAD;
    }

    public abstract void update(double deltaTime);

    public abstract void reset();

    @Override
    public final Vector2 getPosition() {
        return _position;
    } 

    @Override
    public final Direction getDirection() {
        return _direction;
    }

    @Override
    public final Vector2 getGridPosition() {
        return _gridPosition;
    }

    public final State getState() {
        return _state;
    }

    public final void enterState(State nextState) {
        handleStateTransition(nextState);
        if (_state != State.IDLE)
            _preIdleState = _state;
        _state = nextState;
    }

    public final void enterPreIdleState() {
        enterState(_preIdleState);
    }

    public final boolean collidesWith(Entity other) {
        return _position.distance(other._position) < Constants.Pixel.SPRITE_SIZE * 0.7;
    }

    @Override
    public final void repaint(Graphics2D G) {
        G.drawImage(_Sprite.getFrame(), _position.ix(), _position.iy(), null);
    }


    protected Entity(Vector2 gridPosition, Direction direction, int speed, ImmutableMap MapRef) {
        _gridPosition = gridPosition;
        _direction = direction;
        _speed = speed;
        _Map = MapRef;
        _position = GameMap.toPixelVector2(gridPosition);
        _centeredInTile = GameMap.isCenteredInTile(_position);
    }

    protected abstract void handleStateTransition(State nextState);

    protected abstract boolean validMovement(Vector2 fromGrid, Vector2 toGrid);

    protected final boolean isCenteredInTile() {
        return _centeredInTile;
    }

    protected final Tile tileAt(Vector2 gridPosition) {
        return _Map.tileAt(gridPosition);
    }

    protected final void setDirection(Direction direction) {
        _direction = direction;
        _Sprite.setDirection(_direction);
    }

    protected final void setGridPosition(Vector2 gridPosition) {
        _gridPosition = gridPosition;
        _position = GameMap.toPixelVector2(gridPosition);
        _centeredInTile = GameMap.isCenteredInTile(_position);
    }

    protected final void updatePosition(double deltaTime) {
        if (_centeredInTile && !validMovement(_gridPosition, _gridPosition.add(_direction.toVector2())))
            return;
        _position = _position.add(_direction.toVector2().mul(deltaTime * _speed));
        _centeredInTile = GameMap.isCenteredInTile(_position);

        if (!_centeredInTile) {
            _gridPosition = GameMap.toGridVector2(_position);
            return;
        }

        Vector2 tunneled = _Map.useTunnel(_gridPosition, _direction);
        if (tunneled != null) {
            _gridPosition = tunneled;
            _position = GameMap.toPixelVector2(tunneled);
        }
    }

    protected final void setSpeed(int speed) {
        _speed = speed;
    }

    protected final void updateSprite(double deltaTime) {
        _Sprite.update(deltaTime);
    }

    protected final void setSprite(Sprite SpriteRef) {
        _Sprite = SpriteRef;
        _Sprite.setDirection(_direction);
    }


    private final ImmutableMap _Map;
    private Sprite _Sprite;

    private int _speed;
    private boolean _centeredInTile;
    private Vector2 _position, _gridPosition;
    private Direction _direction;
    private State _state, _preIdleState;

}
