package ui;

import utils.GameScore;
import utils.LeaderboardManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ui.UIConstants;

public class LeaderboardScreen {
    private LeaderboardManager leaderboardManager;
    private Stage stage;

    public LeaderboardScreen(Stage stage, LeaderboardManager leaderboardManager) {
        this.stage = stage;
        this.leaderboardManager = leaderboardManager;
    }

    public Scene createScene(Runnable onBack) {
        VBox root = new VBox(UIConstants.SPACING_SMALL);
        root.setStyle("-fx-background-color: #1e1e1e;");
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = new Label("BẢNG XẾP HẠNG TOP 10");
        title.setFont(Font.font("Arial", UIConstants.FONT_SUBTITLE));
        title.setStyle("-fx-text-fill: #ffff00;");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setWrapText(true);

        VBox scoresList = new VBox(UIConstants.SPACING_SMALL);
        scoresList.setPadding(new Insets(UIConstants.SPACING_SMALL));
        scoresList.setAlignment(Pos.CENTER);
        scoresList.setPrefWidth(UIConstants.WINDOW_WIDTH - 2 * UIConstants.PADDING);

        int rank = 1;
        for (GameScore score : leaderboardManager.getTopScores()) {
            Label scoreLabel = new Label(
                String.format("%d. %s - %d (Độ: %s)",
                    rank++,
                    score.getPlayerName(),
                    score.getScore(),
                    score.getDifficulty())
            );
            scoreLabel.setFont(Font.font("Arial", UIConstants.FONT_SMALL));
            scoreLabel.setStyle("-fx-text-fill: #00ff00;");
            scoreLabel.setAlignment(Pos.CENTER);
            scoreLabel.setMaxWidth(UIConstants.WINDOW_WIDTH - 2 * UIConstants.PADDING);
            scoreLabel.setWrapText(true);
            scoresList.getChildren().add(scoreLabel);
        }

        // ScrollPane để cuộn nếu có nhiều scores
        ScrollPane scrollPane = new ScrollPane(scoresList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1e1e1e; -fx-border-color: #1e1e1e;");
        scrollPane.setPrefViewportHeight(UIConstants.WINDOW_HEIGHT - 180); // Trừ space cho title và button
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Button backButton = new Button("QUAY LẠI");
        backButton.setStyle("-fx-font-size: " + UIConstants.FONT_TINY + "; -fx-padding: " + UIConstants.BUTTON_PADDING_SMALL + "; -fx-background-color: #666666; -fx-text-fill: white;");
        backButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        backButton.setOnAction(e -> onBack.run());

        root.getChildren().addAll(title, scrollPane, backButton);
        VBox.setVgrow(title, Priority.NEVER);
        VBox.setVgrow(backButton, Priority.NEVER);

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }
}
