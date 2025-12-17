package com.karas.pacman.maps;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Configs;
import com.karas.pacman.audio.Sound;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.resources.Resource;
import com.karas.pacman.resources.ResourcesManager;

public class Map implements ImmutableMap, Paintable {

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

    public Map(ResourcesManager ResourcesMgr) {
        _MapImage = ResourcesMgr.getImage(Resource.MAP_IMAGE);
        _PelletImage = createSquare(Configs.PX.PELLET_SIZE);
        _PowerupImage = createSquare(Configs.PX.POWERUP_SIZE);
        _WakaSounds = new Sound[] { 
            ResourcesMgr.getSound(Resource.EAT_WA_SOUND), 
            ResourcesMgr.getSound(Resource.EAT_KA_SOUND) 
        };
        _OriginalTilemap = ResourcesMgr.getTilemap();
        reset();
    }

    public void reset() {
        Tile[][] copy = new Tile[_OriginalTilemap.length][];
        for (int y = 0; y < _OriginalTilemap.length; ++y)
            copy[y] = _OriginalTilemap[y].clone();

        _tiles = copy;
        _pelletCounts = 0;
        for (Tile[] row : _tiles)
            for (Tile tile : row)
                if (tile == Tile.PELLET)
                    ++_pelletCounts;
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
    public boolean isMovableAt(Vector2 gridPosition) {
        int x = gridPosition.ix(), y = gridPosition.iy();
        if ((double) x != gridPosition.x() || (double) y != gridPosition.y())
            return checkBound(gridPosition);
        return checkBound(gridPosition) && _tiles[y][x] != Tile.WALL;
    }

    @Override
    public boolean canMoveInDirection(Vector2 position, Direction nextDirection) {
        boolean[] centered = isXYCenteredInTile(position);
        boolean isXCentered = centered[0];
        boolean isYCentered = centered[1];
        
        if (isXCentered && isYCentered) {
            Vector2 p = toGridVector2(position).add(nextDirection.toVector2());
            return isMovableAt(p);
        }
        
        boolean currentDirectionIsVertical = isXCentered;
        return nextDirection.isVertical() == currentDirectionIsVertical;
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
        G.drawImage(_MapImage, 0, 0, null);
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

    private static BufferedImage createSquare(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();        
        g.setColor(Configs.Color.PELLET);
        g.fillRect(0, 0, size, size);
        g.dispose();
        return image;
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
                int offset = (Configs.PX.TILE_SIZE - image.getWidth() / 2);
                Vector2 p = toPixelVector2(new Vector2(x, y)).add(offset);
                G.drawImage(image, p.ix(), p.iy(), null);
                
            }
    }

    private final Sound[] _WakaSounds;
    private final BufferedImage _MapImage, _PelletImage, _PowerupImage;
    private final Tile[][] _OriginalTilemap;

    private int _pelletCounts;
    private Tile[][] _tiles;

}
