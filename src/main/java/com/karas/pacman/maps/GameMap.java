package com.karas.pacman.maps;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.Sound;

public final class GameMap implements ImmutableMap, Paintable {

    public static Vector2 toGridVector2(Vector2 position) {
        return position.div(Constants.Pixel.TILE_SIZE).ceil();
    }

    public static Vector2 toPixelVector2(Vector2 gridPosition) {
        return gridPosition.sub(0.5).mul(Constants.Pixel.TILE_SIZE);
    }

    public static boolean isCenteredInTile(Vector2 position) {
        Vector2 p = position.mod(Constants.Pixel.TILE_SIZE).abs();
        return p.ix() == HALF_TILE && p.iy() == HALF_TILE;
    }

    public GameMap(ResourcesManager ResourcesMgr) {
        _MapImage = ResourcesMgr.getImage(ResourceID.MAP_IMAGE);
        _PelletImage = createSquare(Constants.Pixel.PELLET_SIZE);
        _PowerupImage = createSquare(Constants.Pixel.POWERUP_SIZE);
        _WakaSounds = new Sound[] { 
            ResourcesMgr.getSound(ResourceID.EAT_WA_SOUND), 
            ResourcesMgr.getSound(ResourceID.EAT_KA_SOUND) 
        };
        _OriginalTilemap = ResourcesMgr.getTilemap();
        reset();
    }

    public void reset() {
        _tiles = _OriginalTilemap.clone();
        _pelletCounts = 0;
        for (Tile tile : _OriginalTilemap)
            if (tile == Tile.PELLET)
                ++_pelletCounts;
    }

    public int getPelletCounts() {
        return _pelletCounts;
    }

    @Override
    public Vector2 tryTunneling(Vector2 position, Direction direction) {
        Vector2 p = toGridVector2(position);
        if (_tiles[p.iy() * WIDTH + p.ix()] != Tile.TUNNEL)
            return null;

        Vector2 dir = direction.opposite().toVector2();
        p = dir.mul(WIDTH - 1).add(p);
        return checkBound(p) ? GameMap.toPixelVector2(p) : null;
    }

    @Override
    public boolean canMoveInDirection(Vector2 position, Direction nextDirection) {
        Vector2 p = position.mod(Constants.Pixel.TILE_SIZE).abs();
        boolean isXCentered = p.ix() == HALF_TILE;
        boolean isYCentered = p.iy() == HALF_TILE;
        
        if (isXCentered && isYCentered) {
            p = toGridVector2(position).add(nextDirection.toVector2());
            return checkBound(p) && _tiles[p.iy() * WIDTH + p.ix()] != Tile.WALL;
        }
        
        boolean currentDirectionIsVertical = isXCentered;
        return nextDirection.isVertical() == currentDirectionIsVertical;
    }

    /** @return Tile value eaten at {@code position} */
    public Tile tryEatAt(Vector2 position) {
        Vector2 p = toGridVector2(position);
        Tile tile = _tiles[p.iy() * WIDTH + p.ix()];
        switch (tile) {
            case PELLET:
                _WakaSounds[_pelletCounts % 2].play();
                --_pelletCounts;

            case POWERUP:
                _tiles[p.iy() * WIDTH + p.ix()] = Tile.NONE;
                return tile;

            default:
                return Tile.NONE;
        }
    }

    @Override
    public void repaint(Graphics2D G) {
        G.drawImage(_MapImage, 0, 0, null);
        paintConsumables(G);
    }


    private static boolean checkBound(Vector2 gridPosition) {
        int x = gridPosition.ix(), y = gridPosition.iy();
        return 0 <= y && y < Constants.Grid.MAP_SIZE.iy()
            && 0 <= x && x < Constants.Grid.MAP_SIZE.ix();
    }

    private static BufferedImage createSquare(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();        
        g.setColor(Constants.Color.PELLET);
        g.fillRect(0, 0, size, size);
        g.dispose();
        return image;
    }

    private void paintConsumables(Graphics2D G) {
        for (int i = 0; i < _tiles.length; ++i) {
            BufferedImage image = switch (_tiles[i]) {
                case PELLET  -> _PelletImage;
                case POWERUP -> _PowerupImage;
                default      -> null;
            };
            if (image == null)
                continue;
            int offset = (Constants.Pixel.TILE_SIZE - image.getWidth() / 2);
            Vector2 p = toPixelVector2(new Vector2(i % WIDTH, i / WIDTH)).add(offset);
            G.drawImage(image, p.ix(), p.iy(), null);
        }
    }

    private static final int HALF_TILE = Constants.Pixel.TILE_SIZE / 2;
    private static final int WIDTH     = Constants.Grid.MAP_SIZE.ix();

    private final Sound[] _WakaSounds;
    private final BufferedImage _MapImage, _PelletImage, _PowerupImage;
    private final Tile[] _OriginalTilemap;

    private int _pelletCounts;
    private Tile[] _tiles;

}
