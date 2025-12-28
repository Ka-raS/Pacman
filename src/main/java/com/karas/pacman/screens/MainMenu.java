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
        G.setColor(Constants.Color.MAIN_MENU);
        G.fillRect(0, 0, Constants.Pixel.WINDOW_SIZE.ix(), Constants.Pixel.WINDOW_SIZE.iy());

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


    private void paintTitleImage(Graphics2D G) {
        if (_TitleImage == null)
            return;
        final int W = Constants.Pixel.WINDOW_SIZE.ix();
        final int H = Constants.Pixel.WINDOW_SIZE.iy();

        int width = (int) (W * 0.75);
        int height = width * _TitleImage.getHeight() / _TitleImage.getWidth();
        G.drawImage(_TitleImage, (W - width) / 2, H / 8, width, height, null);
    }

    private final BufferedImage _TitleImage;

    private final Navigator _navigator;
    private volatile Class<? extends Screen> _nextScreen;

}
