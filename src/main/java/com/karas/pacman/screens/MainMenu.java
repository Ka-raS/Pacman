package com.karas.pacman.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.resources.Resource;
import com.karas.pacman.resources.ResourcesManager;

public class MainMenu implements Screen {
    
    public MainMenu(ResourcesManager ResourcesMgr) {
        _TitleImage = ResourcesMgr.getImage(Resource.TITLE_IMAGE);
        _Font = ResourcesMgr.getFont(Configs.UI.FONT_SIZE_SMALL);
    }

    @Override
    public void enter(Class<? extends Screen> fromScreen) {
        _nextScreen = MainMenu.class;
    }

    @Override
    public void exit(Class<? extends Screen> toScreen) {}

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D G) {
        paintBackgroundColor(G);
        paintTitleImage(G);
        paintPlayText(G);
    }

    @Override
    public void input(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getID() == KeyEvent.KEY_PRESSED)
            _nextScreen = Playing.class;
    }


    private void paintBackgroundColor(Graphics2D G) {
        G.setColor(new Color(0, 16, 48));
        G.fillRect(0, 0, Configs.UI.WINDOW_SIZE.ix(), Configs.UI.WINDOW_SIZE.iy());
    }

    private void paintTitleImage(Graphics2D G) {
        final double Ratio = (double) _TitleImage.getWidth() / _TitleImage.getHeight();
        int sizeX = (int) (Configs.UI.WINDOW_SIZE.ix() * 0.6);
        int sizeY = (int) (sizeX / Ratio);
        int x = (Configs.UI.WINDOW_SIZE.ix() - sizeX) / 2;
        int y = (Configs.UI.WINDOW_SIZE.iy() - sizeY) / 2 - (int) (64 * Configs.SCALING);
        G.drawImage(_TitleImage, x, y, sizeX, sizeY, null);
    }

    private void paintPlayText(Graphics2D G) {
        G.setColor(Color.YELLOW);
        G.setFont(_Font);
        int x = (Configs.UI.WINDOW_SIZE.ix() - G.getFontMetrics(_Font).stringWidth(_PLAY_TEXT)) / 2;
        int y = (int) (Configs.UI.WINDOW_SIZE.iy() * 0.8);
        G.drawString(_PLAY_TEXT, x, y);
    }

    private static final String _PLAY_TEXT = "PRESS ENTER TO PLAY!";

    private final BufferedImage _TitleImage;
    private final Font _Font;

    private volatile Class<? extends Screen> _nextScreen;

}
