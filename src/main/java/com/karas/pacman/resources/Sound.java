package com.karas.pacman.resources;

import javax.sound.sampled.Clip;

import com.karas.pacman.commons.Exitable;

public final class Sound implements Exitable {

    public Sound(Clip clip) {
        _clip = clip;
    }

    @Override
    public void exit() {
        if (_clip == null)
            return;
        pause();
        _clip.close();
    }

    public void play() {
        if (_clip == null || _clip.isRunning())
            return;

        if (_clip.getFramePosition() == _clip.getFrameLength())
            _clip.setFramePosition(0);
        _clip.start();
    }

    public void loop() {
        if (_clip != null)
            _clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void pause() {
        if (_clip != null && _clip.isRunning())
            _clip.stop();
    }

    public void reset() {
        if (_clip != null)
            _clip.setFramePosition(0);
    }


    private final Clip _clip;
    
}
