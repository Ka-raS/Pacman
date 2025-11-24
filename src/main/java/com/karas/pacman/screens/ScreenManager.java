package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Map;

import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Enterable;
import com.karas.pacman.resources.ResourcesManager;

public class ScreenManager implements Enterable, Paintable {
    
    public ScreenManager(ResourcesManager ResourcesMgr) {
        Screen mainMenu = new MainMenu(ResourcesMgr);
        Screen playing  = new Playing(ResourcesMgr);
        Screen paused   = new Paused(playing, ResourcesMgr);
        Screen gameOver = new GameOver(ResourcesMgr);

        _screens = Map.of(
            MainMenu.class, mainMenu,
            Playing.class,  playing,
            Paused.class,   paused,
            GameOver.class, gameOver
        );
        _current = _screens.get(MainMenu.class);
    }

    @Override
    public void enter() {
        _current.enter(null);
    }

    @Override
    public void exit() {
        for (Screen screen : _screens.values())
            screen.exit(null);
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

        Class<? extends Screen> fromScreen = _current.getClass();
        _current.exit(nextScreen);
        _current = _screens.get(nextScreen);
        _current.enter(fromScreen);
        return true;
    }


    private final Map<Class<? extends Screen>, Screen> _screens;

    private Screen _current;

}
