package com.karas.pacman;

import java.util.logging.Logger;

public final class Main {
    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Constants.LOG_LEVEL);
        Game game = new Game();
        game.enter();
    }
}