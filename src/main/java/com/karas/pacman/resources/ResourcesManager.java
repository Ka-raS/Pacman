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
        _tilemap = loadTilemap(Resource.TILEMAP, Configs.Grid.MAP_SIZE);
        _font = loadFont(Resource.FONT, Configs.UI.FONT_SIZE_BASE);
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


    private static Sound loadSound(Resource sound) {
        Exception exception;

        try (
            InputStream is = ResourcesManager.class.getResourceAsStream(sound.getPath());
            AudioInputStream ais = is == null ? null : AudioSystem.getAudioInputStream(is)
        ) {
            if (is == null)
                throw new FileNotFoundException("Sound Not Found: " + sound.getPath());
            
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return new Sound(clip);

        } catch (FileNotFoundException e) {
            exception = e;
        } catch (IOException e) {
            exception = new RuntimeException("Failed Reading Sound: " + sound.getPath(), e);
            exception.setStackTrace(e.getStackTrace());
        } catch (UnsupportedAudioFileException e) {
            exception = new RuntimeException("Unsupported Audio File: " + sound.getPath(), e);
            exception.setStackTrace(e.getStackTrace());
        } catch (LineUnavailableException e) {
            exception = new RuntimeException("Audio Line Unavailable: " + sound.getPath(), e);
            exception.setStackTrace(e.getStackTrace());
        }

        handleException(exception, sound.isCritical());
        return Sound.getDummy();
    }

    private static BufferedImage loadImage(Resource image) {
        Exception exception;

        try (InputStream is = ResourcesManager.class.getResourceAsStream(image.getPath())) {
            if (is == null)
                throw new FileNotFoundException("Image Not Found: " + image.getPath());
            
            BufferedImage bufferedImg = ImageIO.read(is);
            if (bufferedImg == null)
                throw new RuntimeException("Failed Reading Image: " + image.getPath());
            return bufferedImg;

        } catch (FileNotFoundException | RuntimeException e) {
            exception = e;
        } catch (IOException e) {
            exception = new RuntimeException("Failed Reading Image: " + image.getPath(), e);
            exception.setStackTrace(e.getStackTrace());
        }

        handleException(exception, image.isCritical());
        return null;
    }

    private static Tile[][] loadTilemap(Resource tilemap, Vector2 size) {
        Exception exception;

        try (
            InputStream is = ResourcesManager.class.getResourceAsStream(tilemap.getPath());
            BufferedReader reader = is == null ? null : new BufferedReader(new InputStreamReader(is))
        ) {
            if (is == null)
                throw new FileNotFoundException("Tilemap Not Found: " + tilemap.getPath());

            Tile[][] tiles = new Tile[size.iy()][size.ix()];

            for (int y = 0; y < size.iy(); ++y) {
                String line = reader.readLine();
                if (line == null || line.length() != tiles[0].length)
                    throw new RuntimeException("Tilemap Data Corrupted: " + tilemap.getPath());

                for (int x = 0; x < size.ix(); ++x) {
                    tiles[y][x] = Tile.fromChar(line.charAt(x));
                    if (tiles[y][x] == null)
                        throw new RuntimeException("Tilemap Data Corrupted: " + tilemap.getPath());
                }
            }
            return tiles;

        } catch (FileNotFoundException | RuntimeException e) {
            exception = e;
        } catch (IOException e) {
            exception = new RuntimeException("Failed Reading Tilemap: " + tilemap.getPath(), e);
            exception.setStackTrace(e.getStackTrace());
        }

        handleException(exception, tilemap.isCritical());
        return null;
    }

    private static Font loadFont(Resource font, float size) {
        Exception exception;

        try (InputStream is = ResourcesManager.class.getResourceAsStream(font.getPath())) {
            if (is == null)
                throw new FileNotFoundException("Font Not Found: " + font.getPath());
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont((float) size);

        } catch (FileNotFoundException e) {
            exception = e;
        } catch (IOException e) {
            exception = new RuntimeException("Failed Reading Font: " + font.getPath(), e);
            exception.setStackTrace(e.getStackTrace());
        } catch (FontFormatException e) {
            exception = new RuntimeException("Invalid Font Format: " + font.getPath(), e);
            exception.setStackTrace(e.getStackTrace());
        }

        handleException(exception, font.isCritical());
        return new Font("Arial", Font.PLAIN, (int) size);
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
        for (Resource sound : sounds)
            _soundMap.put(sound, loadSound(sound));
    }

    private void initImageMap() {
        Resource[] images = { Resource.WINDOW_ICON, Resource.TITLE_IMAGE, Resource.MAP_IMAGE, Resource.SPRITE_SHEET };
        _imageMap = new HashMap<>(images.length);
        for (Resource image : images)
            _imageMap.put(image, loadImage(image));
    }

    private void initSpriteMap() {
        _spriteMap = new HashMap<>(SpriteSheet.values().length);
        BufferedImage sheetImage = _imageMap.get(Resource.SPRITE_SHEET);
        final int SIZE = Configs.PX.SPRITE_SIZE;

        for (SpriteSheet sheet : SpriteSheet.values()) {
            BufferedImage[] sprite = new BufferedImage[sheet.getLen()];
            for (int i = 0; i < sheet.getLen(); ++i)
                sprite[i] = sheetImage.getSubimage((sheet.getCol() + i) * SIZE, sheet.getRow() * SIZE, SIZE, SIZE);
            _spriteMap.put(sheet, sprite);
        }
    }

    private static final Logger _LOGGER = Logger.getLogger(ResourcesManager.class.getName());

    private HashMap<Resource, Sound> _soundMap;
    private HashMap<Resource, BufferedImage> _imageMap;
    private HashMap<SpriteSheet, BufferedImage[]> _spriteMap;
    private Tile[][] _tilemap;
    private Font _font;

}

// IHateSingleton.java