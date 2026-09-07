package epi.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Represents one message bubble in the Epi conversation. */
public class DialogBox extends HBox {
    private DialogBox(String message, boolean fromUser) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.getStyleClass().add(fromUser ? "user-message" : "epi-message");
        getChildren().add(label);
        getStyleClass().add(fromUser ? "user-box" : "epi-box");
    }

    /** Creates a user message bubble. */
    public static DialogBox user(String message) {
        return new DialogBox(message, true);
    }

    /** Creates an Epi message bubble. */
    public static DialogBox epi(String message) {
        return new DialogBox(message, false);
    }
}
