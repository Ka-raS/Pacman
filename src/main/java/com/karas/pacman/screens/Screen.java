package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public interface Screen {
    
    void enter();

    /** @return the next Screen, can be itself */
    Screen update(double deltaTime);

    void repaint(Graphics2D g);

    void input(KeyEvent e);
    
}
