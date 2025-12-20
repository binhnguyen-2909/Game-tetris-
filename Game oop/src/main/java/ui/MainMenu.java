package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ui.UIConstants;
import ui.Theme;
import ui.ThemeManager;

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
        Theme currentTheme = ThemeManager.getInstance().getCurrentTheme();
        
        VBox root = new VBox(UIConstants.SPACING_LARGE);
        String bgColor = currentTheme.colorToCss(currentTheme.getBackgroundColor());
        root.setStyle("-fx-background-color: " + bgColor + ";");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = new Label("BLOCK BLAST");
        title.setFont(Font.font("Arial", UIConstants.FONT_TITLE));
        title.setStyle("-fx-text-fill: " + currentTheme.colorToCss(currentTheme.getAccentTextColor()) + ";");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        Button playButton = createThemedButton("CHƠI GAME", currentTheme, e -> onPlay.run());
        Button leaderboardButton = createThemedButton("BẢNG XẾP HẠNG", currentTheme, e -> onLeaderboard.run());
        Button settingsButton = createThemedButton("CÀI ĐẶT", currentTheme, e -> onSettings.run());

        root.getChildren().addAll(title, playButton, leaderboardButton, settingsButton);

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }
    
    private Button createThemedButton(String text, Theme theme, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        String btnColor = theme.colorToCss(theme.getButtonColor());
        String btnHoverColor = theme.colorToCss(theme.getButtonHoverColor());
        String btnTextColor = theme.colorToCss(theme.getButtonTextColor());
        
        button.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + 
                       "; -fx-background-color: " + btnColor + "; -fx-text-fill: " + btnTextColor + ";");
        button.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        button.setOnAction(handler);
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + 
                           "; -fx-background-color: " + btnHoverColor + "; -fx-text-fill: " + btnTextColor + ";");
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + 
                           "; -fx-background-color: " + btnColor + "; -fx-text-fill: " + btnTextColor + ";");
        });
        
        return button;
    }
}
