package com.karas.pacman.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.graphics.SpriteSheet;
import com.karas.pacman.Configs;
import com.karas.pacman.Game;

public class Pacman extends Entity {

    private static final int X_START = 128;
    private static final int Y_START = 128;

    public Pacman() {
        super(X_START, Y_START);
        m_sprites = SpriteSheet.getPacman();
        m_deathSprites = SpriteSheet.getPacmanDeath();
        m_spritePos = m_spriteCounter = 0;
        m_direction = m_nextDirection = Direction.RIGHT;
    }

    @Override
    public void update() {
        // TODO: Temporary, need validNextDirection()
        boolean directionChanged = false;
        if (m_direction != m_nextDirection) {
            m_direction = m_nextDirection;
            directionChanged = true;
        }

        switch (m_direction) {
            case RIGHT: m_x += 8; break;
            case LEFT:  m_x -= 8; break;
            case UP:    m_y -= 8; break;
            case DOWN:  m_y += 8; break;
            default:    break;
        }

        // TODO: Temporary
        if (directionChanged || ++m_spriteCounter >= 15) {
            m_spriteCounter = 0;
            m_spritePos = (m_spritePos + 1) % 2 + m_direction.ordinal() * 2;
        }
    }
    
    @Override
    public void render(Graphics2D g, Game gameReference) {
        g.setColor(Color.WHITE);
        g.drawImage(m_sprites[m_spritePos], m_x, m_y, Configs.SPRITE_SIZE_RENDERED, Configs.SPRITE_SIZE_RENDERED, gameReference);
    }

    private BufferedImage[] m_sprites;
    private BufferedImage[] m_deathSprites;
    private int m_spritePos;
    private int m_spriteCounter;

}
