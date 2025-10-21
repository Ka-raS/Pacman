package com.karas.pacman;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.karas.pacman.common.ExceptionHandler;
import com.karas.pacman.resources.ImageLoader;
import com.karas.pacman.state.Playing;
import com.karas.pacman.state.State;

public class Game extends JPanel implements Runnable, KeyListener {

    public Game() {
        _running = false;
        _state = new Playing();
        _frame = new JFrame(Configs.TITLE);

        _frame.setResizable(false);
        _frame.setIconImage(ImageLoader.getWindowIcon());
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBackground(Configs.BACKGROUND_COLOR);
        setDoubleBuffered(true);
        setPreferredSize(Configs.WINDOW_SIZE);

        _frame.add(this);
        _frame.addKeyListener(this);
        _frame.pack();
        _frame.setLocationRelativeTo(null);
    }

    public synchronized void enter() {
        if (_running)
            return;
        _running = true;
        _thread = new Thread(this, "Game Thread");
        _updateTimer = _repaintTimer = _logTimer = _updateCount = _frameCount = 0;

        _state.enter();
        _frame.setVisible(true);
        _frame.requestFocus();
        _thread.start(); // calls run()
    }

    public synchronized void exit() {
        if (!_running)
            return;
        System.out.println("Exiting game...");
        _running = false;
        _frame.dispose();

        if (Thread.currentThread() != _thread) {
            try {
                _thread.join();
            } catch (InterruptedException e) {
                ExceptionHandler.handleGeneric(e, "Join Game Thread Failed");
            }
        }
    }

    /** {@code Runnable} method. Start the game with {@code enter()} instead. */
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

    private void updateGame(double deltaTime) {
        deltaTime = Math.min(deltaTime, 0.25); // lag spike
        _updateTimer += deltaTime;
        final double STEP = 1.0 / Configs.UPS_TARGET;

        while (_running && _updateTimer >= STEP) {
            ++_updateCount;
            _updateTimer -= STEP;

            State nextState = _state.update(STEP);
            if (nextState == null) {
                _state.exit();
                exit();

            } else if (nextState != _state) {
                _state.exit();
                _state = nextState;
                _state.enter();
            }
        }
    }

    private void repaintGame(double deltaTime) {
        _repaintTimer += deltaTime;
        if (_repaintTimer >= 1.0 / Configs.FPS_TARGET) {
            repaint(); // calls paintComponent()
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

    /** {@code JPanel} method. Gets called by {@code repaint()} only. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        _state.repaint(g2D);
        ++_frameCount;
        g2D.dispose();
        Toolkit.getDefaultToolkit().sync();
    }

    /** {@code KeyListener} method. Do not call. */
    @Override
    public void keyPressed(KeyEvent e) {
        _state.input(e);
    }

    /** {@code KeyListener} method. Do not call. */
    @Override
    public void keyReleased(KeyEvent e) {
        _state.input(e);
    }

    /** {@code KeyListener} method. Do not call. */
    @Override
    public void keyTyped(KeyEvent e) {
        _state.input(e);
    }

    private boolean _running;
    private JFrame _frame;
    private Thread _thread;
    private volatile State _state;

    private double _updateTimer;
    private double _repaintTimer;
    private double _logTimer;
    private int _updateCount;
    private volatile int _frameCount;

}