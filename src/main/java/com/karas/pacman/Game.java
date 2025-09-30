package com.karas.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.karas.pacman.common.Directions;
import com.karas.pacman.entity.Pacman;
import com.karas.pacman.graphics.ImageLoader;

public class Game extends JPanel implements Runnable, KeyListener {

    public Game() {
        _running = false;
        _frame = new JFrame();
        _pacman = new Pacman();

        _frame.setTitle(Configs.TITLE);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setResizable(false);
        _frame.setIconImage(ImageLoader.getWindowIcon());
        
        super.setBackground(Color.BLACK);
        super.setDoubleBuffered(true);
        super.setPreferredSize(new Dimension(1000, 1000));
        
        _frame.add(this);
        _frame.addKeyListener(this);
        _frame.pack();
    }

    public synchronized void start() {
        if (_running)
            return;
        _running = true;
        _thread = new Thread(this);

        _thread.start();
        _frame.setVisible(true);
        _frame.requestFocus();
    }

    @Override
    public void run() {
        int frames = 0;
        int updates = 0;
        long timer = 0;
        long lastTime = System.nanoTime();
        long lastLogTime = System.nanoTime();
        final long UPDATE_INTERVAL = (long)(1e9 / Configs.UPS_TARGET);

        while (_running) {
            long currentTime = System.nanoTime();
            timer += currentTime - lastTime;
            lastTime = currentTime;

            while (timer >= UPDATE_INTERVAL) {
                updateGame();
                timer -= UPDATE_INTERVAL;
                ++updates;
            }

            super.repaint();
            ++frames;

            if (currentTime - lastLogTime >= (long)1e9) {
                _frame.setTitle(Configs.TITLE + ": " + updates + " UPS, " + frames + " FPS");
                lastLogTime = currentTime;
                updates = frames = 0;
            }

            Thread.yield();
        }
    }

    private void updateGame() {
        _pacman.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D)g;
        _pacman.repaint(g2D);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int direction = -1;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                direction = Directions.UP;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                direction = Directions.RIGHT;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                direction = Directions.DOWN;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                direction = Directions.LEFT;
                break;
            default:
                break;
        }

        if (direction != -1) {
            _pacman.setNextDirection(direction);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private boolean _running;
    private JFrame _frame;
    private Thread _thread;
    private Pacman _pacman;
}