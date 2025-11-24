package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.karas.pacman.resources.ResourcesManager;

public class GameOver implements Screen {

    public GameOver(ResourcesManager ResourcesMgr) {
    }

    @Override
    public void enter(Class<? extends Screen> previous) {}

    @Override
    public void exit() {}

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        return null;
    }

    @Override
    public void repaint(Graphics2D G) {}

    @Override
    public void input(KeyEvent e) {}

}
