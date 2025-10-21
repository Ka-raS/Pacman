package com.karas.pacman.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.karas.pacman.Configs;
import com.karas.pacman.common.ExceptionHandler;
import com.karas.pacman.map.Tile;

public class MapTileLoader {
    public static Tile[][] loadMapTile() {
        Tile[][] tiles = new Tile[Configs.GRID.MAP_SIZE.iy()][Configs.GRID.MAP_SIZE.ix()];
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(MapTileLoader.class.getResourceAsStream(Configs.MAPTILE_PATH))
            );
            for (int r = 0; r < tiles.length; ++r) {
                String line = reader.readLine();
                for (int c = 0; c < tiles[0].length; ++c)
                    tiles[r][c] = Tile.fromChar(line.charAt(c));                
            }

        } catch (NullPointerException e) {
            ExceptionHandler.handleCritical(e, Configs.MAPTILE_PATH + " Not Found");
        } catch (IOException e) {
            ExceptionHandler.handleCritical(e, "Failed reading " + Configs.MAPTILE_PATH);
        }
        return tiles;
    }
}
