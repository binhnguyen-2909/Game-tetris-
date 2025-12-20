package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PauseScreen {
    private Stage stage;
    private Runnable onResume;
    private Runnable onBackToMenu;
    private Runnable onSettings;
    private Runnable onQuit;

    public PauseScreen(Stage stage, Runnable onResume, Runnable onBackToMenu, Runnable onSettings, Runnable onQuit) {
        this.stage = stage;
        this.onResume = onResume;
        this.onBackToMenu = onBackToMenu;
        this.onSettings = onSettings;
        this.onQuit = onQuit;
    }

    public Scene createScene() {
        VBox root = new VBox(UIConstants.SPACING_LARGE);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        // Title
        Text title = new Text("PAUSED");
        title.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_TITLE));
        title.setFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(255,255,255,0.8), 10, 0, 0, 0);");

        // Resume button
        Button resumeButton = createMenuButton("Resume", () -> {
            if (onResume != null) {
                onResume.run();
            }
        });

        // Back to Menu button
        Button backToMenuButton = createMenuButton("Back to Menu", () -> {
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });

        // Settings button
        Button settingsButton = createMenuButton("Settings", () -> {
            if (onSettings != null) {
                onSettings.run();
            }
        });

        // Quit button
        Button quitButton = createMenuButton("Quit", () -> {
            if (onQuit != null) {
                onQuit.run();
            }
        });

        root.getChildren().addAll(
            title,
            resumeButton,
            backToMenuButton,
            settingsButton,
            quitButton
        );

        Scene scene = new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        
        // Xử lý phím P để resume
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.P || 
                event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                if (onResume != null) {
                    onResume.run();
                }
            }
        });

        return scene;
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", UIConstants.FONT_LARGE));
        button.setPrefWidth(UIConstants.BUTTON_WIDTH);
        button.setPrefHeight(UIConstants.BUTTON_HEIGHT);
        button.setStyle(
            "-fx-background-color: #4a4a4a; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: #666666; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: #5a5a5a; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #777777; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-cursor: hand;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: #4a4a4a; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #666666; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-cursor: hand;"
            );
        });
        
        button.setOnAction(e -> {
            if (action != null) {
                action.run();
            }
        });

        return button;
    }
}

