package com.karas.pacman.maps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.resources.ResourcesLoader;

public class Map implements ImmutableMap {

    public static Vector2 toGridVector2(Vector2 position) {
        return position.div(Configs.PX.TILE_SIZE).ceil();
    }

    public static Vector2 toPixelVector2(Vector2 gridPos) {
        return gridPos.sub(0.5).mul(Configs.PX.TILE_SIZE);
    }

    public static boolean isCenteredInTile(Vector2 position) {
        boolean[] centered = isXYCenteredInTile(position);
        return centered[0] && centered[1];
    }

    public Map() {
        _mapImage = ResourcesLoader.loadImage(Configs.MAP_PATH, true);
        _tiles = ResourcesLoader.loadTilemap(
            Configs.TILEMAP_PATH,
            Configs.Grid.MAP_SIZE.iy(),
            Configs.Grid.MAP_SIZE.ix()
        );

        _dotCounts = 0;
        for (int y = 0; y < _tiles.length; ++y)
            for (int x = 0; x < _tiles[y].length; ++x)
                if (_tiles[y][x] == Tile.DOT)
                    ++_dotCounts;
    }

    @Override
    public int getDotCounts() {
        return _dotCounts;
    }

    @Override
    public boolean isMovable(Vector2 gridPos) {
        int x = gridPos.ix(), y = gridPos.iy();
        return 0 <= y && y < _tiles.length
            && 0 <= x && x < _tiles[y].length
            && _tiles[y][x] != Tile.WALL;
    }

    @Override
    public boolean validDirection(Vector2 position, Direction nextDirection) {
        boolean[] centered = isXYCenteredInTile(position);
        boolean isXCentered = centered[0];
        boolean isYCentered = centered[1];
        
        if (isXCentered && isYCentered) {
            Vector2 p = toGridVector2(position).add(nextDirection.toVector2());
            return isMovable(p);
        }
        return nextDirection.isVertical() ? isXCentered : isYCentered;
    }

    @Override
    public Vector2 nearestMovableGridPos(Vector2 position) {
        Vector2 gridPos = position.div(Configs.PX.TILE_SIZE);
        Vector2 ceilGP = gridPos.ceil();
        Vector2 floorGP = gridPos.floor();
        int[] xs = { floorGP.ix(), ceilGP.ix() };
        int[] ys = { floorGP.iy(), ceilGP.iy() };

        Vector2 result = null;
        double minDist = Double.MAX_VALUE;
        for (int y : ys)
            for (int x : xs)
                if (_tiles[y][x] != Tile.WALL) {
                    Vector2 gp = new Vector2(x, y);
                    double dist = position.toDistance(toPixelVector2(gp));
                    if (minDist > dist) {
                        minDist = dist;
                        result = gp;
                    }
                }
        return result;
    }
    
    
    /** @return Tile value eaten at {@code position} */
    public Tile tryEatAt(Vector2 position) {
        if (isCenteredInTile(position))
            return Tile.NONE;

        Vector2 p = toGridVector2(position);
        Tile tile = _tiles[p.iy()][p.ix()];
        switch (tile) {
            case DOT:
                --_dotCounts;
            case POWERUP:
                _tiles[p.iy()][p.ix()] = Tile.NONE;
            default:
                break;
        }
        return tile;
    }

    public void repaint(Graphics2D g) {
        g.drawImage(_mapImage, 0, 0, Configs.UI.MAP_SIZE.ix(), Configs.UI.MAP_SIZE.iy(), null);

        for (int y = 0; y < _tiles.length; ++y)
            for (int x = 0; x < _tiles[y].length; ++x)
                if (_tiles[y][x] == Tile.DOT || _tiles[y][x] == Tile.POWERUP) {
                    BufferedImage image = _tiles[y][x] == Tile.DOT ? DOT_IMAGE : POWERUP_IMAGE;
                    int offset = (Configs.UI.TILE_SIZE - image.getWidth() / 2);
                    Vector2 p = toPixelVector2(new Vector2(x, y)).mul(Configs.SCALING).add(offset);
                    g.drawImage(image, p.ix(), p.iy(), null);
                }
    }


    /** @return { isXCentered, isYCentered } */
    private static boolean[] isXYCenteredInTile(Vector2 position) {
        final int HALF_TILE = Configs.PX.TILE_SIZE / 2;
        Vector2 p = position.mod(Configs.PX.TILE_SIZE).sub(HALF_TILE);
        return new boolean[] { p.ix() == 0, p.iy() == 0 };
    }

    private static BufferedImage createOval(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    
        g2.setColor(Color.WHITE);
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return image;
    }

    private static final BufferedImage DOT_IMAGE = createOval(Configs.UI.DOT_SIZE);
    private static final BufferedImage POWERUP_IMAGE = createOval(Configs.UI.POWERUP_SIZE);

    private int _dotCounts;
    private Tile[][] _tiles;
    private BufferedImage _mapImage;

}
