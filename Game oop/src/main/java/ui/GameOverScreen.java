package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ui.UIConstants;

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
        VBox root = new VBox(UIConstants.SPACING_SMALL);
        root.setStyle("-fx-background-color: #1e1e1e;");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = new Label(isVictory ? "CHIẾN THẮNG!" : "GAME OVER");
        title.setFont(Font.font("Arial", UIConstants.FONT_TITLE));
        title.setStyle("-fx-text-fill: " + (isVictory ? "#00ff00" : "#ff0000") + ";");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        Label scoreText = new Label("ĐIỂM CỦA BẠN: " + finalScore);
        scoreText.setFont(Font.font("Arial", UIConstants.FONT_LARGE));
        scoreText.setStyle("-fx-text-fill: #00ff00;");
        scoreText.setAlignment(Pos.CENTER);
        scoreText.setMaxWidth(Double.MAX_VALUE);

        // Chỉ hiển thị input tên và nút lưu nếu không phải practice mode
        if (isPracticeMode) {
            // Practice mode: hiển thị thông báo
            Label practiceLabel = new Label("(Chế độ luyện tập - Điểm không được lưu)");
            practiceLabel.setFont(Font.font("Arial", UIConstants.FONT_SMALL));
            practiceLabel.setStyle("-fx-text-fill: #cccccc;");
            practiceLabel.setAlignment(Pos.CENTER);
            practiceLabel.setMaxWidth(Double.MAX_VALUE);
            
            Button backButton = new Button("QUAY LẠI");
            backButton.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + "; -fx-background-color: #666666; -fx-text-fill: white;");
            backButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
            backButton.setOnAction(e -> onSaveAndBack.run());
            
            root.getChildren().addAll(title, scoreText, practiceLabel, backButton);
        } else {
            // Normal mode: hiển thị form lưu điểm
            Label nameLabel = new Label("NHẬP TÊN:");
            nameLabel.setFont(Font.font("Arial", UIConstants.FONT_MEDIUM));
            nameLabel.setStyle("-fx-text-fill: #ffffff;");
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.setMaxWidth(Double.MAX_VALUE);

            TextField nameField = new TextField();
            nameField.setPromptText("Tên của bạn");
            nameField.setPrefWidth(UIConstants.TEXT_FIELD_WIDTH);
            nameField.setMaxWidth(UIConstants.TEXT_FIELD_WIDTH);
            nameField.setFont(Font.font("Arial", UIConstants.FONT_SMALL));
            nameField.setStyle("-fx-padding: " + UIConstants.TEXT_FIELD_PADDING + ";");

            Button saveButton = new Button("LƯU ĐIỂM");
            saveButton.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + "; -fx-background-color: #0066ff; -fx-text-fill: white;");
            saveButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
            saveButton.setOnAction(e -> {
                String playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = "Player";
                }
                onSaveScore.accept(playerName);
                onSaveAndBack.run();
            });

            Button skipButton = new Button("BỎ QUA");
            skipButton.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + "; -fx-background-color: #666666; -fx-text-fill: white;");
            skipButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
            skipButton.setOnAction(e -> onSaveAndBack.run());
            
            root.getChildren().addAll(title, scoreText, nameLabel, nameField, saveButton, skipButton);
        }

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }
}
