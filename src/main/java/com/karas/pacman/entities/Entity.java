package com.karas.pacman.entities;

import java.awt.Graphics2D;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
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
        _previousState = _state;
        _state = nextState;
    }

    public final void enterPreviousState() {
        enterState(_previousState);
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

    protected final boolean isCenteredInTile() {
        return _centeredInTile;
    }

    protected final boolean isValidDirection(Direction direction) {
        Vector2 toGrid = _gridPosition.add(direction.vector2());
        return switch (_Map.tileAt(toGrid)) {
            case WALL -> false;
            case GATE -> _state == State.DEAD || _gridPosition.y() > toGrid.y();
            default   -> true;
        };
    }

    protected final void setDirection(Direction direction) {
        if (direction == _direction)
            return;
        _direction = direction;
        _Sprite.setDirection(_direction);
    }

    protected final void setGridPosition(Vector2 gridPosition) {
        _gridPosition = gridPosition;
        _position = GameMap.toPixelVector2(gridPosition);
        _centeredInTile = GameMap.isCenteredInTile(_position);
    }

    protected final void updatePosition(double deltaTime) {
        if (_centeredInTile && !isValidDirection(_direction))
            return;
        _position = _position.add(_direction.vector2().mul(deltaTime * _speed));

        boolean nowCentered = GameMap.isCenteredInTile(_position);
        boolean recentered  = nowCentered && !_centeredInTile;
        _centeredInTile = nowCentered;
        if (!recentered)
            return;

        _gridPosition = GameMap.toGridVector2(_position);
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
    private State _state, _previousState;

}
