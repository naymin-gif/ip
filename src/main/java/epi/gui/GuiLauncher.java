package epi.gui;

import javafx.application.Application;

/** Provides the plain Java entry point required to launch JavaFX reliably. */
public class GuiLauncher {
    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(EpiApplication.class, args);
    }
}
