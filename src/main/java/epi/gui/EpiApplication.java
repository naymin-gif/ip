package epi.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Provides the JavaFX application implementation for Epi. */
public class EpiApplication extends Application {
    private static final double MIN_WINDOW_WIDTH = 520;
    private static final double MIN_WINDOW_HEIGHT = 620;

    /** Loads the main FXML view and displays the application window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/epi/gui/MainWindow.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Epi - Your Purr-fect Task Companion");
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
