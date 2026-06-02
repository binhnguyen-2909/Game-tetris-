package ui;

import utils.GameScore;
import utils.LeaderboardManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LeaderboardScreen {
    private LeaderboardManager leaderboardManager;
    private Stage stage;

    public LeaderboardScreen(Stage stage, LeaderboardManager leaderboardManager) {
        this.stage = stage;
        this.leaderboardManager = leaderboardManager;
    }

    public Scene createScene(Runnable onBack) {
        Theme theme = ThemeManager.getInstance().getCurrentTheme();

        VBox root = new VBox(UIConstants.SPACING_MEDIUM);
        UIStyle.applyScreenBackground(root, theme);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = UIStyle.title("BẢNG XẾP HẠNG TOP 10", theme, UIConstants.FONT_SUBTITLE);

        VBox scoresList = new VBox(UIConstants.SPACING_SMALL);
        scoresList.setPadding(new Insets(UIConstants.SPACING_MEDIUM));
        scoresList.setAlignment(Pos.TOP_CENTER);

        int rank = 1;
        java.util.List<GameScore> top = leaderboardManager.getTopScores();
        if (top.isEmpty()) {
            Label empty = new Label("Chưa có điểm nào. Hãy chơi một ván!");
            empty.setFont(Font.font("Arial", UIConstants.FONT_SMALL));
            empty.setStyle("-fx-text-fill: " + UIStyle.hex(theme.getSecondaryTextColor()) + ";");
            scoresList.getChildren().add(empty);
        } else {
            for (GameScore score : top) {
                scoresList.getChildren().add(createRow(rank++, score, theme));
            }
        }

        ScrollPane scrollPane = new ScrollPane(scoresList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setPrefViewportHeight(UIConstants.WINDOW_HEIGHT - 180);
        scrollPane.setMaxWidth(UIConstants.WINDOW_WIDTH * 0.7);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Button backButton = UIStyle.secondaryButton("QUAY LẠI", theme);
        backButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        backButton.setOnAction(e -> onBack.run());

        root.getChildren().addAll(title, scrollPane, backButton);
        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }

    private HBox createRow(int rank, GameScore score, Theme theme) {
        HBox row = new HBox(UIConstants.SPACING_MEDIUM);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(UIConstants.SPACING_SMALL, UIConstants.SPACING_MEDIUM,
                UIConstants.SPACING_SMALL, UIConstants.SPACING_MEDIUM));
        row.setMaxWidth(UIConstants.WINDOW_WIDTH * 0.66);

        Color rowBg = (rank % 2 == 0)
                ? theme.getBoardBackgroundColor().interpolate(Color.WHITE, 0.04)
                : theme.getBoardBackgroundColor();
        row.setStyle("-fx-background-color: " + UIStyle.rgba(rowBg, 0.85) + "; -fx-background-radius: 10;");

        Label rankLabel = new Label(medal(rank));
        rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_MEDIUM));
        rankLabel.setStyle("-fx-text-fill: " + UIStyle.hex(rankColor(rank, theme)) + ";");
        rankLabel.setMinWidth(48);

        Label nameLabel = new Label(score.getPlayerName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_SMALL));
        nameLabel.setStyle("-fx-text-fill: " + UIStyle.hex(theme.getPrimaryTextColor()) + ";");

        Label modeLabel = new Label(score.getDifficulty());
        modeLabel.setFont(Font.font("Arial", UIConstants.FONT_TINY));
        modeLabel.setStyle("-fx-text-fill: " + UIStyle.hex(theme.getSecondaryTextColor()) + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label scoreLabel = new Label(String.valueOf(score.getScore()));
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_MEDIUM));
        scoreLabel.setStyle("-fx-text-fill: " + UIStyle.hex(theme.getAccentTextColor()) + ";");

        VBox info = new VBox(2, nameLabel, modeLabel);
        info.setAlignment(Pos.CENTER_LEFT);

        row.getChildren().addAll(rankLabel, info, spacer, scoreLabel);
        return row;
    }

    private String medal(int rank) {
        switch (rank) {
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            default: return rank + ".";
        }
    }

    private Color rankColor(int rank, Theme theme) {
        switch (rank) {
            case 1: return Color.rgb(255, 215, 0);   // gold
            case 2: return Color.rgb(192, 192, 210);  // silver
            case 3: return Color.rgb(205, 127, 50);   // bronze
            default: return theme.getSecondaryTextColor();
        }
    }
}
