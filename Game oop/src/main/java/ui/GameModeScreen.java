package ui;

import game.GameMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GameModeScreen {
    private Stage stage;
    private java.util.function.Consumer<GameMode> onModeSelected;

    public GameModeScreen(Stage stage, java.util.function.Consumer<GameMode> onModeSelected) {
        this.stage = stage;
        this.onModeSelected = onModeSelected;
    }

    public Scene createScene(Runnable onBack) {
        Theme theme = ThemeManager.getInstance().getCurrentTheme();

        VBox root = new VBox(UIConstants.SPACING_MEDIUM);
        UIStyle.applyScreenBackground(root, theme);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        Label title = UIStyle.title("CHỌN CHẾ ĐỘ CHƠI", theme, UIConstants.FONT_SUBTITLE);

        VBox list = new VBox(UIConstants.SPACING_SMALL);
        list.setAlignment(Pos.CENTER);
        list.setPadding(new Insets(UIConstants.SPACING_MEDIUM));
        list.setMaxWidth(UIConstants.BUTTON_WIDTH_SMALL + 4 * UIConstants.SPACING_LARGE);
        list.setStyle(UIStyle.cardCss(theme));

        list.getChildren().addAll(
                createModeButton("MARATHON", "Chơi không giới hạn, tốc độ tăng dần", GameMode.MARATHON, theme),
                createModeButton("SPRINT", "Xóa 40 dòng nhanh nhất", GameMode.SPRINT, theme),
                createModeButton("CHALLENGE", "Các thử thách đặc biệt", GameMode.CHALLENGE, theme),
                createModeButton("ZEN", "Thư giãn - không tự động rơi", GameMode.ZEN, theme));

        Button backButton = UIStyle.secondaryButton("QUAY LẠI", theme);
        backButton.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL);
        backButton.setOnAction(e -> onBack.run());

        root.getChildren().addAll(title, list, backButton);

        return new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
    }

    private Button createModeButton(String name, String description, GameMode mode, Theme theme) {
        VBox content = new VBox(3);
        content.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(name);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_MEDIUM));
        titleLabel.setStyle("-fx-text-fill: " + UIStyle.hex(theme.getButtonTextColor()) + ";");

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", UIConstants.FONT_TINY));
        descLabel.setStyle("-fx-text-fill: " + UIStyle.rgba(theme.getButtonTextColor(), 0.8) + ";");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(UIConstants.BUTTON_WIDTH_SMALL + 2 * UIConstants.SPACING_LARGE);

        content.getChildren().addAll(titleLabel, descLabel);

        Button button = UIStyle.primaryButton("", theme);
        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefWidth(UIConstants.BUTTON_WIDTH_SMALL + 3 * UIConstants.SPACING_LARGE);
        button.setOnAction(e -> onModeSelected.accept(mode));
        return button;
    }
}
