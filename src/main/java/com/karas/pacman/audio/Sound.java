package com.karas.pacman.audio;

import javax.sound.sampled.Clip;

import com.karas.pacman.commons.Exitable;

public class Sound implements Exitable {
    
    public static Sound getDummy() {
        return _DUMMY;
    }

    public Sound(Clip clip) {
        if (clip == null)
            throw new NullPointerException("Clip cannot be null");
        _clip = clip;
    }

    @Override
    public void exit() {
        pause();
        _clip.close();
    }

    public void play() {
        if (_clip.isRunning())
            return;

        if (_clip.getFramePosition() == _clip.getFrameLength())
            _clip.setFramePosition(0);
        _clip.start();
    }

    public void loop() {
        _clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void pause() {
        if (_clip.isRunning())
            _clip.stop();
    }


    private static final Sound _DUMMY = new Sound() {
        @Override public void exit()  {}
        @Override public void play()  {}
        @Override public void loop()  {}
        @Override public void pause() {}
    };

    private Sound() {
        _clip = null;
    }

    private final Clip _clip;
    
}
