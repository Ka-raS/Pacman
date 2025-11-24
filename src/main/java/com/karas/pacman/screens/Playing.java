package com.karas.pacman.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.Pacman;
import com.karas.pacman.entities.ghosts.Blinky;
import com.karas.pacman.entities.ghosts.Clyde;
import com.karas.pacman.entities.ghosts.Inky;
import com.karas.pacman.entities.ghosts.Pinky;
import com.karas.pacman.maps.Map;
import com.karas.pacman.maps.Score;
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

        _scores = Arrays.stream(resMgr.getSprite(SpriteSheet.SCORES))
                        .map(Score::new).toList();

        _totalScore = 0;
        _state = null;
        _stateCooldown = 0.0;
        _nextScreen = Playing.class;

        _Font         = resMgr.getFont(Configs.UI.FONT_SIZE_BASE);
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
        if (_state != State.START) {
            _pacman.enterPreIdleState();
            _ghosts.forEach(Ghost::enterPreIdleState);
        }

        switch (_state) {
            case START -> _NewGameSound.play();
            case NORMAL  -> _NormalSound.loop();
            case POWERUP -> _PowerupSound.loop();
            default -> {}
        };
    }

    @Override
    public void exit(Class<? extends Screen> toScreen) {
        _pacman.enterState(Pacman.State.IDLE);
        _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.IDLE));
        _NewGameSound.pause();
        _NormalSound.pause();
        _PowerupSound.pause();
    }        

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        _scores.forEach(score -> score.update(deltaTime));
        _ghosts.forEach(ghost -> ghost.update(deltaTime));
        _pacman.update(deltaTime);
        
        boolean notPaused = _nextScreen == Playing.class;
        if (notPaused)
            updateState(deltaTime);
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D g) {
        _map.repaint(g);
        paintTotalScore(g);
        _pacman.repaint(g);
        _ghosts.forEach(ghost -> ghost.repaint(g));
        _scores.forEach(score -> score.repaint(g));
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
                _totalScore = 0;
                _stateCooldown = Configs.Time.STARTING_DURATION;
                _pacman.reset();
                _ghosts.forEach(Ghost::reset);
                _map.reset(_resMgr.getTilemap());

                _pacman.enterState(Pacman.State.IDLE);
                _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.IDLE));
                _NewGameSound.play();
                break;

            case NORMAL:
                _pacman.enterState(Pacman.State.PREY);
                for (Ghost ghost : _ghosts)
                    if (ghost.getState() == Ghost.State.PREY) 
                        ghost.enterState(Ghost.State.HUNTER);
                _NormalSound.loop();
                break;

            case POWERUP:
                _LOGGER.info("Powerup eaten!");
                _stateCooldown = Configs.Time.POWERUP_DURATION;
                _pacman.enterState(Pacman.State.HUNTER);
                for (Ghost ghost : _ghosts)
                    if (ghost.getState() != Ghost.State.DEAD)
                        ghost.enterState(Ghost.State.PREY);
                _PowerupSound.loop();
                break;

            case LOST:
                _LOGGER.info("Game lost!");
                _stateCooldown = Configs.Time.GAMEOVER_DURATION;
                _pacman.enterState(Pacman.State.DEAD);
                _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.IDLE));
                break;

            case WON:
                _LOGGER.info("Game won!");
                _stateCooldown = Configs.Time.GAMEOVER_DURATION;
                _pacman.enterState(Pacman.State.IDLE);
                _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.IDLE));
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
                    _pacman.enterState(Pacman.State.PREY);
                    _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.HUNTER));
                    enterState(State.NORMAL);
                }
                break;

            case LOST, WON:
                if (_stateCooldown < 0.0)
                    _nextScreen = GameOver.class;
                break;

            case POWERUP:
                if (_stateCooldown < 0.0) {
                    _PowerupSound.pause();
                    enterState(State.NORMAL);
                    break;
                } else if (_stateCooldown < Configs.Time.GHOST_FLASH_DURATION) // TODO: not the best
                    _ghosts.forEach(Ghost::enableFlashing);

                handleCollision(_PowerupSound);
                break;

            case NORMAL:
                handleCollision(_NormalSound);
                break;
        }
    }

    private List<Ghost> createGhosts(ResourcesManager resMgr) {
        BufferedImage[] preySprite  = resMgr.getSprite(SpriteSheet.PREY_GHOST);
        BufferedImage[] deathSprite = resMgr.getSprite(SpriteSheet.DEAD_GHOST);
        Sound deathSound = resMgr.getSound(Resource.GHOST_DEATH_SOUND);

        Ghost blinky = new Blinky(_pacman, _map,         resMgr.getSprite(SpriteSheet.BLINKY), preySprite, deathSprite, deathSound);
        Ghost pinky  = new Pinky (_pacman, _map,         resMgr.getSprite(SpriteSheet.PINKY),  preySprite, deathSprite, deathSound);
        Ghost inky   = new Inky(  _pacman, _map, blinky, resMgr.getSprite(SpriteSheet.INKY),   preySprite, deathSprite, deathSound);
        Ghost clyde  = new Clyde (_pacman, _map,         resMgr.getSprite(SpriteSheet.CLYDE),  preySprite, deathSprite, deathSound);
        
        return List.of(blinky, pinky, inky, clyde);
    }

    private void handleCollision(Sound currentSound) {
        int _deadGhostCount = 0;
        for (Ghost ghost : _ghosts)
            if (ghost.getState() == Ghost.State.DEAD)
                ++_deadGhostCount;

        for (Ghost ghost : _ghosts)
            if (_pacman.collidesWith(ghost))
                switch (ghost.getState()) {
                    case HUNTER:
                        enterState(State.LOST);
                        return;
                
                    case PREY:
                        ghost.enterState(Ghost.State.DEAD);
                        _LOGGER.info(ghost.getClass().getSimpleName() + " eaten!");
                        _totalScore += Configs.Score.GHOST * (1 << _deadGhostCount);
                        _scores.get(_deadGhostCount).setDisplay(ghost.getPosition());
                        ++_deadGhostCount;
                        break;

                    default:
                        break;
                }

        switch (_map.tryEatAt(_pacman.getPosition())) {
            case PELLET:
                _totalScore += Configs.Score.PELLET;
                if (_map.getPelletCounts() == 0) {
                    currentSound.pause();
                    enterState(State.WON);
                }
                break;
        
            case POWERUP:
                _totalScore += Configs.Score.POWERUP;
                currentSound.pause();
                enterState(State.POWERUP);
                break;

            default:
                break;
        }
    }

    private void paintTotalScore(Graphics2D g) {
        String text = String.format("%05d", _totalScore);
        FontMetrics fm = g.getFontMetrics(_Font);
        int x = (Configs.UI.WINDOW_SIZE.ix() - fm.stringWidth(text)) / 2;
        int y = (Configs.UI.WINDOW_SIZE.iy() - fm.getHeight()) / 2;
        
        g.setFont(_Font);
        g.setColor(Color.CYAN);
        g.drawString(text, x, y);
    }

    private static final Logger _LOGGER = Logger.getLogger(Playing.class.getName());

    private final Font _Font;
    private final Sound _NewGameSound, _NormalSound, _PowerupSound;
    private State _state;
    private double _stateCooldown;
    private int _totalScore;
    private List<Score> _scores;
    private Map _map;
    private Pacman _pacman;
    private List<Ghost> _ghosts;
    private ResourcesManager _resMgr;
    private Class<? extends Screen> _nextScreen;

}
