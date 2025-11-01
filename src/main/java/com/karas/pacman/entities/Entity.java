package com.karas.pacman.entities;

import java.awt.Graphics2D;

public interface Entity {
    
    enum State {
        IDLE, HUNTER, PREY, DEAD;
    }

    void enterState(State nextState);

    void update(double deltaTime);
    
    void repaint(Graphics2D g);

}
