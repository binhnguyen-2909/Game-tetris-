package ui;

import game.GameMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GameModeScreen {
    private Stage stage;
    private java.util.function.Consumer<GameMode> onModeSelected;

    public GameModeScreen(Stage stage, java.util.function.Consumer<GameMode> onModeSelected) {
        this.stage = stage;
        this.onModeSelected = onModeSelected;
    }

    public Scene createScene(Runnable onBack) {
        VBox root = new VBox(UIConstants.SPACING_MEDIUM);
        root.setStyle("-fx-background-color: #1e1e1e;");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = new Label("CHỌN CHẾ ĐỘ CHƠI");
        title.setFont(Font.font("Arial", UIConstants.FONT_SUBTITLE));
        title.setStyle("-fx-text-fill: #ffff00;");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        Button marathonButton = createModeButton("MARATHON", "Chơi không giới hạn, tốc độ tăng dần", GameMode.MARATHON);
        Button sprintButton = createModeButton("SPRINT", "Xóa 40 dòng nhanh nhất", GameMode.SPRINT);
        Button challengeButton = createModeButton("CHALLENGE", "Các thử thách đặc biệt", GameMode.CHALLENGE);
        Button zenButton = createModeButton("ZEN", "Thư giãn - không tự động rơi", GameMode.ZEN);

        Button backButton = new Button("QUAY LẠI");
        backButton.setStyle("-fx-font-size: " + UIConstants.FONT_TINY + "; -fx-padding: " + UIConstants.BUTTON_PADDING_SMALL + "; -fx-background-color: #666666; -fx-text-fill: white;");
        backButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        backButton.setOnAction(e -> onBack.run());

        root.getChildren().addAll(title, marathonButton, sprintButton, challengeButton, zenButton, backButton);

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }

    private Button createModeButton(String title, String description, GameMode mode) {
        VBox buttonContent = new VBox(5);
        buttonContent.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", UIConstants.FONT_MEDIUM));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", UIConstants.FONT_TINY));
        descLabel.setStyle("-fx-text-fill: #cccccc;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(UIConstants.BUTTON_WIDTH_SMALL - 20);
        
        buttonContent.getChildren().addAll(titleLabel, descLabel);
        
        Button button = new Button();
        button.setGraphic(buttonContent);
        button.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + "; -fx-background-color: #0066ff; -fx-text-fill: white;");
        button.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        button.setOnAction(e -> onModeSelected.accept(mode));
        
        return button;
    }
}

