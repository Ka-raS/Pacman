package com.karas.pacman.resources;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import com.karas.pacman.commons.ExceptionHandler;
import com.karas.pacman.maps.Tile;

public class ResourcesLoader {

    public static BufferedImage loadImage(String path, boolean isCritical) {
        BufferedImage image = null;
        
        try (InputStream is = ResourcesLoader.class.getResourceAsStream(path)) {
            if (is == null)
                throw new FileNotFoundException("Image Not Found: " + path);
            image = ImageIO.read(is);
            if (image == null)
                throw new IOException("Failed Reading Image: " + path);

        } catch (IOException e) {
            if (isCritical)
                ExceptionHandler.handleCritical(e, e.getMessage());
            else
                ExceptionHandler.handleGeneric(e, e.getMessage());
        }

        return image;
    }

    public static Tile[][] loadTilemap(String path, int rows, int columns) {
        Tile[][] tiles = new Tile[rows][columns];

        try (InputStream is = ResourcesLoader.class.getResourceAsStream(path)) {
            if (is == null)
                throw new FileNotFoundException("Tilemap Not Found: " + path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            for (int y = 0; y < rows; ++y) {
                String line = reader.readLine();
                if (line == null || line.length() != tiles[0].length)
                    throw new IllegalArgumentException("Tilemap Data Corrupted: " + path);

                for (int x = 0; x < columns; ++x) {
                    tiles[y][x] = Tile.fromChar(line.charAt(x));
                    if (tiles[y][x] == null)
                        throw new IllegalArgumentException("Tilemap Data Corrupted: " + path);
                }
            }

        } catch (IOException | IllegalArgumentException e) {
            ExceptionHandler.handleCritical(e, e.getMessage());
        }

        return tiles;
    }


    private ResourcesLoader() {}

}
