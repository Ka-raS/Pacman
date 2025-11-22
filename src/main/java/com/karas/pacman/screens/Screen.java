package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public interface Screen {
    
    void enter(Class<? extends Screen> fromScreen);

    void exit(Class<? extends Screen> toScreen);

    /** @return next screen.class to switch, current screen.class to remain, or null to exit */
    Class<? extends Screen> update(double deltaTime);

    void repaint(Graphics2D g);

    void input(KeyEvent e);
    
}
