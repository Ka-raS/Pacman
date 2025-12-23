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
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.karas.pacman.commons.Exitable;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.screens.ScreenManager;

public final class Game implements Exitable {

    public Game() {
        _running = false;
        _frameCount = 0;
        _scale = Constants.DEFAULT_SCALE;
        _resourceManager = new ResourcesManager();
        _screenManager = new ScreenManager(_resourceManager);
        _frame = new JFrame(Constants.TITLE);
        _panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics G) {
                super.paintComponent(G);
                Graphics2D G2D = (Graphics2D) G;
                G2D.scale(_scale, _scale);
                _screenManager.repaint(G2D);
                ++_frameCount;
                toolkit.sync();
            }

            private final Toolkit toolkit = Toolkit.getDefaultToolkit();
        };
    }

    /** must not be called after Game.exit() */
    public synchronized void enter() {
        if (_running)
            return;
        _running = true;

        _LOGGER.info("Entering game");
        try {
            SwingUtilities.invokeAndWait(this::initializeUI);
        } catch (InterruptedException e) {
            String message = Thread.currentThread() + " interrupted while waiting for EDT to initialize UI";
            _LOGGER.log(Level.WARNING, message, e);
            exit();
            return;
        } catch (InvocationTargetException e) {
            _LOGGER.log(Level.SEVERE, "Failed to initialize UI", e.getCause());
            exit();
            return;
        }
        _thread = new Thread(this::gameLoop, "Game Thread");
        _thread.start(); // GameThread calls this.gameLoop()
    }

    @Override
    public synchronized void exit() {
        if (!_running)
            return;
        _running = false;

        _LOGGER.info("Exiting game");
        SwingUtilities.invokeLater(() -> {
            _frame.setVisible(false);
            _frame.dispose();
        });
        _screenManager.exit();
        _resourceManager.exit();

        if (Thread.currentThread() != _thread) {
            try {
                _thread.join();
            } catch (InterruptedException e) {
                _LOGGER.log(Level.WARNING, "Join Game Thread Failed", e);
            }
        }
    }


    private void initializeUI() {
        _panel.setDoubleBuffered(true);
        _panel.setFocusable(true);
        _panel.setBackground(Constants.Color.BACKGROUND);
        _panel.setPreferredSize(new Dimension(
            (int) (Constants.Pixel.WINDOW_SIZE.ix() * _scale),
            (int) (Constants.Pixel.WINDOW_SIZE.iy() * _scale)
        ));
        _panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                _screenManager.input(e);
            }
        });
        _panel.addComponentListener(new ComponentAdapter() {
            @Override 
            public void componentResized(ComponentEvent e) {
                _scale = Math.min(
                    _panel.getWidth()  / Constants.Pixel.WINDOW_SIZE.x(),
                    _panel.getHeight() / Constants.Pixel.WINDOW_SIZE.y()
                );
            }
        });

        _frame.add(_panel);
        _frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                _panel.requestFocusInWindow();
            }
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        _frame.pack();
        _frame.setResizable(true);
        _frame.setIconImage(_resourceManager.getImage(ResourceID.WINDOW_ICON));
        _frame.setLocationRelativeTo(null);
        _frame.setVisible(true);
    }

    private void gameLoop() {
        long lastTime = System.nanoTime();
        double updateTimer = 0.0, repaintTimer = 0.0, statsTimer = 0.0;
        int updateCount = 0;

        while (_running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / 1e9;
            if (deltaTime < 0.0)
                continue;
            
            lastTime = currentTime;
            updateTimer += deltaTime;
            statsTimer += deltaTime;
            repaintTimer += deltaTime;

            while (_running && updateTimer >= Constants.Time.UPDATE_INTERVAL) {
                ++updateCount;
                updateTimer -= Constants.Time.UPDATE_INTERVAL;
                if (!_screenManager.update(Constants.Time.UPDATE_INTERVAL))
                    exit();
            }
            if (!_running)
                break;

            if (repaintTimer >= Constants.Time.REPAINT_INTERVAL) {
                repaintTimer = 0.0;
                _panel.repaint(); // EDT calls _panel.paintComponent()
            }

            if (statsTimer >= 1.0) {
                final int ups = updateCount, fps = _frameCount;
                statsTimer = updateCount = _frameCount = 0;
                SwingUtilities.invokeLater(() ->
                    _frame.setTitle(String.format("%s: %d UPS, %d FPS", Constants.TITLE, ups, fps))
                );
            }
        }
    }

    private static final Logger _LOGGER = Logger.getLogger(Game.class.getName());

    private final JFrame _frame;
    private final JPanel _panel;
    private final ScreenManager _screenManager;
    private final ResourcesManager _resourceManager;
    
    private volatile boolean _running;
    private volatile int _frameCount;
    private volatile double _scale;
    private Thread _thread;

}