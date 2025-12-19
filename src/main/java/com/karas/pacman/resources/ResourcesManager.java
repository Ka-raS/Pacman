package com.karas.pacman.resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumMap;
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
import com.karas.pacman.commons.Exitable;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.data.ScoreDatabase;
import com.karas.pacman.maps.Tile;

public class ResourcesManager implements Exitable {

    public ResourcesManager() {
        _soundMap  = initSoundMap();
        _imageMap  = initImageMap();
        _spriteMap = initSpriteMap(_imageMap.get(ResourceID.SPRITE_SHEET));
        _tilemap   = initTilemap();
        _fonts     = initFonts();
        _database  = initDatabase();
    }

    public Sound getSound(ResourceID soundID) {
        if (soundID.getType() != ResourceID.Type.SOUND)
            throw new IllegalArgumentException("ResourceID is not of type SOUND: " + soundID);
        return _soundMap.get(soundID);
    }

    public BufferedImage getImage(ResourceID imageID) {
        if (imageID.getType() != ResourceID.Type.IMAGE)
            throw new IllegalArgumentException("ResourceID is not of type IMAGE: " + imageID);
        return _imageMap.get(imageID);
    }

    public BufferedImage[] getSprite(SpriteID spriteID) {
        return _spriteMap.get(spriteID);
    }

    public Tile[][] getTilemap() {
        return _tilemap;
    }

    public Font getFont(int size) {
        return switch (size) {
            case Configs.PX.FONT_SIZE_SMALL  -> _fonts[0];
            case Configs.PX.FONT_SIZE_MEDIUM -> _fonts[1];
            case Configs.PX.FONT_SIZE_LARGE  -> _fonts[2];
            default                          -> _fonts[1].deriveFont((float) size);
        };
    }

    public ScoreDatabase getDatabase() {
        return _database;
    }

    @Override
    public void exit() {
        for (Sound sound : _soundMap.values())
            sound.exit();

        _soundMap.clear();
        _imageMap.clear();
        _spriteMap.clear();

        try {
            _database.save();
        } catch (IOException e) {
            _LOGGER.log(Level.SEVERE, "Failed Saving Database On Exit", e);
        }
    }


    private static EnumMap<ResourceID, Sound> initSoundMap() {
        EnumMap<ResourceID, Sound> soundMap = new EnumMap<>(ResourceID.class);
        for (ResourceID sound : ResourceID.values())
            if (sound.getType() == ResourceID.Type.SOUND)
                try {
                    soundMap.put(sound, loadSound(sound.getPath()));
                } catch (RuntimeException e) {
                    handleException(e, sound.isCritical());
                    soundMap.put(sound, new Sound(null));
                }
        return soundMap;
    }

    private static EnumMap<ResourceID, BufferedImage> initImageMap() {
        EnumMap<ResourceID, BufferedImage> imageMap = new EnumMap<>(ResourceID.class);
        for (ResourceID image : ResourceID.values())
            if (image.getType() == ResourceID.Type.IMAGE)
                try {
                    imageMap.put(image, loadImage(image.getPath()));
                } catch (RuntimeException e) {
                    handleException(e, image.isCritical());
                    imageMap.put(image, null);
                }
        return imageMap;
    }

    private static EnumMap<SpriteID, BufferedImage[]> initSpriteMap(BufferedImage sheetImage) {
        EnumMap<SpriteID, BufferedImage[]> spriteMap = new EnumMap<>(SpriteID.class);
        
        if (sheetImage == null) {
            _LOGGER.warning("Sprite sheet not initialized.");
            for (SpriteID sheet : SpriteID.values())
                spriteMap.put(sheet, new BufferedImage[sheet.getLength()]);
            return spriteMap;
        }

        final int SIZE = Configs.PX.SPRITE_SIZE;
        for (SpriteID sheet : SpriteID.values()) {
            BufferedImage[] sprite = new BufferedImage[sheet.getLength()];
            for (int i = 0; i < sheet.getLength(); ++i)
                sprite[i] = sheetImage.getSubimage((sheet.getColumn() + i) * SIZE, sheet.getRow() * SIZE, SIZE, SIZE);
            spriteMap.put(sheet, sprite);
        }
        return spriteMap;
    }

