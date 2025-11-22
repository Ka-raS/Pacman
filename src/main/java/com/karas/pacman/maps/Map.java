package com.karas.pacman.maps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.resources.Sound;

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

    public Map(BufferedImage mapImgae, Tile[][] tileMap, Sound waSound, Sound kaSound) {
        _mapImage = mapImgae;
        setTilemap(tileMap);
        _WakaSounds = new Sound[] { waSound, kaSound };
    }

    public void reset(Tile[][] tileMap) {
        setTilemap(tileMap);
    }

    @Override
    public int getPelletCounts() {
        return _pelletCounts;
    }

    @Override
    public Vector2 tryTunneling(Vector2 position, Direction direction) {
        if (!isCenteredInTile(position))
            return null;

        Vector2 p = toGridVector2(position);
        if (!checkBound(p) || _tiles[p.iy()][p.ix()] != Tile.TUNNEL)
            return null;

        Vector2 dir = direction.opposite().toVector2();
        p = dir.mul(Configs.Grid.MAP_SIZE.ix() - 1).add(p);
        if (!checkBound(p) || _tiles[p.iy()][p.ix()] != Tile.TUNNEL)
            return null;

        return Map.toPixelVector2(p);
    }

    @Override
    public boolean isNotWall(Vector2 gridPos) {
        int x = gridPos.ix(), y = gridPos.iy();
        return checkBound(gridPos) && _tiles[y][x] != Tile.WALL;
    }

    @Override
    public boolean isValidDirection(Vector2 position, Direction nextDirection) {
        boolean[] centered = isXYCenteredInTile(position);
        boolean isXCentered = centered[0];
        boolean isYCentered = centered[1];
        
        if (isXCentered && isYCentered) {
            Vector2 p = toGridVector2(position).add(nextDirection.toVector2());
            return isNotWall(p);
        }
        return nextDirection.isVertical() ? isXCentered : isYCentered;
    }

    /** @return Tile value eaten at {@code position} */
    public Tile tryEatAt(Vector2 position) {
        if (isCenteredInTile(position))
            return Tile.NONE;
        Vector2 p = toGridVector2(position);
        if (!checkBound(p))
            return Tile.NONE;

        Tile tile = _tiles[p.iy()][p.ix()];
        switch (tile) {
            case PELLET:
                _WakaSounds[_pelletCounts % 2].play();
                --_pelletCounts;

            case POWERUP:
                _tiles[p.iy()][p.ix()] = Tile.NONE;
                break;
                
            default:
                break;
        }
        return tile;
    }

    public void repaint(Graphics2D g) {
        g.drawImage(_mapImage, 0, 0, Configs.UI.MAP_SIZE.ix(), Configs.UI.MAP_SIZE.iy(), null);
        paintConsumables(g);
    }


    /** @return { isXCentered, isYCentered } */
    private static boolean[] isXYCenteredInTile(Vector2 position) {
        Vector2 p = position.mod(Configs.PX.TILE_SIZE).abs()
                            .sub(Configs.PX.TILE_SIZE / 2);
        return new boolean[] { p.ix() == 0, p.iy() == 0 };
    }

    private static boolean checkBound(Vector2 gridPos) {
        int x = gridPos.ix(), y = gridPos.iy();
        return 0 <= y && y < Configs.Grid.MAP_SIZE.iy()
            && 0 <= x && x < Configs.Grid.MAP_SIZE.ix();
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

    private void setTilemap(Tile[][] tileMap) {
        _tiles = tileMap;
        _pelletCounts = 0;
        for (Tile[] row : _tiles)
            for (Tile tile : row)
                if (tile == Tile.PELLET)
                    ++_pelletCounts;
    }

    private void paintConsumables(Graphics2D g) {
        for (int y = 0; y < _tiles.length; ++y)
            for (int x = 0; x < _tiles[y].length; ++x) {
                BufferedImage image = switch (_tiles[y][x]) {
                    case PELLET  -> _PELLET_IMAGE;
                    case POWERUP -> _POWERUP_IMAGE;
                    default      -> null;
                };
                if (image == null)
                    continue;
                int offset = (Configs.UI.TILE_SIZE - image.getWidth() / 2);
                Vector2 p = toPixelVector2(new Vector2(x, y)).mul(Configs.SCALING).add(offset);
                g.drawImage(image, p.ix(), p.iy(), null);
                
            }
    }

    private static final BufferedImage _PELLET_IMAGE = createOval(Configs.UI.PELLET_SIZE);
    private static final BufferedImage _POWERUP_IMAGE = createOval(Configs.UI.POWERUP_SIZE);
    
    private final Sound[] _WakaSounds;
    private int _pelletCounts;
    private Tile[][] _tiles;
    private BufferedImage _mapImage;

}
