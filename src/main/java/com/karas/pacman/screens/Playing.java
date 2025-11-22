package com.karas.pacman.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Logger;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.entities.Entity;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.Pacman;
import com.karas.pacman.entities.ghosts.Blinky;
import com.karas.pacman.entities.ghosts.Clyde;
import com.karas.pacman.entities.ghosts.Inky;
import com.karas.pacman.entities.ghosts.Pinky;
import com.karas.pacman.maps.Map;
import com.karas.pacman.maps.Tile;
import com.karas.pacman.resources.Resource;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.Sound;
import com.karas.pacman.resources.SpriteSheet;

public class Playing implements Screen {

    public Playing(ResourcesManager resMgr) {
        _resMgr = resMgr;
        _map = new Map(
            resMgr.getImage(Resource.MAP_IMAGE), resMgr.getTilemap(), 
            resMgr.getSound(Resource.EAT_WA_SOUND), resMgr.getSound(Resource.EAT_KA_SOUND)
        );
        _pacman = new Pacman(_map, 
            resMgr.getSprite(SpriteSheet.PACMAN), resMgr.getSprite(SpriteSheet.DEAD_PACMAN),
            resMgr.getSound(Resource.PACMAN_DEATH_SOUND)
        );
        _ghosts = createGhosts(resMgr);

        _state = null;
        _stateCooldown = 0.0;
        _nextScreen = Playing.class;
        _NewGameSound = resMgr.getSound(Resource.GAME_START_SOUND);
        _NormalSound  = resMgr.getSound(Resource.GAME_NORMAL_SOUND);
        _PowerupSound = resMgr.getSound(Resource.GAME_POWERUP_SOUND);
    }

    @Override
    public void enter(Class<? extends Screen> fromScreen) {
        _nextScreen = Playing.class;

        if (fromScreen == MainMenu.class) {
            enterState(State.START);
            return;
        }

        _LOGGER.info("Resumed game!");
        if (_state != State.START)
            setAllIdle(false);

        switch (_state) {
            case START -> _NewGameSound.play();
            case NORMAL  -> _NormalSound.loop();
            case POWERUP -> _PowerupSound.loop();
            default -> {}
        };
    }

    @Override
    public void exit(Class<? extends Screen> toScreen) {
        setAllIdle(true);
        _NewGameSound.pause();
        _NormalSound.pause();
        _PowerupSound.pause();
    }        

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        _pacman.update(deltaTime);
        _ghosts.forEach(ghost -> ghost.update(deltaTime));
        