    private static Tile[][] initTilemap() {
        final Vector2 SIZE = Configs.Grid.MAP_SIZE;
        try {
            return loadTilemap(ResourceID.TILEMAP.getPath(), SIZE);
        } catch (RuntimeException e) {
            handleException(e, ResourceID.TILEMAP.isCritical());

            Tile[][] tilemap = new Tile[SIZE.iy()][SIZE.ix()];
            for (Tile[] row : tilemap)
                for (int i = 0; i < SIZE.ix(); ++i)
                    row[i] = Tile.NONE;
            return tilemap;
        }
    }

    private static Font[] initFonts() {
        Font font;

        try {
            font = loadFont(ResourceID.FONT.getPath(), Configs.PX.FONT_SIZE_MEDIUM);
        } catch (RuntimeException e) {
            handleException(e, ResourceID.FONT.isCritical());
            font = new Font("Arial", Font.PLAIN, (int) Configs.PX.FONT_SIZE_MEDIUM);
        }

        return new Font[] { font.deriveFont((float)Configs.PX.FONT_SIZE_SMALL), font, font.deriveFont((float)Configs.PX.FONT_SIZE_LARGE) };
    }

    private static ScoreDatabase initDatabase() {
        try {
            return loadOrCreateDatabase(ResourceID.DATABASE_FILE.getPath());

        } catch (RuntimeException e) {
            handleException(e, ResourceID.DATABASE_FILE.isCritical());
            _LOGGER.warning("Using Empty Temporary Database");
            return new ScoreDatabase();
        }
    }

    private static Sound loadSound(String path) throws RuntimeException {
        URL url = ResourcesManager.class.getResource(path);
        if (url == null)
            throw new RuntimeException("Sound Not Found: " + path);

        try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return new Sound(clip);

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
        URL url = ResourcesManager.class.getResource(path);
        if (url == null)
            throw new RuntimeException("Tilemap Not Found: " + path);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            Tile[][] tiles = new Tile[size.iy()][size.ix()];

            for (int y = 0; y < size.iy(); ++y) {
                String line = reader.readLine();
                if (line == null || line.length() != tiles[0].length)
                    throw new IllegalArgumentException("Tilemap Data Corrupted At Line " + (y+1));
                    
                for (int x = 0; x < size.ix(); ++x) {
                    tiles[y][x] = Tile.of(line.charAt(x));
                    if (tiles[y][x] == null)
                        throw new IllegalArgumentException(
                            String.format("Tilemap Data Corrupted At Line %d Column %d", y+1, x+1)
                        );
                }
            }
            return tiles;

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
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Font Not Found: " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed Reading Font: " + path, e);
        } catch (FontFormatException e) {
            throw new RuntimeException("Invalid Font Format: " + path, e);
        }
    }

    private static ScoreDatabase loadOrCreateDatabase(String path) throws RuntimeException {
        File databaseFile;
        try {
            URI jarPath = ResourcesManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            databaseFile = new File(new File(jarPath).getParent(), path);

        } catch (URISyntaxException e) {
            _LOGGER.log(Level.WARNING, "Failed Getting JAR Path. Searching Current Directory For Database File", e);
            databaseFile = new File("." + path);
        }

        if (!databaseFile.exists()) {
            _LOGGER.info("Creating New Database File At: " + databaseFile.getPath());
            databaseFile.getParentFile().mkdirs();
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed Creating Database File.", e);
            }
        }

        try {
            return new ScoreDatabase(databaseFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("[UNEXPECTED] Database File Not Found: " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed Reading Database File: " + path, e);
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
    }

    private static final Logger _LOGGER = Logger.getLogger(ResourcesManager.class.getName());

    private final EnumMap<ResourceID, Sound> _soundMap;
    private final EnumMap<ResourceID, BufferedImage> _imageMap;
    private final EnumMap<SpriteID, BufferedImage[]> _spriteMap;
    private final Tile[][] _tilemap;
    private final Font[] _fonts;
    private final ScoreDatabase _database;

}

// IHateSingleton.java