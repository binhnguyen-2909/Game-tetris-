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
        Theme theme = ThemeManager.getInstance().getCurrentTheme();

        VBox root = new VBox(UIConstants.SPACING_LARGE);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.82);");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Text title = new Text("TẠM DỪNG");
        title.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_TITLE));
        title.setFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 12, 0, 0, 0);");

        VBox card = new VBox(UIConstants.SPACING_MEDIUM);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(UIConstants.SPACING_LARGE));
        card.setMaxWidth(UIConstants.BUTTON_WIDTH + 2 * UIConstants.SPACING_LARGE);
        card.setStyle(UIStyle.cardCss(theme));

        card.getChildren().addAll(
                menuButton(UIStyle.primaryButton("Tiếp tục", theme), onResume),
                menuButton(UIStyle.primaryButton("Về Menu", theme), onBackToMenu),
                menuButton(UIStyle.primaryButton("Cài đặt", theme), onSettings),
                menuButton(UIStyle.secondaryButton("Thoát", theme), onQuit));

        root.getChildren().addAll(title, card);

        Scene scene = new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.P
                    || event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                if (onResume != null) {
                    onResume.run();
                }
            }
        });

        return scene;
    }

    private Button menuButton(Button button, Runnable action) {
        button.setPrefWidth(UIConstants.BUTTON_WIDTH);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> {
            if (action != null) {
                action.run();
            }
        });
        return button;
    }
}