        boolean notPaused = _nextScreen == Playing.class;
        if (notPaused)
            updateState(deltaTime);
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D g) {
        _map.repaint(g);
        _pacman.repaint(g);
        _ghosts.forEach(ghost -> ghost.repaint(g));
    }

    @Override
    public void input(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED)
            return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP,    KeyEvent.VK_W -> _pacman.setNextDirection(Direction.UP);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> _pacman.setNextDirection(Direction.RIGHT);
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> _pacman.setNextDirection(Direction.DOWN);
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> _pacman.setNextDirection(Direction.LEFT);

            case KeyEvent.VK_ESCAPE -> {
                boolean hasNewScreen = _nextScreen != Playing.class;
                if (!hasNewScreen)
                    _nextScreen = Paused.class;
            }
        }
    }

    
    private static enum State {
        START, NORMAL, POWERUP, LOST, WON
    }

    private void enterState(State nextState) {
        switch (nextState) {
            case START:
                _LOGGER.info("Started new playing!");
                _stateCooldown = Configs.Time.STARTING_DURATION;
                _pacman.reset();
                _ghosts.forEach(Ghost::reset);
                _map.reset(_resMgr.getTilemap());
                setAllIdle(true);
                _NewGameSound.play();
                break;

            case NORMAL:
                _pacman.enterState(Pacman.State.PREY);
                _ghosts.forEach(ghost -> ghost.enterState(Entity.State.HUNTER));
                _NormalSound.loop();
                break;

            case POWERUP:
                _LOGGER.info("Powerup eaten!");
                _stateCooldown = Configs.Time.POWERUP_DURATION;
                _pacman.enterState(Pacman.State.HUNTER);
                _ghosts.forEach(ghost -> ghost.enterState(Entity.State.PREY));
                _PowerupSound.loop();
                break;

            case LOST:
                _LOGGER.info("Game lost!");
                _stateCooldown = Configs.Time.GAMEOVER_DURATION;
                _pacman.enterState(Pacman.State.DEAD);
                setAllIdle(true);
                break;

            case WON:
                _LOGGER.info("Game won!");
                _stateCooldown = Configs.Time.GAMEOVER_DURATION;
                setAllIdle(true);
                break;
        }
        _state = nextState;
    }

    private void updateState(double deltaTime) {
        if (_stateCooldown > 0.0)
            _stateCooldown -= deltaTime;

        switch (_state) {
            case START:
                if (_stateCooldown < 0.0) {
                    setAllIdle(false);
                    enterState(State.NORMAL);
                }
                break;

            case LOST, WON:
                if (_stateCooldown < 0.0)
                    _nextScreen = null; // TODO: _nextScreen = GameOver;
                break;

            case POWERUP:
                if (_stateCooldown < 0.0) {
                    _PowerupSound.pause();
                    enterState(State.NORMAL);
                    break;
                } else if (_stateCooldown < Configs.Time.GHOST_FLASH_DURATION) // TODO: not the best
                    _ghosts.forEach(Ghost::enableFlashing);

                evaluateCommonState(_PowerupSound);
                break;

            case NORMAL:
                evaluateCommonState(_NormalSound);
                break;
        }
    }

    private List<Ghost> createGhosts(ResourcesManager resMgr) {
        BufferedImage[] preySprite = resMgr.getSprite(SpriteSheet.PREY_GHOST);
        BufferedImage[] deathSprite = resMgr.getSprite(SpriteSheet.DEAD_GHOST);
        Sound deathSound = resMgr.getSound(Resource.GHOST_DEATH_SOUND);

        Ghost blinky = new Blinky(_pacman, _map,         resMgr.getSprite(SpriteSheet.BLINKY), preySprite, deathSprite, deathSound);
        Ghost pinky  = new Pinky (_pacman, _map,         resMgr.getSprite(SpriteSheet.PINKY),  preySprite, deathSprite, deathSound);
        Ghost inky   = new Inky(  _pacman, _map, blinky, resMgr.getSprite(SpriteSheet.INKY),   preySprite, deathSprite, deathSound);
        Ghost clyde  = new Clyde (_pacman, _map,         resMgr.getSprite(SpriteSheet.CLYDE),  preySprite, deathSprite, deathSound);
        
        return List.of(blinky, pinky, inky, clyde);
    }

    private void setAllIdle(boolean isIdle) {
        _pacman.setIdle(isIdle);
        _ghosts.forEach(ghost -> ghost.setIdle(isIdle));
    }

    private void evaluateCommonState(Sound currentSound) {
        if (_ghosts.stream().anyMatch(Ghost::hasCaughtPacman)) {
            currentSound.pause();
            enterState(State.LOST);
            return;
        }

        if (_map.tryEatAt(_pacman.getPosition()) == Tile.POWERUP) {
            currentSound.pause();
            enterState(State.POWERUP);
            return;
        }

        if (_map.getPelletCounts() == 0) {
            currentSound.pause();
            enterState(State.WON);
        }
    }


    private static final Logger _LOGGER = Logger.getLogger(Playing.class.getName());

    private final Sound _NewGameSound, _NormalSound, _PowerupSound;
    private Map _map;
    private Pacman _pacman;
    private List<Ghost> _ghosts;
    private State _state;
    private double _stateCooldown;
    private ResourcesManager _resMgr;
    private Class<? extends Screen> _nextScreen;

}
