package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.graphics.MenuNavigator;
import com.karas.pacman.resources.Resource;
import com.karas.pacman.resources.ResourcesManager;

public class MainMenu implements Screen {
    
    public MainMenu(ResourcesManager ResourcesMgr) {
        _TitleImage = ResourcesMgr.getImage(Resource.TITLE_IMAGE);
        _navigator = new MenuNavigator(
            new Vector2(Configs.PX.WINDOW_SIZE.ix() / 3, Configs.PX.WINDOW_SIZE.iy() * 0.75),
            ResourcesMgr.getFont(Configs.PX.FONT_SIZE_SMALL),
            new MenuNavigator.MenuOption("Start Game", Playing.class),
            new MenuNavigator.MenuOption("Exit Game",  null)
        );
    }

    @Override
    public void enter(Class<? extends Screen> previous) {
        _nextScreen = MainMenu.class;
    }

    @Override
    public void exit() {}

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D G) {
        paintBackgroundColor(G);
        paintTitleImage(G);
        _navigator.repaint(G);
    }

    @Override
    public void input(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP    -> _navigator.previous();
            case KeyEvent.VK_DOWN  -> _navigator.next();
            case KeyEvent.VK_ENTER -> _nextScreen = _navigator.select();
        }
    }


    private void paintBackgroundColor(Graphics2D G) {
        G.setColor(Configs.Color.MAIN_MENU);
        G.fillRect(0, 0, Configs.PX.WINDOW_SIZE.ix(), Configs.PX.WINDOW_SIZE.iy());
    }

    private void paintTitleImage(Graphics2D G) {
        final double Ratio = (double) _TitleImage.getWidth() / _TitleImage.getHeight();
        int sizeX = (int) (Configs.PX.WINDOW_SIZE.ix() * 0.6);
        int sizeY = (int) (sizeX / Ratio);
        int x = (Configs.PX.WINDOW_SIZE.ix() - sizeX) / 2;
        int y = (Configs.PX.WINDOW_SIZE.iy() - sizeY) / 2 - 64;
        G.drawImage(_TitleImage, x, y, sizeX, sizeY, null);
    }

    private final BufferedImage _TitleImage;

    private final MenuNavigator _navigator;

    private volatile Class<? extends Screen> _nextScreen;

}
