package com.karas.pacman;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.karas.pacman.resources.ImageLoader;
import com.karas.pacman.state.Playing;
import com.karas.pacman.state.State;

public class Game extends JPanel implements Runnable, KeyListener {

    public Game() {
        _running = false;
        _state = new Playing();
        _frame = new JFrame(Configs.TITLE);

        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setIconImage(ImageLoader.getWindowIcon());
        _frame.setResizable(false);
        
        setBackground(Configs.BACKGROUND_COLOR);
        setDoubleBuffered(true);
        setPreferredSize(Configs.WINDOW_SIZE);
        
        _frame.add(this);
        _frame.addKeyListener(this);
        _frame.pack();
    }

    /**
     * Starts the game thread by calling {@code run()}
     */
    public synchronized void enter() {
        if (_running)
            return;
        _running = true;
        _thread = new Thread(this);

        _thread.start();
        _frame.setVisible(true);
        _frame.requestFocus();
    }

    public synchronized void exit() {
        if (!_running)
            return;
        _running = false;
        try {
            _thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Do not call this directly.
     * Call {@code enter()} instead.
     */
    @Override
    public void run() {
        long timer = 0L;
        long lastTime = System.nanoTime();
        long lastLogTime = lastTime;
        int frames = 0, updates = 0;
        final long UPDATE_INTERVAL = (long)(1e9 / Configs.UPS_TARGET);

        while (_running) {
            long currentTime = System.nanoTime();
            timer += currentTime - lastTime;
            lastTime = currentTime;

            while (timer >= UPDATE_INTERVAL) {
                timer -= UPDATE_INTERVAL;
                ++updates;

                State nextState = _state.update();
                if (nextState == null) {
                    _state.exit();
                    exit();
                    break;
                } else if (nextState != _state) {
                    _state.exit();
                    _state = nextState;
                    _state.enter();
                }
            }

            if (!_running)
                break;
            repaint(); // calls paintComponent()
            ++frames;

            if (currentTime - lastLogTime >= (long)1e9) {
                _frame.setTitle(Configs.TITLE + ": " + updates + " UPS, " + frames + " FPS");
                lastLogTime = currentTime;
                updates = frames = 0;
            }

            Thread.yield();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D)g;
        _state.repaint(g2D);
        g2D.dispose();
    }

    // KeyListener methods gets called by _frame

    @Override
    public void keyPressed(KeyEvent e) {
        _state.input(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        _state.input(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        _state.input(e);
    }

    private boolean _running;
    private State _state;
    private JFrame _frame;
    private Thread _thread;
    
}