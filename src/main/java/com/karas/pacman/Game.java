package com.karas.pacman;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.karas.pacman.commons.Exitable;
import com.karas.pacman.resources.Resource;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.screens.ScreenManager;

public class Game implements Exitable {

    public Game() {
        _running = false;
        _frameCount = 0;
        _scale = Configs.DEFAULT_SCALE;
        _resourceManager = new ResourcesManager();
        _screenManager = new ScreenManager(_resourceManager);

        _panel = new JPanel(true) {
            @Override
            protected void paintComponent(Graphics G) {
                super.paintComponent(G);
                Graphics2D G2D = (Graphics2D) G;
                G2D.scale(_scale, _scale);
                _screenManager.repaint(G2D);
                ++_frameCount;
                Toolkit.getDefaultToolkit().sync();
            }
        };
        _panel.setFocusable(true);
        _panel.setBackground(Configs.Color.BACKGROUND);
        _panel.setPreferredSize(new Dimension(
            (int) (Configs.PX.WINDOW_SIZE.ix() * _scale),
            (int) (Configs.PX.WINDOW_SIZE.iy() * _scale)
        ));

        _frame = new JFrame(Configs.TITLE);
        _frame.setVisible(false);
        _frame.setResizable(true);
        _frame.setIconImage(_resourceManager.getImage(Resource.WINDOW_ICON));
    }

    public synchronized void enter() {
        if (_running)
            return;
        _running = true;

        _LOGGER.info("Entering game...");
        _frame.add(_panel);
        _frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                _screenManager.input(e);
            }
        });
        _frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        _frame.addComponentListener(new ComponentAdapter() {
            @Override 
            public void componentResized(ComponentEvent e) {
                _scale = Math.min(
                    _panel.getWidth()  / Configs.PX.WINDOW_SIZE.x(),
                    _panel.getHeight() / Configs.PX.WINDOW_SIZE.y()
                );
            }
        });

        _frame.pack();
        _frame.setLocationRelativeTo(null);
        _frame.setVisible(true);
        _frame.requestFocus();

        _thread = new Thread(this::gameLoop);
        _thread.start(); // GameThread calls this.gameLoop()
    }

    @Override
    public synchronized void exit() {
        if (!_running)
            return;
        _running = false;

        _LOGGER.info("Exiting game...");
        _frame.setVisible(false);
        _screenManager.exit();
        _resourceManager.exit();
        _frame.dispose();

        if (Thread.currentThread() != _thread) {
            try {
                _thread.join();
            } catch (InterruptedException e) {
                _LOGGER.log(Level.WARNING, "Join Game Thread Failed", e);
            }
        }
    }


    private void gameLoop() {
        long lastTime = System.nanoTime();
        double updateTimer = 0.0, repaintTimer = 0.0, logTimer = 0.0;
        int updateCount = 0;

        while (_running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / 1e9;
            if (deltaTime < 0.0)
                continue;
            
            lastTime = currentTime;
            updateTimer += deltaTime;
            logTimer += deltaTime;
            repaintTimer += deltaTime;

            while (_running && updateTimer >= Configs.Time.UPDATE_INTERVAL) {
                if (!_screenManager.update(Configs.Time.UPDATE_INTERVAL))
                    exit();
                ++updateCount;
                updateTimer -= Configs.Time.UPDATE_INTERVAL;
            }

            if (repaintTimer >= Configs.Time.REPAINT_INTERVAL) {
                _panel.repaint(); // EDT calls _panel.paintComponent()
                repaintTimer = 0.0;
            }

            if (logTimer >= 1.0) {
                _frame.setTitle(String.format("%s: %d UPS, %d FPS", Configs.TITLE, updateCount, _frameCount));
                logTimer = updateCount = _frameCount = 0;
            }
        }
    }

    private static final Logger _LOGGER = Logger.getLogger(Game.class.getName());

    private final JFrame _frame;
    private final JPanel _panel;
    private final ScreenManager _screenManager;
    private final ResourcesManager _resourceManager;
    
    private Thread _thread;
    private volatile boolean _running;
    private volatile int _frameCount;
    private volatile double _scale;

}