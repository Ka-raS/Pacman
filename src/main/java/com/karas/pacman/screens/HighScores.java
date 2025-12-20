package com.karas.pacman.screens;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.karas.pacman.Constants;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.ScoreDatabase;

public final class HighScores implements Screen {

    public HighScores(ResourcesManager ResourcesMgr) {
        _ScoreDatabase = ResourcesMgr.getDatabase();
        _HeaderImage = ResourcesMgr.getImage(ResourceID.HIGHSCORE_IMAGE);
        _FontMedium = ResourcesMgr.getFont(Constants.Pixel.FONT_SIZE_MEDIUM);
        _FontSmall = ResourcesMgr.getFont(Constants.Pixel.FONT_SIZE_SMALL);
    }

    @Override
    public void enter(Class<? extends Screen> previous) {
        _nextScreen = HighScores.class;
        _highscores = _ScoreDatabase.getTopEntries(5);
    }

    @Override
    public void exit() {}

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D G) {
        paintHeaderImage(G);
        paintText(G);
        paintHighScores(G);
    }

    @Override
    public void input(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            _nextScreen = Playing.class;
    }


    private void paintHeaderImage(Graphics2D G) {
        final double Ratio = (double) _HeaderImage.getWidth() / _HeaderImage.getHeight();
        int sizeX = (int) (Constants.Pixel.WINDOW_SIZE.ix() * 0.6);
        int sizeY = (int) (sizeX / Ratio);
        int x = (Constants.Pixel.WINDOW_SIZE.ix() - sizeX) / 2;
        int y = Constants.Pixel.WINDOW_SIZE.iy() >> 4;
        G.drawImage(_HeaderImage, x, y, sizeX, sizeY, null);
    }

    private void paintText(Graphics2D G) {
        String text = "HIGH SCORES";
        FontMetrics fm = G.getFontMetrics(_FontMedium);
        int x = (Constants.Pixel.WINDOW_SIZE.ix() - fm.stringWidth(text)) / 2;
        int y = Constants.Pixel.WINDOW_SIZE.iy() / 3;

        G.setFont(_FontMedium);
        G.setColor(Constants.Color.TEXT);
        G.drawString(text, x, y);

        text = "NEW GAME";
        fm = G.getFontMetrics(_FontSmall);
        x = (Constants.Pixel.WINDOW_SIZE.ix() - fm.stringWidth(text)) / 2;
        y = (int) (Constants.Pixel.WINDOW_SIZE.iy() * 0.85);
        
        G.setFont(_FontSmall);
        G.setColor(Constants.Color.HIGHLIGHT);
        G.drawString(text, x, y);
    }

    private void paintHighScores(Graphics2D G) {
        G.setFont(_FontSmall);
        G.setColor(Constants.Color.TEXT);
        FontMetrics fm = G.getFontMetrics(_FontSmall);
        int y = (int) (Constants.Pixel.WINDOW_SIZE.iy() * 0.45);
        int i = 0;

        for (ScoreDatabase.Entry entry : _highscores) {
            String text = String.format("%d. %05d - %s", ++i, entry.score(), entry.date());
            int x = (Constants.Pixel.WINDOW_SIZE.ix() - fm.stringWidth(text)) / 2;
            G.drawString(text, x, y);
            y += fm.getHeight() * 2;
        }
    }

    private final ScoreDatabase _ScoreDatabase;
    private final BufferedImage _HeaderImage;
    private final Font _FontSmall, _FontMedium;
    
    private Iterable<ScoreDatabase.Entry> _highscores;
    private volatile Class<? extends Screen> _nextScreen;

}
