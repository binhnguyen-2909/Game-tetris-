package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu {
    private Stage stage;
    private Runnable onPlay;
    private Runnable onLeaderboard;
    private Runnable onSettings;

    public MainMenu(Stage stage, Runnable onPlay, Runnable onLeaderboard, Runnable onSettings) {
        this.stage = stage;
        this.onPlay = onPlay;
        this.onLeaderboard = onLeaderboard;
        this.onSettings = onSettings;
    }

    public Scene createScene() {
        Theme theme = ThemeManager.getInstance().getCurrentTheme();

        VBox root = new VBox(UIConstants.SPACING_LARGE);
        UIStyle.applyScreenBackground(root, theme);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = UIStyle.title("BLOCK BLAST", theme, UIConstants.FONT_TITLE);

        Label subtitle = new Label("Tetris-style block puzzle");
        subtitle.setStyle("-fx-text-fill: " + UIStyle.hex(theme.getSecondaryTextColor())
                + "; -fx-font-size: " + UIConstants.FONT_SMALL + "px; -fx-font-style: italic;");

        VBox buttons = new VBox(UIConstants.SPACING_MEDIUM);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(UIConstants.SPACING_LARGE));
        buttons.setMaxWidth(UIConstants.BUTTON_WIDTH_SMALL + 2 * UIConstants.SPACING_LARGE);
        buttons.setStyle(UIStyle.cardCss(theme));

        Button playButton = menuButton(UIStyle.primaryButton("CHƠI GAME", theme), e -> onPlay.run());
        Button leaderboardButton = menuButton(UIStyle.primaryButton("BẢNG XẾP HẠNG", theme), e -> onLeaderboard.run());
        Button settingsButton = menuButton(UIStyle.primaryButton("CÀI ĐẶT", theme), e -> onSettings.run());

        buttons.getChildren().addAll(playButton, leaderboardButton, settingsButton);
        root.getChildren().addAll(title, subtitle, buttons);

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }

    private Button menuButton(Button b, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        b.setOnAction(handler);
        return b;
    }
}
