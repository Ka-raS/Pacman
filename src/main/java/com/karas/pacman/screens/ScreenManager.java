package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Map;

import com.karas.pacman.resources.ResourcesManager;

public class ScreenManager {
    
    public ScreenManager(ResourcesManager resourcesManager) {
        Screen mainMenu = new MainMenu(resourcesManager);
        Screen playing  = new Playing(resourcesManager);
        Screen paused   = new Paused(resourcesManager, playing);
        Screen gameOver = new GameOver(resourcesManager);

        _screens = Map.of(
            MainMenu.class, mainMenu,
            Playing.class,  playing,
            Paused.class,   paused,
            GameOver.class, gameOver
        );

        _current = mainMenu;
        _current.enter(null);
    }

    public void exit() {
        for (Screen screen : _screens.values())
            screen.exit(null);
    }

    public void input(KeyEvent e) {
        _current.input(e);
    }

    public void repaint(Graphics2D g) {
        _current.repaint(g);
    }

    /** @return shouldExit */
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
    private Map<Class<? extends Screen>, Screen> _screens;

}
