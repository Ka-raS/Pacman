package com.karas.pacman.resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.karas.pacman.maps.Tile;

public class ResourcesManager implements Exitable {

    public ResourcesManager() {
        _soundMap  = createSoundMap();
        _imageMap  = createImageMap();
        _spriteMap = createSpriteMap(_imageMap.get(Resource.SPRITE_SHEET));
        _tilemap   = createTilemap();
        _fonts     = createFonts();
    }

    @Override
    public void exit() {
        for (Sound sound : _soundMap.values())
            sound.exit();
    }

    public Sound getSound(Resource sound) {
        if (sound == null)
            return Sound.getDummy();
        return _soundMap.get(sound);
    }

    public BufferedImage getImage(Resource image) {
        if (image == null)
            return null;
        return _imageMap.get(image);
    }

    public BufferedImage[] getSprite(SpriteSheet sheet) {
        if (sheet == null)
            return null;
        return _spriteMap.get(sheet);
    }

    public Tile[][] getTilemap() {
        Tile[][] copy = new Tile[_tilemap.length][];
        for (int y = 0; y < _tilemap.length; ++y)
            copy[y] = _tilemap[y].clone();
        return copy;
    }

    public Font getFont(int size) { 
        if (size <= 0)
            size = Configs.PX.FONT_SIZE_SMALL;
        return switch (size) {
            case Configs.PX.FONT_SIZE_SMALL  -> _fonts[0];
            case Configs.PX.FONT_SIZE_MEDIUM -> _fonts[1];
            case Configs.PX.FONT_SIZE_LARGE  -> _fonts[2];
            default -> _fonts[1].deriveFont((float) size);
        };
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

    private static BufferedImage createSquare(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();        
        g.setColor(Configs.Color.PELLET);
        g.fillRect(0, 0, size, size);
        g.dispose();
        return image;
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

    private static EnumMap<Resource, Sound> createSoundMap() {
        Resource[] sounds = { 
            Resource.EAT_WA_SOUND, Resource.EAT_KA_SOUND, Resource.PACMAN_DEATH_SOUND, Resource.GHOST_DEATH_SOUND, 
            Resource.GAME_START_SOUND, Resource.GAME_NORMAL_SOUND, Resource.GAME_POWERUP_SOUND
        };
        EnumMap<Resource, Sound> soundMap = new EnumMap<>(Resource.class);
        
        for (Resource sound : sounds) {
            try {
                soundMap.put(sound, loadSound(sound.getPath()));
            } catch (RuntimeException e) {
                handleException(e, sound.isCritical());
                soundMap.put(sound, Sound.getDummy());
            }
        }
        return soundMap;
    }

    private static EnumMap<Resource, BufferedImage> createImageMap() {
        Resource[] images = { Resource.WINDOW_ICON, Resource.TITLE_IMAGE, Resource.MAP_IMAGE, Resource.SPRITE_SHEET };
        EnumMap<Resource, BufferedImage> imageMap = new EnumMap<>(Resource.class);

        for (Resource image : images) {
            try {
                imageMap.put(image, loadImage(image.getPath()));
            } catch (RuntimeException e) {
                handleException(e, image.isCritical());
                imageMap.put(image, null);
            }
        }
        return imageMap;
    }

    private static EnumMap<SpriteSheet, BufferedImage[]> createSpriteMap(BufferedImage sheetImage) {
        EnumMap<SpriteSheet, BufferedImage[]> spriteMap = new EnumMap<>(SpriteSheet.class);
        
        if (sheetImage == null) {
            _LOGGER.warning("Sprite sheet not initialized.");
            for (SpriteSheet sheet : SpriteSheet.values())
                spriteMap.put(sheet, new BufferedImage[sheet.getLen()]);
            return spriteMap;
        }

        spriteMap.put(SpriteSheet.PELLETS, new BufferedImage[] {
            createSquare(Configs.PX.PELLET_SIZE),
            createSquare(Configs.PX.POWERUP_SIZE)
        });

        final int SIZE = Configs.PX.SPRITE_SIZE;
        for (SpriteSheet sheet : SpriteSheet.values()) {
            if (sheet == SpriteSheet.PELLETS)
                continue;

            BufferedImage[] sprite = new BufferedImage[sheet.getLen()];
            for (int i = 0; i < sheet.getLen(); ++i)
                sprite[i] = sheetImage.getSubimage((sheet.getCol() + i) * SIZE, sheet.getRow() * SIZE, SIZE, SIZE);
            spriteMap.put(sheet, sprite);
        }
        return spriteMap;
    }

    private static Tile[][] createTilemap() {
        final Vector2 SIZE = Configs.Grid.MAP_SIZE;
        try {
            return loadTilemap(Resource.TILEMAP.getPath(), SIZE);
        } catch (RuntimeException e) {
            handleException(e, Resource.TILEMAP.isCritical());

            Tile[][] tilemap = new Tile[SIZE.iy()][SIZE.ix()];
            for (Tile[] row : tilemap)
                for (int i = 0; i < SIZE.ix(); ++i)
                    row[i] = Tile.NONE;
            return tilemap;
        }
    }

    private static Font[] createFonts() {
        Font font;

        try {
            font = loadFont(Resource.FONT.getPath(), Configs.PX.FONT_SIZE_MEDIUM);
        } catch (RuntimeException e) {
            handleException(e, Resource.FONT.isCritical());
            font = new Font("Arial", Font.PLAIN, (int) Configs.PX.FONT_SIZE_MEDIUM);
        }

        return new Font[] { font.deriveFont(Configs.PX.FONT_SIZE_SMALL), font, font.deriveFont(Configs.PX.FONT_SIZE_LARGE) };
    }

    private static final Logger _LOGGER = Logger.getLogger(ResourcesManager.class.getName());

    private final EnumMap<Resource, Sound> _soundMap;
    private final EnumMap<Resource, BufferedImage> _imageMap;
    private final EnumMap<SpriteSheet, BufferedImage[]> _spriteMap;
    private final Tile[][] _tilemap;
    private final Font[] _fonts;

}

// IHateSingleton.java