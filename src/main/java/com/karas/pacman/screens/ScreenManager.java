package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Map;

import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Exitable;
import com.karas.pacman.resources.ResourcesManager;

public class ScreenManager implements Exitable, Paintable {
    
    public ScreenManager(ResourcesManager ResourcesMgr) {
        Screen mainMenu   = new MainMenu(ResourcesMgr);
        Screen playing    = new Playing(ResourcesMgr);
        Screen paused     = new Paused(playing, ResourcesMgr);
        Screen highScores = new HighScores(ResourcesMgr);

        _current = mainMenu;
        _screens = Map.of(
            MainMenu.class,   mainMenu,
            Playing.class,    playing,
            Paused.class,     paused,
            HighScores.class, highScores
        );
    }

    public void enter() {
        _current = _screens.get(MainMenu.class);
        _current.enter(null);
    }

    @Override
    public void exit() {
        for (Screen screen : _screens.values())
            screen.exit();
    }

    public void input(KeyEvent e) {
        _current.input(e);
    }

    @Override
    public void repaint(Graphics2D G) {
        _current.repaint(G);
    }

    /** @return shouldExit */
    public boolean update(double deltaTime) {
        Class<? extends Screen> nextScreen = _current.update(deltaTime);
        if (nextScreen == null)
            return false; // exit
        if (nextScreen == _current.getClass())
            return true;

        Class<? extends Screen> currentScreen = _current.getClass();
        _current.exit();
        _current = _screens.get(nextScreen);
        _current.enter(currentScreen);
        return true;
    }


    private Screen _current;
    private final Map<Class<? extends Screen>, Screen> _screens;

}
