package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourceManager;

public final class MainMenu implements Screen {
    
    public MainMenu(ResourceManager ResourceMgr) {
        _TitleImage = ResourceMgr.getImage(ResourceID.TITLE_IMAGE);
        _navigator = new Navigator(
            new Vector2(Constants.Pixel.WINDOW_SIZE.ix() * 0.375, Constants.Pixel.WINDOW_SIZE.iy() * 0.75),
            ResourceMgr.getFont(Constants.Pixel.FONT_SIZE_SMALL),
            new Navigator.Option("Start Game", Playing.class),
            new Navigator.Option("Exit Game",  null)
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
            case KeyEvent.VK_UP,   KeyEvent.VK_W -> _navigator.previous();
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> _navigator.next();
            case KeyEvent.VK_ENTER -> _nextScreen = _navigator.select();
        }
    }


    private void paintBackgroundColor(Graphics2D G) {
        G.setColor(Constants.Color.MAIN_MENU);
        G.fillRect(0, 0, Constants.Pixel.WINDOW_SIZE.ix(), Constants.Pixel.WINDOW_SIZE.iy());
    }

    private void paintTitleImage(Graphics2D G) {
        final double Ratio = (double) _TitleImage.getWidth() / _TitleImage.getHeight();
        int sizeX = (int) (Constants.Pixel.WINDOW_SIZE.ix() * 0.75);
        int sizeY = (int) (sizeX / Ratio);
        int x = (Constants.Pixel.WINDOW_SIZE.ix() - sizeX) / 2;
        int y = Constants.Pixel.WINDOW_SIZE.iy() / 8;
        G.drawImage(_TitleImage, x, y, sizeX, sizeY, null);
    }

    private final BufferedImage _TitleImage;

    private final Navigator _navigator;
    private volatile Class<? extends Screen> _nextScreen;

}
