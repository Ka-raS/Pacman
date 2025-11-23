package com.karas.pacman.resources;

import javax.sound.sampled.Clip;

public class Sound {
    
    public static Sound getDummy() {
        return _DUMMY;
    }

    public Sound(Clip clip) {
        if (clip == null)
            throw new NullPointerException("Clip cannot be null");
        _clip = clip;
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

    public void close() {
        _clip.stop();
        _clip.close();
    }


    private static final Sound _DUMMY = new Sound() {
        @Override public void play() {}
        @Override public void loop() {}
        @Override public void pause() {}
        @Override public void close() {}
    };

    private Sound() {}

    private boolean isSoundEnded() {
        return _clip.getFramePosition() == _clip.getFrameLength();
    }

    private Clip _clip;
    
}
