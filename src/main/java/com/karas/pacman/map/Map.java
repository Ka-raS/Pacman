package com.karas.pacman.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.common.Vector2;
import com.karas.pacman.resources.ImageLoader;
import com.karas.pacman.resources.MapTileLoader;

public class Map {

    public static Vector2 toGridVector2(Vector2 p) {
        return p.div(Configs.PX.TILE_SIZE).add(1); // TODO: investigate +1
    }

    public static Vector2 toPixelVector2(Vector2 gridP) {
        return gridP.sub(0.5).mul(Configs.PX.TILE_SIZE);
    }

    public static boolean isCenteredInTile(Vector2 p) {
        p = p.add(Configs.PX.TILE_SIZE / 2).mod(Configs.PX.TILE_SIZE);
        return p.ix() == 0 && p.iy() == 0;
    }

    public Map() {
        _mapImage = ImageLoader.getMap();
        _tiles = MapTileLoader.loadMapTile();
    }

    public void repaint(Graphics2D g) {
        g.drawImage(_mapImage, 0, 0, Configs.UI.MAP_SIZE.ix(), Configs.UI.MAP_SIZE.iy(), null);
    }

    public Tile getTile(Vector2 p) {
        p = toGridVector2(p);
        return _tiles[p.iy()][p.ix()];
    }

    private BufferedImage _mapImage;
    private Tile[][] _tiles;

}
