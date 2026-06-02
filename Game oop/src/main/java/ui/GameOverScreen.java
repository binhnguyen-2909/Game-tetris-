package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GameOverScreen {
    private Stage stage;
    private int finalScore;
    private boolean isVictory;
    private boolean isPracticeMode;
    private Runnable onSaveAndBack;

    public GameOverScreen(Stage stage, int finalScore, boolean isVictory, boolean isPracticeMode, Runnable onSaveAndBack) {
        this.stage = stage;
        this.finalScore = finalScore;
        this.isVictory = isVictory;
        this.isPracticeMode = isPracticeMode;
        this.onSaveAndBack = onSaveAndBack;
    }

    public Scene createScene(java.util.function.Consumer<String> onSaveScore) {
        Theme theme = ThemeManager.getInstance().getCurrentTheme();

        VBox root = new VBox(UIConstants.SPACING_MEDIUM);
        UIStyle.applyScreenBackground(root, theme);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Color titleColor = isVictory ? Color.rgb(80, 230, 120) : Color.rgb(255, 90, 90);
        Label title = new Label(isVictory ? "CHIẾN THẮNG!" : "GAME OVER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_TITLE));
        title.setStyle("-fx-text-fill: " + UIStyle.hex(titleColor) + ";"
                + "-fx-effect: dropshadow(gaussian, " + UIStyle.rgba(titleColor, 0.6) + ", 16, 0.2, 0, 0);");

        VBox card = new VBox(UIConstants.SPACING_MEDIUM);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(UIConstants.SPACING_LARGE));
        card.setMaxWidth(UIConstants.TEXT_FIELD_WIDTH + 2 * UIConstants.SPACING_LARGE);
        card.setStyle(UIStyle.cardCss(theme));

        Label scoreText = new Label("ĐIỂM CỦA BẠN: " + finalScore);
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_LARGE));
        scoreText.setStyle("-fx-text-fill: " + UIStyle.hex(theme.getAccentTextColor()) + ";");

        if (isPracticeMode) {
            Label practiceLabel = new Label("(Chế độ luyện tập - Điểm không được lưu)");
            practiceLabel.setFont(Font.font("Arial", UIConstants.FONT_SMALL));
            practiceLabel.setStyle("-fx-text-fill: " + UIStyle.hex(theme.getSecondaryTextColor()) + ";");

            Button backButton = UIStyle.primaryButton("QUAY LẠI", theme);
            backButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
            backButton.setOnAction(e -> onSaveAndBack.run());

            card.getChildren().addAll(scoreText, practiceLabel, backButton);
        } else {
            Label nameLabel = new Label("NHẬP TÊN:");
            nameLabel.setFont(Font.font("Arial", UIConstants.FONT_MEDIUM));
            nameLabel.setStyle("-fx-text-fill: " + UIStyle.hex(theme.getPrimaryTextColor()) + ";");

            TextField nameField = new TextField();
            nameField.setPromptText("Tên của bạn");
            nameField.setMaxWidth(UIConstants.TEXT_FIELD_WIDTH);
            nameField.setFont(Font.font("Arial", UIConstants.FONT_SMALL));
            nameField.setStyle("-fx-padding: " + UIConstants.TEXT_FIELD_PADDING + "; -fx-background-radius: 8;");

            Button saveButton = UIStyle.primaryButton("LƯU ĐIỂM", theme);
            saveButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
            saveButton.setOnAction(e -> {
                String playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = "Player";
                }
                onSaveScore.accept(playerName);
                onSaveAndBack.run();
            });

            Button skipButton = UIStyle.secondaryButton("BỎ QUA", theme);
            skipButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
            skipButton.setOnAction(e -> onSaveAndBack.run());

            card.getChildren().addAll(scoreText, nameLabel, nameField, saveButton, skipButton);
        }

        root.getChildren().addAll(title, card);

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }
}
