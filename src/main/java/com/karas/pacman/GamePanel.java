package com.karas.pacman;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import com.karas.pacman.screens.ScreenManager;

public final class GamePanel extends JPanel {
    
    public GamePanel(ScreenManager ScreenMgr) {
        _offsetX = _offsetY = _frameCount = 0;
        _scale = Constants.DEFAULT_SCALE;
        _ScreenManager = ScreenMgr;
    }

    public int getFrameCount() {
        return _frameCount;
    }

    public void resetFrameCount() {
        _frameCount = 0;
    }

    public void initializeUI() {
        setFocusable(true);
        setBackground(Constants.Color.BACKGROUND);
        setPreferredSize(new Dimension(
            (int) (Constants.Pixel.WINDOW_SIZE.x() * _scale),
            (int) (Constants.Pixel.WINDOW_SIZE.y() * _scale)
        ));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                _ScreenManager.input(e);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override 
            public void componentResized(ComponentEvent e) {
                int width  = getWidth();
                int height = getHeight();
                _scale = Math.min(
                    width  / Constants.Pixel.WINDOW_SIZE.x(),
                    height / Constants.Pixel.WINDOW_SIZE.y()
                );
                _offsetX = (width  - Constants.Pixel.WINDOW_SIZE.x() * _scale) / 2;
                _offsetY = (height - Constants.Pixel.WINDOW_SIZE.y() * _scale) / 2;
            }
        });
    }


    @Override
    protected void paintComponent(Graphics G) {
        super.paintComponent(G);
        Graphics2D G2D = (Graphics2D) G;
        G2D.translate(_offsetX, _offsetY);
        G2D.scale(_scale, _scale);

        _ScreenManager.repaint(G2D);
        ++_frameCount;
    }


    private final ScreenManager _ScreenManager;

    private double _scale, _offsetX, _offsetY;
    private volatile int _frameCount;

}
