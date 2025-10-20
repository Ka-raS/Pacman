package com.karas.pacman.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.common.Direction;
import com.karas.pacman.common.Vector2;
import com.karas.pacman.resources.ImageLoader;
import com.karas.pacman.resources.MapTileLoader;

public class Map {

    public static Vector2 toGridVector2(Vector2 p) {
        return p.div(Configs.PX.TILE_SIZE).ceil();
    }

    public static Vector2 toPixelVector2(Vector2 gridP) {
        return gridP.sub(0.5).mul(Configs.PX.TILE_SIZE);
    }

    public static boolean isCenteredInTile(Vector2 p) {
        final int CENTER = Configs.PX.TILE_SIZE / 2;
        p = p.mod(Configs.PX.TILE_SIZE);
        return p.ix() == CENTER && p.iy() == CENTER;
    }

    public Map() {
        _mapImage = ImageLoader.getMap();
        _tiles = MapTileLoader.loadMapTile();
        _dotCounts = 0;
        for (Tile[] row : _tiles)
            for (Tile t : row)
                if (t == Tile.DOT)
                    ++_dotCounts;

    }

    public int getDotCounts() {
        return _dotCounts;
    }

    public void tryEatDotAt(Vector2 p) {
        if (!isCenteredInTile(p))
            return;
        p = toGridVector2(p);
        if (_tiles[p.iy()][p.ix()] == Tile.DOT) {
            _tiles[p.iy()][p.ix()] = Tile.NONE;
            --_dotCounts;
        }
    }

    public boolean tryEatPowerupAt(Vector2 p) {
        if (!isCenteredInTile(p))
            return false;
        p = toGridVector2(p);
        if (_tiles[p.iy()][p.ix()] == Tile.POWERUP) {
            _tiles[p.iy()][p.ix()] = Tile.NONE;
            return true;
        }
        return false;
    }
    
    public boolean willHitWall(Vector2 p, Direction d) {
        Vector2 halfTile = Direction.toVector2(d).mul(Configs.PX.TILE_SIZE / 2);
        p = p.add(halfTile);
        if (d == Direction.LEFT || d == Direction.UP)
            p = p.floor(); // TODO: why???
        p = toGridVector2(p);
        return _tiles[p.iy()][p.ix()] == Tile.WALL;
    }

    public void repaint(Graphics2D g) {
        g.drawImage(_mapImage, 0, 0, Configs.UI.MAP_SIZE.ix(), Configs.UI.MAP_SIZE.iy(), null);

        for (int y = 0; y < _tiles.length; y++) {
            for (int x = 0; x < _tiles[y].length; x++) {
                if (_tiles[y][x] == Tile.DOT)
                    paintDot(g, toPixelVector2(new Vector2(x, y)));
                else if (_tiles[y][x] == Tile.POWERUP)
                    paintPowerup(g, toPixelVector2(new Vector2(x, y)));
            }
        }
    }

    private void paintDot(Graphics2D g, Vector2 p) {
        p = p.mul(Configs.SCALING);
        final int OFFSET = Configs.UI.TILE_SIZE - Configs.UI.DOT_SIZE / 2;
        g.setColor(Color.WHITE);
        g.fillOval(p.ix() + OFFSET, p.iy() + OFFSET, Configs.UI.DOT_SIZE, Configs.UI.DOT_SIZE);
    }
    
    private void paintPowerup(Graphics2D g, Vector2 p) {
        p = p.mul(Configs.SCALING);
        final int OFFSET = Configs.UI.TILE_SIZE - Configs.UI.POWERUP_SIZE / 2;
        g.setColor(Color.WHITE);
        g.fillOval(p.ix() + OFFSET, p.iy() + OFFSET, Configs.UI.POWERUP_SIZE, Configs.UI.POWERUP_SIZE);
    }

    private BufferedImage _mapImage;
    private Tile[][] _tiles;
    private int _dotCounts;

}
