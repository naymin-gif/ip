package epi.gui;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** Represents one message bubble in the Epi conversation. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 60;

    private DialogBox(String message, boolean fromUser) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.getStyleClass().add(fromUser ? "user-message" : "epi-message");
        String imagePath = fromUser
                ? "/epi/gui/images/user.png"
                : "/epi/gui/images/epi.png";
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView avatar = new ImageView(image);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        if (fromUser) {
            getChildren().addAll(label, avatar);
        } else {
            getChildren().addAll(avatar, label);
        }
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
