package com.karas.pacman.entity;

import java.awt.Graphics2D;

import com.karas.pacman.Configs;
import com.karas.pacman.Game;

public abstract class Entity {

    public enum Direction {
        RIGHT, LEFT, UP, DOWN
    }

    public Entity(int x, int y) {
        m_x = x;
        m_y = y;
        m_direction = Direction.values()[(int)(Math.random() * Direction.values().length)];
    }

    public void setNextDirection(Direction d) {
        m_nextDirection = d;
    }

    public boolean collides(Entity other) {
        final int BOUND = (int)(Configs.SPRITE_SIZE_RENDERED * 0.9f);
        return Math.abs(m_x - other.m_x) < BOUND && Math.abs(m_y - other.m_y) < BOUND;
    }
    
    public abstract void update();
    
    public abstract void render(Graphics2D g, Game gameReference);

    protected int m_x, m_y;
    protected Direction m_direction;
    protected Direction m_nextDirection;

}
