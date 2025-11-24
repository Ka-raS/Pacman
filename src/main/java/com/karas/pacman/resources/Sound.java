package com.karas.pacman.resources;

import javax.sound.sampled.Clip;

import com.karas.pacman.commons.Enterable;

public class Sound implements Enterable {
    
    public static Sound getDummy() {
        return _DUMMY;
    }

    public Sound(Clip clip) {
        if (clip == null)
            throw new NullPointerException("Clip cannot be null");
        _clip = clip;
    }

    @Override
    public void enter() {
        _clip.setFramePosition(0);
    }

    @Override
    public void exit() {
        _clip.stop();
        _clip.close();
    }

    public void play() {
        if (_clip.isRunning())
            return;

        if (isSoundEnded())
            _clip.setFramePosition(0);
        _clip.start();
    }

    public void loop() {
        _clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void pause() {    
        _clip.stop();
    }


    private static final Sound _DUMMY = new Sound() {
        @Override public void enter() {}
        @Override public void exit() {}
        @Override public void play() {}
        @Override public void loop() {}
        @Override public void pause() {}
    };

    private Sound() {
        _clip = null;
    }

    private boolean isSoundEnded() {
        return _clip.getFramePosition() == _clip.getFrameLength();
    }

    private final Clip _clip;
    
}
