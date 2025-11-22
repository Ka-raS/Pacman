package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import com.karas.pacman.resources.ResourcesManager;

public class ScreenManager {
    
    public ScreenManager(ResourcesManager resourcesManager) {
        Screen mainMenu = new MainMenu(resourcesManager);
        Screen playing = new Playing(resourcesManager);
        Screen paused = new Paused(playing, resourcesManager);

        _screens = new HashMap<>(3);
        _screens.put(MainMenu.class, mainMenu);
        _screens.put(Playing.class, playing);
        _screens.put(Paused.class, paused);

        _current = mainMenu;
        _current.enter(null);
    }

    public void exit() {
        for (Screen screen : _screens.values())
            screen.exit(null);
        _screens.clear();
    }

    public void input(KeyEvent e) {
        _current.input(e);
    }

    public void repaint(Graphics2D g) {
        _current.repaint(g);
    }

    /** @return isRunning */
    public boolean update(double deltaTime) {
        Class<? extends Screen> nextScreen = _current.update(deltaTime);
        if (nextScreen == null)
            return false; // exit
        if (nextScreen == _current.getClass())
            return true;

        Class<? extends Screen> fromScreen = _current.getClass();
        _current.exit(nextScreen);
        _current = _screens.get(nextScreen);
        _current.enter(fromScreen);
        return true;
    }


    private Screen _current;
    private HashMap<Class<? extends Screen>, Screen> _screens;

}
