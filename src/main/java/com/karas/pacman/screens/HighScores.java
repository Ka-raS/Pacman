package com.karas.pacman.screens;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.karas.pacman.Constants;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourceManager;
import com.karas.pacman.resources.ScoreDatabase;

public final class HighScores implements Screen {

    public HighScores(ResourceManager ResourceMgr) {
        _ScoreDatabase = ResourceMgr.getDatabase();
        _HeaderImage   = ResourceMgr.getImage(ResourceID.HIGHSCORE_IMAGE);
        _FontMedium    = ResourceMgr.getFont(Constants.Pixel.FONT_SIZE_MEDIUM);
        _FontSmall     = ResourceMgr.getFont(Constants.Pixel.FONT_SIZE_SMALL);
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
        if (_HeaderImage == null)
            return;
        final int W = Constants.Pixel.WINDOW_SIZE.ix();
        final int H = Constants.Pixel.WINDOW_SIZE.iy();

        int width = (int) (W * 0.6);
        int height = width * _HeaderImage.getHeight() / _HeaderImage.getWidth();
        G.drawImage(_HeaderImage, (W - width) / 2, H / 16, width, height, null);
    }

    private void paintText(Graphics2D G) {
        final int W = Constants.Pixel.WINDOW_SIZE.ix();
        final int H = Constants.Pixel.WINDOW_SIZE.iy();

        final String TOP = "HIGH SCORES";
        G.setFont(_FontMedium);
        G.setColor(Constants.Color.TEXT);
        G.drawString(TOP, (W - G.getFontMetrics().stringWidth(TOP)) / 2, H / 3);

        final String BOTTOM = "NEW GAME";
        G.setFont(_FontSmall);
        G.setColor(Constants.Color.HIGHLIGHT);
        G.drawString(BOTTOM, (W - G.getFontMetrics().stringWidth(BOTTOM)) / 2, (int)(H * 0.85));
    }

    private void paintHighScores(Graphics2D G) {
        final int W = Constants.Pixel.WINDOW_SIZE.ix();
        final int H = Constants.Pixel.WINDOW_SIZE.iy();

        G.setFont(_FontSmall);
        G.setColor(Constants.Color.TEXT);
        FontMetrics fm = G.getFontMetrics(_FontSmall);
        int y = (int) (H * 0.45);
        int i = 0;

        for (ScoreDatabase.Entry entry : _highscores) {
            String text = String.format("%d. %05d - %s", ++i, entry.score(), entry.date());
            G.drawString(text, (W - fm.stringWidth(text)) / 2, y);
            y += fm.getHeight() * 2;
        }
    }

    private final ScoreDatabase _ScoreDatabase;
    private final BufferedImage _HeaderImage;
    private final Font _FontSmall, _FontMedium;
    
    private volatile Class<? extends Screen> _nextScreen;
    private Iterable<ScoreDatabase.Entry> _highscores;

}
