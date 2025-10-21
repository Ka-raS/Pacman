package com.karas.pacman.common;

import com.karas.pacman.Configs;

public class ExceptionHandler {
    public static void handleGeneric(Exception e, String log) {
        System.out.println("[WARNING] " + log);
        if (Configs.DEBUG)
            e.printStackTrace();
    }

    public static void handleCritical(Exception e, String log) {
        System.err.println("[ERROR] " + log);
        if (Configs.DEBUG)
            e.printStackTrace();
        System.exit(1);
    }
}
