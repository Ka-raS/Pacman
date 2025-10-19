package com.karas.pacman.resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.karas.pacman.Configs;
import com.karas.pacman.map.Tile;

public class MapTileLoader {
    public static Tile[][] loadMapTile() {
        Tile[][] tiles = new Tile[Configs.MAP_GRID.iy()][Configs.MAP_GRID.ix()];
        try {
            InputStreamReader isReader = new InputStreamReader(MapTileLoader.class.getResourceAsStream(Configs.MAPTILE_PATH));
            BufferedReader reader = new BufferedReader(isReader);

            for (int r = 0; r < tiles.length; ++r) {
                String line = reader.readLine();
                for (int c = 0; c < tiles[0].length; ++c)
                    tiles[r][c] = Tile.fromChar(line.charAt(c));                
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Load maptile failed: " + e.getMessage());
            System.exit(1);
        }
        return tiles;
    }
}
