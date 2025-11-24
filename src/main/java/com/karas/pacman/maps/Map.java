package com.karas.pacman.maps;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Drawable;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.resources.Sound;

public class Map implements ImmutableMap, Drawable {

    public static Vector2 toGridVector2(Vector2 position) {
        return position.div(Configs.PX.TILE_SIZE).ceil();
    }

    public static Vector2 toPixelVector2(Vector2 gridPosition) {
        return gridPosition.sub(0.5).mul(Configs.PX.TILE_SIZE);
    }

    public static boolean isCenteredInTile(Vector2 position) {
        boolean[] centered = isXYCenteredInTile(position);
        return centered[0] && centered[1];
    }

    public Map(Tile[][] tileMap, BufferedImage MapImage, BufferedImage[] PelletImages, Sound WaSound, Sound KaSound) {
        setTilemap(tileMap);
        _MapImage = MapImage;
        _PelletImage = PelletImages[0];
        _PowerupImage = PelletImages[1];
        _WakaSounds = new Sound[] { WaSound, KaSound };
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
    public boolean isNotWall(Vector2 gridPosition) {
        int x = gridPosition.ix(), y = gridPosition.iy();
        return checkBound(gridPosition) && _tiles[y][x] != Tile.WALL;
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

    @Override
    public void repaint(Graphics2D G) {
        G.drawImage(_MapImage, 0, 0, Configs.UI.MAP_SIZE.ix(), Configs.UI.MAP_SIZE.iy(), null);
        paintConsumables(G);
    }


    /** @return { isXCentered, isYCentered } */
    private static boolean[] isXYCenteredInTile(Vector2 position) {
        Vector2 p = position.mod(Configs.PX.TILE_SIZE).abs()
                            .sub(Configs.PX.TILE_SIZE / 2);
        return new boolean[] { p.ix() == 0, p.iy() == 0 };
    }

    private static boolean checkBound(Vector2 gridPosition) {
        int x = gridPosition.ix(), y = gridPosition.iy();
        return 0 <= y && y < Configs.Grid.MAP_SIZE.iy()
            && 0 <= x && x < Configs.Grid.MAP_SIZE.ix();
    }

    private void setTilemap(Tile[][] tileMap) {
        _tiles = tileMap;
        _pelletCounts = 0;
        for (Tile[] row : _tiles)
            for (Tile tile : row)
                if (tile == Tile.PELLET)
                    ++_pelletCounts;
    }

    private void paintConsumables(Graphics2D G) {
        for (int y = 0; y < _tiles.length; ++y)
            for (int x = 0; x < _tiles[y].length; ++x) {
                BufferedImage image = switch (_tiles[y][x]) {
                    case PELLET  -> _PelletImage;
                    case POWERUP -> _PowerupImage;
                    default      -> null;
                };
                if (image == null)
                    continue;
                int offset = (Configs.UI.TILE_SIZE - image.getWidth() / 2);
                Vector2 p = toPixelVector2(new Vector2(x, y)).mul(Configs.SCALING).add(offset);
                G.drawImage(image, p.ix(), p.iy(), null);
                
            }
    }

    private final Sound[] _WakaSounds;
    private final BufferedImage _MapImage, _PelletImage, _PowerupImage;

    private int _pelletCounts;
    private Tile[][] _tiles;

}
