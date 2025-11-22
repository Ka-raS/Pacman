package com.karas.pacman.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import com.karas.pacman.Configs;
import com.karas.pacman.resources.ResourcesManager;

public class Paused implements Screen {
    
    public Paused(Screen playingScreen, ResourcesManager resourcesManager) {
        _playingScreen = playingScreen;
        _FontLarge = resourcesManager.getFont(Configs.UI.FONT_SIZE_LARGE);
        _FontSmall = resourcesManager.getFont(Configs.UI.FONT_SIZE_SMALL);
    }

    @Override
    public void enter(Class<? extends Screen> fromScreen) {
        _LOGGER.info("Game paused.");
        _nextScreen = Paused.class;
        _selectedOption = 0;
    }
    
    @Override
    public void exit(Class<? extends Screen> toScreen) {}

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        _playingScreen.update(deltaTime);
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D g) {
        _playingScreen.repaint(g);
        paintOverlay(g);
        paintText(g);
    }

    @Override
    public void input(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED)
            return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP    -> _selectedOption = (_selectedOption - 1 + _OPTIONS.length) % _OPTIONS.length;
            case KeyEvent.VK_DOWN  -> _selectedOption = (_selectedOption + 1) % _OPTIONS.length;
            case KeyEvent.VK_ENTER -> {
                switch (_selectedOption) {
                    case 0 -> _nextScreen = Playing.class;
                    case 1 -> _nextScreen = MainMenu.class;
                };
            }

            case KeyEvent.VK_ESCAPE -> _nextScreen = Playing.class;
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getID() == KeyEvent.KEY_PRESSED)
            _nextScreen = Playing.class;
    }


    private void paintOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 192));
        g.fillRect(0, 0, Configs.UI.WINDOW_SIZE.ix(), Configs.UI.WINDOW_SIZE.iy());
    }

    private void paintText(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(_FontLarge);
        g.drawString("PAUSED", Configs.UI.WINDOW_SIZE.ix()/3, Configs.UI.WINDOW_SIZE.iy()/3);

        g.setFont(_FontSmall);
        for (int i = 0; i < _OPTIONS.length; i++) {
            g.setColor(i == _selectedOption ? Color.YELLOW : Color.WHITE);
            g.drawString(_OPTIONS[i], Configs.UI.WINDOW_SIZE.ix()/3, Configs.UI.WINDOW_SIZE.iy()/2 + i * (_FontSmall.getSize() + 10));
        }
    }

    private static final Logger _LOGGER = Logger.getLogger(Paused.class.getName());
    private static final String[] _OPTIONS = { "RESUME", "EXIT TO MENU" };
    
    private final Font _FontLarge, _FontSmall;
    private Screen _playingScreen;
    private volatile int _selectedOption;
    private volatile Class<? extends Screen> _nextScreen;

}
