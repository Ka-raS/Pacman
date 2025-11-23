package com.karas.pacman.screens;

import com.karas.pacman.resources.ResourcesManager;

public class GameOver implements Screen {

    public GameOver(ResourcesManager resourcesManager) {
        _resourcesManager = resourcesManager;
    }

    @Override
    public void enter(Class<? extends Screen> fromScreen) {}

    @Override
    public void exit(Class<? extends Screen> toScreen) {}

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        return null;
    }

    @Override
    public void repaint(java.awt.Graphics2D g) {}

    @Override
    public void input(java.awt.event.KeyEvent e) {}
    

    private ResourcesManager _resourcesManager;

}
