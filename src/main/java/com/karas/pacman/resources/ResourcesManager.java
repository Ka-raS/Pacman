package com.karas.pacman.resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.Tile;

public class ResourcesManager {

    public ResourcesManager() {
        initSoundMap();
        initImageMap();
        initSpriteMap();
        initTilemap();
        initFont();


        try {
            _font = loadFont(Resource.FONT.getPath(), Configs.UI.FONT_SIZE_BASE);
        } catch (RuntimeException e) {
            handleException(e, Resource.FONT.isCritical());
            _font = new Font("Arial", Font.PLAIN, (int) Configs.UI.FONT_SIZE_BASE);
        }
    }

    public Sound getSound(Resource sound) {
        return _soundMap.getOrDefault(sound, null);
    }

    public BufferedImage getImage(Resource image) {
        return _imageMap.getOrDefault(image, null);
    }

    public BufferedImage[] getSprite(SpriteSheet sheet) {
        return _spriteMap.getOrDefault(sheet, null);
    }

    public Tile[][] getTilemap() {
        Tile[][] copy = new Tile[_tilemap.length][];
        for (int y = 0; y < _tilemap.length; ++y)
            copy[y] = _tilemap[y].clone();
        return copy;
    }

    public Font getFont(float size) { 
        return _font.deriveFont(size); 
    }

    public void exit() {
        for (Sound sound : _soundMap.values())
            sound.close();
        _soundMap.clear();
        _imageMap.clear();
        _spriteMap.clear();
        _tilemap = null;
        _font = null;
    }


    private static Sound loadSound(String path) throws RuntimeException {
        try (
            InputStream is = ResourcesManager.class.getResourceAsStream(path);
            AudioInputStream ais = is == null ? null : AudioSystem.getAudioInputStream(is)
        ) {
            if (is == null)
                throw new FileNotFoundException();
            
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return new Sound(clip);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Sound Not Found: " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed Reading Sound: " + path, e);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException("Unsupported Audio File: " + path, e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException("Audio Line Unavailable: " + path, e);
        }
    }

    private static BufferedImage loadImage(String path) throws RuntimeException {
        try (InputStream is = ResourcesManager.class.getResourceAsStream(path)) {
            if (is == null)
                throw new FileNotFoundException();
            
            BufferedImage bufferedImg = ImageIO.read(is);
            if (bufferedImg == null)
                throw new NullPointerException();
            return bufferedImg;

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Image Not Found: " + path, e);
        } catch (NullPointerException e) {
            throw new RuntimeException("Invalid Image Format: " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed Reading Image: " + path, e);
        }
    }

    private static Tile[][] loadTilemap(String path, Vector2 size) throws RuntimeException {
        try (
            InputStream is = ResourcesManager.class.getResourceAsStream(path);
            BufferedReader reader = is == null ? null : new BufferedReader(new InputStreamReader(is))
        ) {
            if (is == null)
                throw new FileNotFoundException();

            Tile[][] tiles = new Tile[size.iy()][size.ix()];

            for (int y = 0; y < size.iy(); ++y) {
                String line = reader.readLine();
                if (line == null || line.length() != tiles[0].length)
                    throw new IllegalArgumentException("Tilemap Data Corrupted At Line " + (y+1));
                    
                for (int x = 0; x < size.ix(); ++x) {
                    tiles[y][x] = Tile.fromChar(line.charAt(x));
                    if (tiles[y][x] == null)
                        throw new IllegalArgumentException(
                            String.format("Tilemap Data Corrupted At Line %d Column %d", y+1, x+1)
                        );
                }
            }
            return tiles;

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Tilemap Not Found: " + path, e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage() + ": " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed Reading Tilemap: " + path, e);
        }
    }

    private static Font loadFont(String path, float size) throws RuntimeException {
        try (InputStream is = ResourcesManager.class.getResourceAsStream(path)) {
            if (is == null)
                throw new FileNotFoundException();
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont((float) size);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Font Not Found: " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed Reading Font: " + path, e);
        } catch (FontFormatException e) {
            throw new RuntimeException("Invalid Font Format: " + path, e);
        }
    }

    private static void handleException(Exception e, boolean isCritical) {
        if (!isCritical) {
            _LOGGER.log(Level.WARNING, e.getMessage(), e);
            return;
        }

        _LOGGER.log(Level.SEVERE, e.getMessage(), e);
        JOptionPane.showMessageDialog(
            null,
            e.getMessage(),
            "ERROR",
            JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }

    private void initSoundMap() {
        Resource[] sounds = { 
            Resource.EAT_WA_SOUND, Resource.EAT_KA_SOUND, Resource.PACMAN_DEATH_SOUND, Resource.GHOST_DEATH_SOUND, 
            Resource.GAME_START_SOUND, Resource.GAME_NORMAL_SOUND, Resource.GAME_POWERUP_SOUND
        };
        _soundMap = new HashMap<>(sounds.length);

        for (Resource sound : sounds) {
            try {
                _soundMap.put(sound, loadSound(sound.getPath()));
            } catch (RuntimeException e) {
                handleException(e, sound.isCritical());
                _soundMap.put(sound, Sound.getDummy());
            }
        }
    }

    private void initImageMap() {
        Resource[] images = { Resource.WINDOW_ICON, Resource.TITLE_IMAGE, Resource.MAP_IMAGE, Resource.SPRITE_SHEET };
        _imageMap = new HashMap<>(images.length);

        for (Resource image : images) {
            try {
                _imageMap.put(image, loadImage(image.getPath()));
            } catch (RuntimeException e) {
                handleException(e, image.isCritical());
                _imageMap.put(image, null);
            }
        }
    }

    private void initSpriteMap() {
        BufferedImage sheetImage = _imageMap.get(Resource.SPRITE_SHEET);
        _spriteMap = new HashMap<>(SpriteSheet.values().length);
        
        if (sheetImage == null) {
            _LOGGER.warning("Sprite sheet not initialized.");
            for (SpriteSheet sheet : SpriteSheet.values())
                _spriteMap.put(sheet, null);
            return;
        }

        final int SIZE = Configs.PX.SPRITE_SIZE;
        for (SpriteSheet sheet : SpriteSheet.values()) {
            BufferedImage[] sprite = new BufferedImage[sheet.getLen()];
            for (int i = 0; i < sheet.getLen(); ++i)
                sprite[i] = sheetImage.getSubimage((sheet.getCol() + i) * SIZE, sheet.getRow() * SIZE, SIZE, SIZE);
            _spriteMap.put(sheet, sprite);
        }
    }

    private void initTilemap() {
        final Vector2 SIZE = Configs.Grid.MAP_SIZE;
        try {
            _tilemap = loadTilemap(Resource.TILEMAP.getPath(), SIZE);
        } catch (RuntimeException e) {
            handleException(e, Resource.TILEMAP.isCritical());
            _tilemap = new Tile[SIZE.iy()][SIZE.ix()];
            for (Tile[] row : _tilemap)
                for (int i = 0; i < SIZE.ix(); ++i)
                    row[i] = Tile.NONE;
        }
    }

    private void initFont() {

    }

    private static final Logger _LOGGER = Logger.getLogger(ResourcesManager.class.getName());

    private HashMap<Resource, Sound> _soundMap;
    private HashMap<Resource, BufferedImage> _imageMap;
    private HashMap<SpriteSheet, BufferedImage[]> _spriteMap;
    private Tile[][] _tilemap;
    private Font _font;

}

// IHateSingleton.java