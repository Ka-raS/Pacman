package com.karas.pacman;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.karas.pacman.resources.Resource;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.screens.ScreenManager;

public class Game extends JPanel implements Runnable, KeyListener {

    public Game() {
        _running = false;
        _frame = new JFrame(Configs.TITLE);
        _resourceManager = new ResourcesManager();
        _screenManager = new ScreenManager(_resourceManager);

        _frame.setResizable(false);
        _frame.setIconImage(_resourceManager.getImage(Resource.WINDOW_ICON));
        _frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { exit(); }
        });
        
        setBackground(Configs.BACKGROUND_COLOR);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(Configs.UI.WINDOW_SIZE.ix(), Configs.UI.WINDOW_SIZE.iy()));

        _frame.add(this);
        _frame.addKeyListener(this);
        _frame.pack();
        _frame.setLocationRelativeTo(null);
    }

    public synchronized void enter() {
        if (_running)
            return;
        _running = true;

        _LOGGER.info("Entering game...");
        _thread = new Thread(this, "Game Thread");
        _updateTimer = _repaintTimer = _logTimer = _updateCount = _frameCount = 0;
        _frame.setVisible(true);
        _frame.requestFocus();
        _thread.start(); // Game Thread calls run()
    }

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

    /** {@code Runnable} method. Enter Game with {@code enter()} instead. */
    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (_running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / 1e9;
            if (deltaTime < 0.0)
                continue;
            lastTime = currentTime;

            updateGame(deltaTime);
            if (!_running)
                break;
            repaintGame(deltaTime);
            logGame(deltaTime);
        }
    }

    /** {@code KeyListener} method. Gets called by EDT. */
    @Override
    public void keyPressed(KeyEvent e) {
        _screenManager.input(e);
    }

    /** {@code KeyListener} method. Gets called by EDT. */
    @Override
    public void keyReleased(KeyEvent e) {
        _screenManager.input(e);
    }

    /** {@code KeyListener} method. Gets called by EDT. */
    @Override
    public void keyTyped(KeyEvent e) {
        _screenManager.input(e);
    }


    /** {@code JPanel} method. Gets called by EDT. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        _screenManager.repaint(g2D);
        ++_frameCount;
        g2D.dispose();
        Toolkit.getDefaultToolkit().sync();
    }


    private void updateGame(double deltaTime) {
        deltaTime = Math.min(deltaTime, 0.25); // lag spike
        _updateTimer += deltaTime;
        while (_running && _updateTimer >= Configs.Time.UPDATE_INTERVAL) {
            if (!_screenManager.update(Configs.Time.UPDATE_INTERVAL))
                exit();
            ++_updateCount;
            _updateTimer -= Configs.Time.UPDATE_INTERVAL;
        }
    }

    private void repaintGame(double deltaTime) {
        _repaintTimer += deltaTime;
        if (_repaintTimer >= Configs.Time.REPAINT_INTERVAL) {
            repaint(); // EDT calls paintComponent()
            _repaintTimer = 0.0;
        }
    }

    private void logGame(double deltaTime) {
        _logTimer += deltaTime;
        if (_logTimer >= 1.0) {
            _frame.setTitle(String.format("%s: %d UPS, %d FPS", Configs.TITLE, _updateCount, _frameCount));
            _logTimer = _updateCount = _frameCount = 0;
        }
    }

    private static final Logger _LOGGER = Logger.getLogger(Game.class.getName());

    private boolean _running;
    private JFrame _frame;
    private Thread _thread;
    private ScreenManager _screenManager;
    private ResourcesManager _resourceManager;
    private int _updateCount;
    private volatile int _frameCount;
    private double _updateTimer, _repaintTimer, _logTimer;

}