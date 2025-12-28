package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.logging.Logger;

import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Exitable;
import com.karas.pacman.resources.ResourceManager;

public final class ScreenManager implements Exitable, Paintable {
    
    public ScreenManager(ResourceManager ResourceMgr) {
        Screen mainMenu   = new MainMenu(ResourceMgr);
        Screen playing    = new Playing(ResourceMgr);
        Screen paused     = new Paused(playing, ResourceMgr);
        Screen highScores = new HighScores(ResourceMgr);

        _current = mainMenu;
        _current.enter(null);
        _enteringScreen = false;
        _screens = Map.of(
            MainMenu.class,   mainMenu,
            Playing.class,    playing,
            Paused.class,     paused,
            HighScores.class, highScores
        );
    }

    @Override
    public void exit() {
        for (Screen screen : _screens.values())
            screen.exit();
    }

    public void input(KeyEvent e) {
        if (!_enteringScreen)
            _current.input(e);
    }

    @Override
    public void repaint(Graphics2D G) {
        if (!_enteringScreen)
            _current.repaint(G);
    }

    /** @return {@code false} if exiting, {@code true} if running */
    public boolean update(double deltaTime) {
        Class<? extends Screen> nextScreen = _current.update(deltaTime);
        if (nextScreen == null)
            return false;
        if (nextScreen == _current.getClass())
            return true;

        _enteringScreen = true;
        LOG.info(() -> "Switching to screen " + nextScreen.getSimpleName());

        Class<? extends Screen> currentScreen = _current.getClass();
        _current.exit();
        _current = _screens.get(nextScreen);
        _current.enter(currentScreen);

        _enteringScreen = false;
        return true;
    }

    private static final Logger LOG = Logger.getLogger(ScreenManager.class.getName());

    private final Map<Class<? extends Screen>, Screen> _screens;
    private Screen _current;
    private volatile boolean _enteringScreen;

}
