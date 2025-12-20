package ui;

import game.Difficulty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ui.UIConstants;

public class DifficultyScreen {
    private Stage stage;
    private java.util.function.Consumer<Difficulty> onDifficultySelected;

    public DifficultyScreen(Stage stage, java.util.function.Consumer<Difficulty> onDifficultySelected) {
        this.stage = stage;
        this.onDifficultySelected = onDifficultySelected;
    }

    public Scene createScene(Runnable onBack) {
        VBox root = new VBox(UIConstants.SPACING_LARGE);
        root.setStyle("-fx-background-color: #1e1e1e;");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = new Label("CHỌN ĐỘ KHÓ");
        title.setFont(Font.font("Arial", UIConstants.FONT_SUBTITLE));
        title.setStyle("-fx-text-fill: #ffff00;");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        Button easyButton = createDifficultyButton("DỄ", Difficulty.EASY);
        Button mediumButton = createDifficultyButton("TRUNG BÌNH", Difficulty.MEDIUM);
        Button hardButton = createDifficultyButton("KHÓ", Difficulty.HARD);

        Button backButton = new Button("QUAY LẠI");
        backButton.setStyle("-fx-font-size: " + UIConstants.FONT_TINY + "; -fx-padding: " + UIConstants.BUTTON_PADDING_SMALL + "; -fx-background-color: #666666; -fx-text-fill: white;");
        backButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        backButton.setOnAction(e -> onBack.run());

        root.getChildren().addAll(title, easyButton, mediumButton, hardButton, backButton);

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }

    private Button createDifficultyButton(String label, Difficulty difficulty) {
        Button button = new Button(label);
        button.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + "; -fx-background-color: #0066ff; -fx-text-fill: white;");
        button.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        button.setOnAction(e -> onDifficultySelected.accept(difficulty));
        return button;
    }
}
