package ui;

import game.Difficulty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DifficultyScreen {
    private Stage stage;
    private java.util.function.Consumer<Difficulty> onDifficultySelected;

    public DifficultyScreen(Stage stage, java.util.function.Consumer<Difficulty> onDifficultySelected) {
        this.stage = stage;
        this.onDifficultySelected = onDifficultySelected;
    }

    public Scene createScene(Runnable onBack) {
        Theme theme = ThemeManager.getInstance().getCurrentTheme();

        VBox root = new VBox(UIConstants.SPACING_LARGE);
        UIStyle.applyScreenBackground(root, theme);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = UIStyle.title("CHỌN ĐỘ KHÓ", theme, UIConstants.FONT_SUBTITLE);

        VBox card = new VBox(UIConstants.SPACING_MEDIUM);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(UIConstants.SPACING_LARGE));
        card.setMaxWidth(UIConstants.BUTTON_WIDTH_SMALL + 2 * UIConstants.SPACING_LARGE);
        card.setStyle(UIStyle.cardCss(theme));

        card.getChildren().addAll(
                difficultyButton("DỄ", Difficulty.EASY, theme),
                difficultyButton("TRUNG BÌNH", Difficulty.MEDIUM, theme),
                difficultyButton("KHÓ", Difficulty.HARD, theme));

        Button backButton = UIStyle.secondaryButton("QUAY LẠI", theme);
        backButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        backButton.setOnAction(e -> onBack.run());

        root.getChildren().addAll(title, card, backButton);

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }

    private Button difficultyButton(String label, Difficulty difficulty, Theme theme) {
        Button button = UIStyle.primaryButton(label, theme);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        button.setOnAction(e -> onDifficultySelected.accept(difficulty));
        return button;
    }
}
