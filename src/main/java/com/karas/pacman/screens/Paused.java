package com.karas.pacman.screens;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.resources.ResourcesManager;

public class Paused implements Screen {
    
    public Paused(Screen PlayingScreen, ResourcesManager ResourcesMgr) {
        _PlayingScreen = PlayingScreen;
        _FontLarge = ResourcesMgr.getFont(Configs.PX.FONT_SIZE_LARGE);
        
        _navigator = new MenuNavigator(
            new Vector2(Configs.PX.WINDOW_SIZE.ix() * 0.375, Configs.PX.WINDOW_SIZE.iy() / 2),
            ResourcesMgr.getFont(Configs.PX.FONT_SIZE_SMALL),
            new MenuNavigator.MenuOption("Resume",    Playing.class),
            new MenuNavigator.MenuOption("Main Menu", MainMenu.class)
        );
    }

    @Override
    public void enter(Class<? extends Screen> previous) {
        _LOGGER.info("Game paused.");
        _nextScreen = Paused.class;
    }
    
    @Override
    public void exit() {}

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        _PlayingScreen.update(deltaTime);
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D G) {
        _PlayingScreen.repaint(G);
        paintOverlay(G);
        paintPausedText(G);
        _navigator.repaint(G);
    }

    @Override
    public void input(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP,   KeyEvent.VK_W -> _navigator.previous();
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> _navigator.next();
            
            case KeyEvent.VK_ENTER  -> _nextScreen = _navigator.select();
            case KeyEvent.VK_ESCAPE -> _nextScreen = Playing.class;
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getID() == KeyEvent.KEY_PRESSED)
            _nextScreen = Playing.class;
    }


    private void paintOverlay(Graphics2D G) {
        G.setColor(Configs.Color.PAUSE_MENU);
        G.fillRect(0, 0, Configs.PX.WINDOW_SIZE.ix(), Configs.PX.WINDOW_SIZE.iy());
    }

    private void paintPausedText(Graphics2D G) {
        G.setColor(Configs.Color.TEXT);
        G.setFont(_FontLarge);
        G.drawString("PAUSED", Configs.PX.WINDOW_SIZE.ix() / 3, Configs.PX.WINDOW_SIZE.iy() / 3);
    }

    private static final Logger _LOGGER = Logger.getLogger(Paused.class.getName());

    private final Font _FontLarge;
    private final Screen _PlayingScreen;

    private final MenuNavigator _navigator;

    private volatile Class<? extends Screen> _nextScreen;

}
