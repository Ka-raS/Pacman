package com.karas.pacman.entities;

import java.awt.Graphics2D;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.maps.Map;
import com.karas.pacman.resources.SpriteSheet;
import com.karas.pacman.resources.Sprites;

public class Pacman implements Entity {

    public Pacman(ImmutableMap mapRef) {
        _position = Map.toPixelVector2(Configs.Grid.PACMAN_POS);
        _direction = _nextDirection = Direction.RIGHT;
        _speed = Configs.PX.PACMAN_SPEED;        
        _sprites = new Sprites(SpriteSheet.PACMAN, 0, 2);
        _mapRef = mapRef;
        enterState(Entity.State.PREY);
        
    }

    public Vector2 getPosition() {
        return _position;
    }

    public void setNextDirection(Direction d) {
        _nextDirection = d;
    }

    @Override
    public void enterState(State nextState) {
        if (_state == State.DEAD)
            return;

        switch (nextState) {
            case IDLE:
                break;
        
            case PREY, HUNTER:
                _sprites.setOffset(_direction.ordinal() * 2);
                break;

            case DEAD:
                if (_state == Entity.State.IDLE)
                    return;
                _sprites = new Sprites(SpriteSheet.DEAD_PACMAN, 0, 8);
                break;
        }
        _state = nextState;
    }

    @Override
    public void update(double deltaTime) {
        switch (_state) {
            case PREY, HUNTER:
                boolean checkedDir = _mapRef.validDirection(_position, _nextDirection);
                if (checkedDir && _direction != _nextDirection) {
                    _direction = _nextDirection;
                    _sprites.setOffset(_direction.ordinal() * 2);
                }
                if (checkedDir || _mapRef.validDirection(_position, _direction))
                    _position = _position.add(_direction.toVector2().mul(deltaTime * _speed));
                
            case IDLE:
                _sprites.update(deltaTime);
                break;

            case DEAD:
                if (!_sprites.isLastFrame())
                    _sprites.update(deltaTime);
                break;            
        }
    }

    @Override
    public void repaint(Graphics2D g) {
        Vector2 p = _position.mul(Configs.SCALING);
        g.drawImage(_sprites.getFrame(), p.ix(), p.iy(), Configs.UI.SPRITE_SIZE, Configs.UI.SPRITE_SIZE, null);
    }


    private Vector2 _position;
    private Direction _direction, _nextDirection;
    private double _speed;
    private State _state;
    private Sprites _sprites;
    private final ImmutableMap _mapRef;

}
