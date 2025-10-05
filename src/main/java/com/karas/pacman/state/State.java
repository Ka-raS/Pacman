package com.karas.pacman.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public interface State {
    
    public void enter();

    public void exit();

    public State update();

    public void repaint(Graphics2D g);

    public void input(KeyEvent e);
    
}
