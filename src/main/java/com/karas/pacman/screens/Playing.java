package com.karas.pacman.screens;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.Pacman;
import com.karas.pacman.entities.ghosts.Blinky;
import com.karas.pacman.entities.ghosts.Clyde;
import com.karas.pacman.entities.ghosts.Inky;
import com.karas.pacman.entities.ghosts.Pinky;
import com.karas.pacman.maps.GameMap;
import com.karas.pacman.maps.ScorePopup;
import com.karas.pacman.resources.ResourceID;
import com.karas.pacman.resources.ResourcesManager;
import com.karas.pacman.resources.ScoreDatabase;
import com.karas.pacman.resources.Sound;
import com.karas.pacman.resources.SpriteID;

public final class Playing implements Screen {

    public Playing(ResourcesManager ResourcesMgr) {
        _map    = new GameMap(ResourcesMgr);
        _pacman = new Pacman(_map, ResourcesMgr);
        _scores = Arrays.stream(ResourcesMgr.getSprite(SpriteID.SCORES)).map(ScorePopup::new).toList();
        
        Blinky blinky = new Blinky(_pacman, _map, ResourcesMgr);
        Pinky pinky   = new Pinky(_pacman, _map, ResourcesMgr);
        Inky inky     = new Inky(_pacman, _map, blinky, ResourcesMgr);
        Clyde clyde   = new Clyde(_pacman, _map, ResourcesMgr);
        _ghosts = List.of(blinky, pinky, inky, clyde);

        _totalScore = 0;
        _state = null;
        _stateDuration = 0.0;
        _nextScreen = Playing.class;

        _ScoreDatabase = ResourcesMgr.getDatabase();
        _FontMedium    = ResourcesMgr.getFont(Constants.Pixel.FONT_SIZE_MEDIUM);
        _NewGameSound  = ResourcesMgr.getSound(ResourceID.GAME_START_SOUND);
        _NormalSound   = ResourcesMgr.getSound(ResourceID.GAME_NORMAL_SOUND);
        _PowerupSound  = ResourcesMgr.getSound(ResourceID.GAME_POWERUP_SOUND);
        _WonSound      = ResourcesMgr.getSound(ResourceID.GAME_WON_SOUND);
    }

    @Override
    public void enter(Class<? extends Screen> previous) {
        _nextScreen = Playing.class;

        if (previous != Paused.class) {
            enterState(State.START);
            return;
        }

        _LOGGER.info("Resumed game");
        if (_state != State.START) {
            _pacman.enterPreIdleState();
            _ghosts.forEach(Ghost::enterPreIdleState);
        }

        switch (_state) {
            case START   -> _NewGameSound.play();
            case NORMAL  -> _NormalSound.loop();
            case POWERUP -> _PowerupSound.loop();
            case WON     -> _WonSound.play();
            default      -> {}
        };
    }

