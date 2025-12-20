package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SettingsScreen {
    private Stage stage;
    private ThemeManager themeManager;
    private Runnable onBack;

    public SettingsScreen(Stage stage, Runnable onBack) {
        this.stage = stage;
        this.themeManager = ThemeManager.getInstance();
        this.onBack = onBack;
    }

    public Scene createScene() {
        VBox root = new VBox(UIConstants.SPACING_LARGE);
        Theme currentTheme = themeManager.getCurrentTheme();
        String bgColor = currentTheme.colorToCss(currentTheme.getBackgroundColor());
        root.setStyle("-fx-background-color: " + bgColor + ";");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = new Label("CÀI ĐẶT");
        title.setFont(Font.font("Arial", UIConstants.FONT_SUBTITLE));
        title.setStyle("-fx-text-fill: " + currentTheme.colorToCss(currentTheme.getAccentTextColor()) + ";");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        Label themeLabel = new Label("CHỌN CHỦ ĐỀ MÀU:");
        themeLabel.setFont(Font.font("Arial", UIConstants.FONT_MEDIUM));
        themeLabel.setStyle("-fx-text-fill: " + currentTheme.colorToCss(currentTheme.getPrimaryTextColor()) + ";");
        themeLabel.setAlignment(Pos.CENTER);
        themeLabel.setMaxWidth(Double.MAX_VALUE);

        // Theme buttons
        VBox themeButtons = new VBox(UIConstants.SPACING_SMALL);
        themeButtons.setAlignment(Pos.CENTER);

        Button darkButton = createThemeButton("Dark", Theme.ThemeType.DARK);
        Button lightButton = createThemeButton("Light", Theme.ThemeType.LIGHT);
        Button neonButton = createThemeButton("Neon", Theme.ThemeType.NEON);
        Button pastelButton = createThemeButton("Pastel", Theme.ThemeType.PASTEL);
        Button retroButton = createThemeButton("Retro", Theme.ThemeType.RETRO);

        themeButtons.getChildren().addAll(darkButton, lightButton, neonButton, pastelButton, retroButton);

        Button backButton = new Button("QUAY LẠI");
        String btnColor = currentTheme.colorToCss(currentTheme.getButtonColor());
        String btnHoverColor = currentTheme.colorToCss(currentTheme.getButtonHoverColor());
        String btnTextColor = currentTheme.colorToCss(currentTheme.getButtonTextColor());
        backButton.setStyle("-fx-font-size: " + UIConstants.FONT_TINY + "; -fx-padding: " + UIConstants.BUTTON_PADDING_SMALL + 
                          "; -fx-background-color: " + btnColor + "; -fx-text-fill: " + btnTextColor + ";");
        backButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        backButton.setOnAction(e -> {
            onBack.run();
        });
        
        // Hover effect
        backButton.setOnMouseEntered(e -> {
            backButton.setStyle("-fx-font-size: " + UIConstants.FONT_TINY + "; -fx-padding: " + UIConstants.BUTTON_PADDING_SMALL + 
                               "; -fx-background-color: " + btnHoverColor + "; -fx-text-fill: " + btnTextColor + ";");
        });
        backButton.setOnMouseExited(e -> {
            backButton.setStyle("-fx-font-size: " + UIConstants.FONT_TINY + "; -fx-padding: " + UIConstants.BUTTON_PADDING_SMALL + 
                               "; -fx-background-color: " + btnColor + "; -fx-text-fill: " + btnTextColor + ";");
        });

        root.getChildren().addAll(title, themeLabel, themeButtons, backButton);

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }

    private Button createThemeButton(String label, Theme.ThemeType themeType) {
        Theme currentTheme = themeManager.getCurrentTheme();
        Theme previewTheme = Theme.getTheme(themeType);
        
        Button button = new Button(label);
        String btnColor = currentTheme.colorToCss(currentTheme.getButtonColor());
        String btnHoverColor = currentTheme.colorToCss(currentTheme.getButtonHoverColor());
        String btnTextColor = currentTheme.colorToCss(currentTheme.getButtonTextColor());
        
        // Highlight current theme
        if (themeManager.getCurrentTheme().getType() == themeType) {
            button.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + 
                           "; -fx-background-color: " + btnHoverColor + "; -fx-text-fill: " + btnTextColor + 
                           "; -fx-border-color: " + currentTheme.colorToCss(currentTheme.getAccentTextColor()) + "; -fx-border-width: 2;");
        } else {
            button.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + 
                           "; -fx-background-color: " + btnColor + "; -fx-text-fill: " + btnTextColor + ";");
        }
        
        button.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        button.setOnAction(e -> {
            themeManager.setTheme(themeType);
            // Reload scene với theme mới
            Scene newScene = createScene();
            stage.setScene(newScene);
        });
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            if (themeManager.getCurrentTheme().getType() != themeType) {
                button.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + 
                               "; -fx-background-color: " + btnHoverColor + "; -fx-text-fill: " + btnTextColor + ";");
            }
        });
        button.setOnMouseExited(e -> {
            if (themeManager.getCurrentTheme().getType() != themeType) {
                button.setStyle("-fx-font-size: " + UIConstants.FONT_MEDIUM + "; -fx-padding: " + UIConstants.BUTTON_PADDING + 
                               "; -fx-background-color: " + btnColor + "; -fx-text-fill: " + btnTextColor + ";");
            }
        });
        
        return button;
    }
}

