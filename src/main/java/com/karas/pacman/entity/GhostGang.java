package com.karas.pacman.entity;

import java.awt.Graphics2D;

import com.karas.pacman.Configs;
import com.karas.pacman.common.Direction;
import com.karas.pacman.map.Map;
import com.karas.pacman.resources.ImageLoader;

public class GhostGang {

    public static final int BLINKY=0, PINKY=1, INKY=2, CLYDE=3;

    public GhostGang() {
        _ghosts = new Ghost[] {
            new Ghost(Configs.GRID.BLINKY_POS, Configs.PX.BLINKY_SPEED, ImageLoader.getBlinky(), Direction.UP), 
            new Ghost(Configs.GRID.PINKY_POS,  Configs.PX.PINKY_SPEED,  ImageLoader.getPinky(),  Direction.LEFT), 
            new Ghost(Configs.GRID.INKY_POS,   Configs.PX.INKY_SPEED,   ImageLoader.getInky(),   Direction.RIGHT), 
            new Ghost(Configs.GRID.CLYDE_POS,  Configs.PX.CLYDE_SPEED,  ImageLoader.getClyde(),  Direction.DOWN), 
        };
    }

    public boolean caughtPacman(Pacman pacman) {
        for (Ghost ghost : _ghosts)
            if (ghost.isCollides(pacman))
                return true;
        return false;
    }

    public void update(double deltaTime, Map map, boolean isIdling) {
        
    }
    
    public void repaint(Graphics2D g) {
        for (Ghost ghost : _ghosts)
            ghost.repaint(g);
    }
    

    private Ghost[] _ghosts;
    
}
