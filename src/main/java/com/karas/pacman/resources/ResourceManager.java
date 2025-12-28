package com.karas.pacman.resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Exitable;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.maps.Tile;

public final class ResourceManager implements Exitable {

    public ResourceManager() {
        _imageMap  = loadImages();
        _spriteMap = loadSprites(_imageMap.get(ResourceID.SPRITE_SHEET));
        _soundMap  = loadSounds();
        _tilemap   = loadTilemap();
        _database  = loadDatabase();
        _fonts     = loadFonts();
    }

    public BufferedImage getImage(ResourceID imageID) {
        return _imageMap.get(imageID);
    }

    public BufferedImage[] getSprite(SpriteID spriteID) {
        return _spriteMap.get(spriteID);
    }

    public Sound getSound(ResourceID soundID) {
        return _soundMap.get(soundID);
    }

    public Tile[] getTilemap() {
        return _tilemap;
    }

    public ScoreDatabase getDatabase() {
        return _database;
    }

    public Font getFont(int size) {
        return switch (size) {
            case Constants.Pixel.FONT_SIZE_SMALL  -> _fonts[0];
            case Constants.Pixel.FONT_SIZE_MEDIUM -> _fonts[1];
            case Constants.Pixel.FONT_SIZE_LARGE  -> _fonts[2];
            default -> _fonts[1].deriveFont((float) size);
        };
    }

    @Override
    public void exit() {
        _soundMap.values().forEach(Sound::exit);
        _soundMap.clear();
        _imageMap.clear();
        _spriteMap.clear();
        _database.save();
    }


    private static EnumMap<ResourceID, BufferedImage> loadImages() {
        EnumMap<ResourceID, BufferedImage> imageMap = new EnumMap<>(ResourceID.class);
        for (ResourceID id : ResourceID.values()) {
            if (id.type() != ResourceID.Type.IMAGE)
                continue;

            URL url = ResourceManager.class.getResource(id.path());
            try {
                imageMap.put(id, ImageIO.read(url));
            } catch (IOException e) {
                LOG.log(Level.WARNING, e, () -> "Failed Reading Image: " + url);
                imageMap.put(id, null);
            }
        }
        return imageMap;
    }

    private static EnumMap<SpriteID, BufferedImage[]> loadSprites(BufferedImage sheetImage) {
        final int SIZE = Constants.Pixel.SPRITE_SIZE;
        EnumMap<SpriteID, BufferedImage[]> spriteMap = new EnumMap<>(SpriteID.class);

        for (SpriteID id : SpriteID.values()) {
            BufferedImage[] sprite = new BufferedImage[id.length()];
            spriteMap.put(id, sprite);
            if (sheetImage == null)
                continue;
            for (int i = 0; i < sprite.length; ++i)
                sprite[i] = sheetImage.getSubimage((id.column() + i) * SIZE, id.row() * SIZE, SIZE, SIZE);
        }
        return spriteMap;
    }

    private static EnumMap<ResourceID, Sound> loadSounds() {
        EnumMap<ResourceID, Sound> soundMap = new EnumMap<>(ResourceID.class);
        for (ResourceID id : ResourceID.values()) {
            if (id.type() != ResourceID.Type.SOUND)
                continue;

            URL url = ResourceManager.class.getResource(id.path());
            try (AudioInputStream stream = AudioSystem.getAudioInputStream(url)) {
                Clip clip = AudioSystem.getClip();
                clip.open(stream);
                soundMap.put(id, new Sound(clip));
                continue;

            } catch (IOException e) {
                LOG.log(Level.WARNING, e, () -> "Failed Reading Sound: " + url);
            } catch (UnsupportedAudioFileException e) {
                LOG.log(Level.WARNING, e, () -> "Unsupported Audio File: " + url);
            } catch (LineUnavailableException e) {
                LOG.log(Level.WARNING, e, () -> "Audio Line Unavailable: " + url);
            }
            soundMap.put(id, new Sound(null));
        }
        return soundMap;
    }

    private static Tile[] loadTilemap() {
        final Vector2 SIZE = Constants.Grid.MAP_SIZE;
        Tile[] tilemap = new Tile[SIZE.iy() * SIZE.ix()];
        URL url = ResourceManager.class.getResource(ResourceID.TILEMAP.path());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            for (int y = 0; y < SIZE.iy(); ++y) {
                String line = reader.readLine();
                for (int x = 0; x < SIZE.ix(); ++x)
                    tilemap[y * SIZE.ix() + x] = Tile.of(line.charAt(x));
            }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e, () -> "Failed Reading Tilemap: " + url);
            Arrays.fill(tilemap, Tile.WALL);
        }
        return tilemap;
    }

    private static ScoreDatabase loadDatabase() {
        File file = new File(ResourceID.DATABASE.path());
        if (!file.exists()) {
            LOG.info("Creating new database file at: " + file.getAbsolutePath());
            try {
                file.createNewFile();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Failed Creating Database File", e);
                file = null;
            }
        }
        return new ScoreDatabase(file);
    }

    private static Font[] loadFonts() {
        URL url = ResourceManager.class.getResource(ResourceID.FONT.path());
        try (InputStream stream = url.openStream()) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
            return new Font[] {
                font.deriveFont((float) Constants.Pixel.FONT_SIZE_SMALL),
                font.deriveFont((float) Constants.Pixel.FONT_SIZE_MEDIUM),
                font.deriveFont((float) Constants.Pixel.FONT_SIZE_LARGE)
            };
    
        } catch (IOException e) {
            LOG.log(Level.WARNING, e, () -> "Failed Reading Font: " + url);
        } catch (FontFormatException e) {
            LOG.log(Level.WARNING, e, () -> "Invalid Font Format: " + url);
        }

        Font fallback = new Font("Arial", Font.BOLD, Constants.Pixel.FONT_SIZE_MEDIUM);
        return new Font[] {
            fallback.deriveFont((float) Constants.Pixel.FONT_SIZE_SMALL),
            fallback,
            fallback.deriveFont((float) Constants.Pixel.FONT_SIZE_LARGE)
        };
    }

    private static final Logger LOG = Logger.getLogger(ResourceManager.class.getName());

    private final EnumMap<ResourceID, Sound> _soundMap;
    private final EnumMap<ResourceID, BufferedImage> _imageMap;
    private final EnumMap<SpriteID, BufferedImage[]> _spriteMap;
    private final Tile[] _tilemap;
    private final Font[] _fonts;
    private final ScoreDatabase _database;

}

// IHateSingleton.java
