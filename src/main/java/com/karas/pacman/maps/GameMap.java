package com.karas.pacman.maps;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourceManager;
import com.karas.pacman.resources.Sound;

public final class GameMap implements ImmutableMap, Paintable {

    public static Vector2 toGridVector2(Vector2 position) {
        return position.div(Constants.Pixel.TILE_SIZE).ceil();
    }

    public static Vector2 toPixelVector2(Vector2 gridPosition) {
        return gridPosition.sub(0.5).mul(Constants.Pixel.TILE_SIZE);
    }

    public static boolean isCenteredInTile(Vector2 position) {
        final int HALF = Constants.Pixel.TILE_SIZE / 2;
        Vector2 p = position.mod(Constants.Pixel.TILE_SIZE).abs();
        return p.ix() == HALF && p.iy() == HALF;
    }

    public GameMap(ResourceManager ResourceMgr) {
        _MapImage = ResourceMgr.getImage(ResourceID.MAP_IMAGE);
        _PelletImage = createSquare(Constants.Pixel.PELLET_SIZE);
        _PowerupImage = createSquare(Constants.Pixel.POWERUP_SIZE);
        _WakaSounds = new Sound[] { 
            ResourceMgr.getSound(ResourceID.EAT_WA_SOUND), 
            ResourceMgr.getSound(ResourceID.EAT_KA_SOUND) 
        };
        _OriginalTilemap = ResourceMgr.getTilemap();
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
    public Tile tileAt(Vector2 gridPosition) {
        return _tiles[gridPosition.iy() * WIDTH + gridPosition.ix()];
    }

    @Override
    public Vector2 useTunnel(Vector2 gridPosition, Direction direction) {
        if (tileAt(gridPosition) != Tile.TUNNEL)
            return null;
        Vector2 dir = direction.opposite().vector2();
        return dir.mul(WIDTH - 1).add(gridPosition);
    }

    public Tile eatConsumable(Vector2 gridPosition) {
        Tile tile = tileAt(gridPosition);
        switch (tile) {
            case PELLET:
                _WakaSounds[_pelletCounts % 2].play();
                --_pelletCounts;

            case POWERUP:
                _tiles[gridPosition.iy() * WIDTH + gridPosition.ix()] = Tile.NONE;
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

    private static final int WIDTH = Constants.Grid.MAP_SIZE.ix();

    private final Sound[] _WakaSounds;
    private final BufferedImage _MapImage, _PelletImage, _PowerupImage;
    private final Tile[] _OriginalTilemap;

    private int _pelletCounts;
    private Tile[] _tiles;

}