    @Override
    public void exit() {
        _pacman.enterState(Pacman.State.IDLE);
        _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.IDLE));
        _NewGameSound.pause();
        _NormalSound.pause();
        _PowerupSound.pause();
        _WonSound.pause();
    }        

    @Override
    public Class<? extends Screen> update(double deltaTime) {
        _scores.forEach(score -> score.update(deltaTime));
        _ghosts.forEach(ghost -> ghost.update(deltaTime));
        _pacman.update(deltaTime);
        
        if (_nextScreen == Playing.class)
            updateState(deltaTime);
        return _nextScreen;
    }

    @Override
    public void repaint(Graphics2D G) {
        _map.repaint(G);
        paintTotalScore(G);
        _pacman.repaint(G);
        _ghosts.forEach(ghost -> ghost.repaint(G));
        _scores.forEach(score -> score.repaint(G));
    }

    @Override
    public void input(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP,    KeyEvent.VK_W -> _pacman.setNextDirection(Direction.UP);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> _pacman.setNextDirection(Direction.RIGHT);
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> _pacman.setNextDirection(Direction.DOWN);
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> _pacman.setNextDirection(Direction.LEFT);

            case KeyEvent.VK_ESCAPE -> {
                if (_nextScreen == Playing.class)
                    _nextScreen = Paused.class;
            }
        }
    }


    private static enum State {
        START, NORMAL, POWERUP, LOST, WON
    }

    private void enterState(State nextState) {
        _state = nextState;
        switch (nextState) {
            case START:
                _LOGGER.info("Started new playing");
                _totalScore = 0;
                _stateDuration = Constants.Time.STARTING_DURATION;
                
                _pacman.reset();
                _ghosts.forEach(Ghost::reset);
                _map.reset();
                for (Sound sound : List.of(_NewGameSound, _NormalSound, _PowerupSound, _WonSound))
                    sound.reset();

                _pacman.enterState(Pacman.State.IDLE);
                _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.IDLE));
                _NewGameSound.play();
                break;

            case NORMAL:
                _pacman.enterState(Pacman.State.PREY);
                for (Ghost ghost : _ghosts)
                    if (ghost.getState() != Ghost.State.DEAD) 
                        ghost.enterState(Ghost.State.HUNTER);
                _NormalSound.loop();
                break;

            case POWERUP:
                _stateDuration = Constants.Time.POWERUP_DURATION;
                _pacman.enterState(Pacman.State.HUNTER);
                for (Ghost ghost : _ghosts)
                    if (ghost.getState() != Ghost.State.DEAD)
                        ghost.enterState(Ghost.State.PREY);
                _PowerupSound.loop();
                break;

            case LOST:
                _LOGGER.info("Game lost");
                _stateDuration = Constants.Time.GAMELOST_DURATION;
                _pacman.enterState(Pacman.State.DEAD);
                _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.IDLE));
                break;

            case WON:
                _LOGGER.info("Game won");
                _stateDuration = Constants.Time.GAMEWON_DURATION;
                _pacman.enterState(Pacman.State.IDLE);
                _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.IDLE));
                _WonSound.play();
                _ScoreDatabase.addEntry(_totalScore);
                break;
        }
    }

    private void updateState(double deltaTime) {
        if (_stateDuration > 0.0)
            _stateDuration -= deltaTime;

        switch (_state) {
            case START:
                if (_stateDuration < 0.0) {
                    _pacman.enterState(Pacman.State.PREY);
                    _ghosts.forEach(ghost -> ghost.enterState(Ghost.State.HUNTER));
                    enterState(State.NORMAL);
                }
                break;

            case LOST, WON:
                if (_stateDuration < 0.0)
                    _nextScreen = HighScores.class;
                break;

            case POWERUP:
                if (_stateDuration < 0.0) {
                    _PowerupSound.pause();
                    enterState(State.NORMAL);
                    break;
                } else if (_stateDuration < Constants.Time.GHOST_FLASH_DURATION) // TODO: not the best
                    _ghosts.forEach(Ghost::startFlashing);

                handleCollision(_PowerupSound);
                break;

            case NORMAL:
                handleCollision(_NormalSound);
                break;
        }
    }

    private void handleCollision(Sound currentSound) {
        int deadGhostCount = 0;
        for (Ghost ghost : _ghosts)
            if (ghost.getState() == Ghost.State.DEAD)
                ++deadGhostCount;

        for (Ghost ghost : _ghosts)
            if (_pacman.collidesWith(ghost))
                switch (ghost.getState()) {
                    case HUNTER:
                        enterState(State.LOST);
                        return;
                
                    case PREY:
                        ghost.enterState(Ghost.State.DEAD);
                        _LOGGER.info(ghost.getClass().getSimpleName() + " eaten");
                        _totalScore += Constants.Score.GHOST * (1 << deadGhostCount);
                        _scores.get(deadGhostCount).displayAt(ghost.getPosition());
                        ++deadGhostCount;
                        break;

                    default:
                        break;
                }

        switch (_map.tryEatAt(_pacman.getGridPosition())) {
            case PELLET:
                _totalScore += Constants.Score.PELLET;
                if (_map.getPelletCounts() == 0) {
                    currentSound.pause();
                    enterState(State.WON);
                }
                break;
        
            case POWERUP:
                _LOGGER.info("Powerup eaten");
                _totalScore += Constants.Score.POWERUP;
                currentSound.pause();
                enterState(State.POWERUP);
                break;

            default:
                break;
        }
    }

    private void paintTotalScore(Graphics2D G) {
        String text = String.format("%05d", _totalScore);
        FontMetrics fm = G.getFontMetrics(_FontMedium);
        int x = (Constants.Pixel.WINDOW_SIZE.ix() - fm.stringWidth(text)) / 2;
        int y = (Constants.Pixel.WINDOW_SIZE.iy() - fm.getHeight()) / 2;
        
        G.setFont(_FontMedium);
        G.setColor(Constants.Color.SCORE);
        G.drawString(text, x, y);
    }

    private static final Logger _LOGGER = Logger.getLogger(Playing.class.getName());

    private final Font _FontMedium;
    private final Sound _NewGameSound, _NormalSound, _PowerupSound, _WonSound;
    private final ScoreDatabase _ScoreDatabase;

    private final GameMap _map;
    private final Pacman _pacman;
    private final List<Ghost> _ghosts;
    private final List<ScorePopup> _scores;
    private volatile Class<? extends Screen> _nextScreen;
    private State _state;
    private double _stateDuration;
    private int _totalScore;

}
