package com.karas.pacman.commons;

import javax.swing.JOptionPane;

import com.karas.pacman.Configs;

public class ExceptionHandler {
    public static void handleGeneric(Exception e, String message) {
        if (message == null)
            message = e.getMessage();
        System.out.println("[WARNING] " + message);
        if (Configs.DEBUG)
            e.printStackTrace();
    }

    public static void handleCritical(Exception e, String message) {
        if (message == null)
            message = e.getMessage();
        System.err.println("[ERROR] " + message);
        if (Configs.DEBUG)
            e.printStackTrace();

        JOptionPane.showMessageDialog(
            null,
            message,
            "ERROR",
            JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }


    private ExceptionHandler() {}

}
