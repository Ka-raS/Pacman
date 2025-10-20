package com.karas.pacman;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        _frame.setIconImage(ImageLoader.getWindowIcon());
        _frame.setResizable(false);

        setBackground(Configs.BACKGROUND_COLOR);
        setDoubleBuffered(true);
        setPreferredSize(Configs.WINDOW_SIZE);
        
        _frame.add(this);
        _frame.addKeyListener(this);
        _frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        _frame.pack();
    }

    public synchronized void enter() {
        if (_running)
            return;
        _running = true;
        _thread = new Thread(this, "Game Thread");

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
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Join game thread Failed.");
                System.exit(1);
            }
        }
    }

    /** {@code Runnable} method. Start the game with {@code enter} instead. */
    @Override
    public void run() {
        int frames = 0;
        double logTimer = 0.0;
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
            repaint(); // calls paintComponent()
            ++frames;

            logTimer += deltaTime;
            if (logTimer >= 1.0) {
                _frame.setTitle(Configs.TITLE + ": " + frames + " FPS");
                logTimer = frames = 0;
            }
        }
    }

    private void updateGame(double deltaTime) {
        State nextState = _state.update(deltaTime);
        if (nextState == null) {
            _state.exit();
            exit();
        } else if (nextState != _state) {
            _state.exit();
            _state = nextState;
            _state.enter();
        }
    }

    /** {@code JPanel} method. Gets called by {@code repaint()} only. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        _state.repaint(g2D);
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
    private State _state;
    private JFrame _frame;
    private Thread _thread;

}