package com.karas.pacman;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.karas.pacman.commons.Exitable;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourceManager;
import com.karas.pacman.screens.ScreenManager;

public final class Game implements Exitable {

    public Game() {
        _running = false;
        _updateTimer = _repaintTimer = _statsTimer = _updateCount = 0;
        _resourceManager = new ResourceManager();
        _screenManager = new ScreenManager(_resourceManager);
        _thread = new Thread(this::gameLoop, "Game Thread");
        _frame = new JFrame(Constants.TITLE);
        _panel = new GamePanel(_screenManager);

        SwingUtilities.invokeLater(() -> {
            _frame.add(_panel);
            _frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowActivated(WindowEvent e) {
                    _panel.requestFocusInWindow();
                }
                @Override
                public void windowClosing(WindowEvent e) {
                    _running = false;
                }
            });

            _frame.pack(); // uses _panel.getPreferredSize()
            _frame.setResizable(true);
            _frame.setIconImage(_resourceManager.getImage(ResourceID.WINDOW_ICON));
            _frame.setLocationRelativeTo(null);
        });
    }

    /** must not be called after {@link Game#exit()} */
    public synchronized void enter() {
        if (_running)
            return;
        _running = true;
        LOG.info("Entering game");
        SwingUtilities.invokeLater(() -> _frame.setVisible(true));
        _thread.start();
    }

    @Override
    public void exit() {
        _running = false;
    }


    private void gameLoop() {
        long previousTime = System.nanoTime();

        while (_running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - previousTime) / 1e9;
            if (deltaTime < 0.0)
                continue;
            previousTime = currentTime;
        
            update(deltaTime);
            repaint(deltaTime);
            displayStats(deltaTime);
        }

        cleanup();
    }

    private void update(double deltaTime) {
        final double STEP = 1.0 / Constants.UPS_TARGET;

        _updateTimer += deltaTime;
        if (_updateTimer > STEP * 4)
            _updateTimer = STEP * 4;

        while (_updateTimer >= STEP) {
            if (!_screenManager.update(STEP)) {
                _running = false;
                return;
            }
            _updateTimer -= STEP;
            ++_updateCount;
        }
    }

    private void repaint(double deltaTime) {
        _repaintTimer += deltaTime;
        if (_repaintTimer >= 1.0 / Constants.FPS_TARGET) {
            _panel.repaint(); // EDT calls _panel.paintComponent()
            _repaintTimer = 0.0;
        }
    }

    private void displayStats(double deltaTime) {
        _statsTimer += deltaTime;
        if (_statsTimer >= 1.0) {
            String title = Constants.TITLE + ": " + _updateCount + " UPS, " + _panel.getFrameCount() + " FPS";
            SwingUtilities.invokeLater(() -> _frame.setTitle(title));
            _statsTimer = _updateCount = 0;
            _panel.resetFrameCount();
        }
    }

    private void cleanup() {
        LOG.info("Exiting game");
        SwingUtilities.invokeLater(() -> _frame.dispose());
        _screenManager.exit();
        _resourceManager.exit();
    }

    private static final Logger LOG = Logger.getLogger(Game.class.getName());

    private final Thread _thread;
    private final JFrame _frame;
    private final GamePanel _panel;
    private final ScreenManager _screenManager;
    private final ResourceManager _resourceManager;
    
    private volatile boolean _running;
    private double _updateTimer, _repaintTimer, _statsTimer;
    private int _updateCount;

}