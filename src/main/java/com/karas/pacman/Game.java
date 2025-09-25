package com.karas.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.karas.pacman.entity.Pacman;
import com.karas.pacman.entity.Entity.Direction;

public class Game extends JPanel implements KeyListener {

    public Game() {
        m_running = false;
        m_frame = new JFrame();
        m_pacman = new Pacman();

        m_frame.setTitle(Configs.TITLE);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.setResizable(false);
        
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(1000, 1000));
        
        m_frame.add(this);
        m_frame.addKeyListener(this);
        m_frame.pack();
    }

    public void run() {
        m_running = true;
        m_frame.setVisible(true);
        m_frame.requestFocus();

        int frames = 0;
        int updates = 0;
        float deltaTime = 0.0f;
        long lastTime = System.nanoTime();
        long lastLogTime = System.currentTimeMillis();

        while (m_running) {
            long currentTime = System.nanoTime();
            deltaTime += (currentTime - lastTime) / (1e9f / Configs.UPS_TARGET);
            lastTime = currentTime;
            if (deltaTime >= 1.0f) {
                update();
                ++updates;
                --deltaTime;
            }

            repaint();
            ++frames;
            if (System.currentTimeMillis() - lastLogTime >= 1000) {
                System.out.printf("\r%d UPS, %d FPS", updates, frames);
                frames = updates = 0;
                lastLogTime += 1000;
            }
        }
    }

    private void update() {
        m_pacman.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D)g;
        m_pacman.render(g2D, this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Direction direction = null;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                direction = Direction.UP;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                direction = Direction.RIGHT;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                direction = Direction.DOWN;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                direction = Direction.LEFT;
                break;
            default:
                break;
        }

        if (direction != null) {
            m_pacman.setNextDirection(direction);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private JFrame m_frame;
    private boolean m_running;
    private Pacman m_pacman;
}