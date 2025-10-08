package com.karas.pacman.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.resources.ImageLoader;

public class Map {

    public Map() {
        _mapImage = ImageLoader.getMap();
    }

    public void repaint(Graphics2D g) {
        g.drawImage(_mapImage, 0, 0, Configs.MAP_SIZE_UI.x(), Configs.MAP_SIZE_UI.y(), null);
    }

    private BufferedImage _mapImage;
    
}
