package com.karas.pacman.screens;

import java.awt.event.KeyEvent;

import com.karas.pacman.commons.Drawable;

public interface Screen extends Drawable {
    
    void enter(Class<? extends Screen> fromScreen);

    void exit(Class<? extends Screen> toScreen);

    /** @return next screen.class to switch, current screen.class to remain, or null to exit */
    Class<? extends Screen> update(double deltaTime);

    void input(KeyEvent e);
    
}
