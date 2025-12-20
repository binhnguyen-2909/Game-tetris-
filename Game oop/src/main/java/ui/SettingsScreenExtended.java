package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SettingsScreenExtended {
    private Stage stage;
    private ThemeManager themeManager;
    private GameSettings settings;
    private Runnable onBack;
    
    // Key remapping
    private KeyCode waitingForKey = null;
    private Button waitingButton = null;

    public SettingsScreenExtended(Stage stage, Runnable onBack) {
        this.stage = stage;
        this.themeManager = ThemeManager.getInstance();
        this.settings = GameSettings.getInstance();
        this.onBack = onBack;
    }

    public Scene createScene() {
        Theme currentTheme = themeManager.getCurrentTheme();
        
        VBox root = new VBox(UIConstants.SPACING_MEDIUM);
        String bgColor = currentTheme.colorToCss(currentTheme.getBackgroundColor());
        root.setStyle("-fx-background-color: " + bgColor + ";");
        root.setPadding(new Insets(UIConstants.PADDING));
        root.setPrefSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        // Title
        Label title = new Label("CÀI ĐẶT");
        title.setFont(Font.font("Arial", UIConstants.FONT_SUBTITLE));
        title.setStyle("-fx-text-fill: " + currentTheme.colorToCss(currentTheme.getAccentTextColor()) + ";");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        // ScrollPane để cuộn nếu có nhiều options
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        VBox content = new VBox(UIConstants.SPACING_MEDIUM);
        content.setPadding(new Insets(UIConstants.SPACING_SMALL));
        
        // 1. Theme Selection
        content.getChildren().add(createSectionTitle("CHỦ ĐỀ MÀU", currentTheme));
        content.getChildren().add(createThemeSection(currentTheme));
        
        // 2. Key Bindings
        content.getChildren().add(createSectionTitle("ĐIỀU KHIỂN", currentTheme));
        content.getChildren().add(createKeyBindingSection(currentTheme));
        
        // 3. Gameplay Settings
        content.getChildren().add(createSectionTitle("GAMEPLAY", currentTheme));
        content.getChildren().add(createGameplaySection(currentTheme));
        
        // 4. Visual Settings
        content.getChildren().add(createSectionTitle("HIỂN THỊ", currentTheme));
        content.getChildren().add(createVisualSection(currentTheme));
        
        // 5. Audio Settings (placeholder)
        content.getChildren().add(createSectionTitle("ÂM THANH", currentTheme));
        content.getChildren().add(createAudioSection(currentTheme));
        
        scrollPane.setContent(content);
        
        // Back button
        Button backButton = createThemedButton("QUAY LẠI", currentTheme, e -> onBack.run());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        VBox.setVgrow(backButton, Priority.NEVER);
        
        root.getChildren().addAll(title, scrollPane, backButton);
        
        Scene scene = new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        
        // Handle key events for key remapping
        scene.setOnKeyPressed(this::handleKeyPress);
        
        return scene;
    }
    
    private Label createSectionTitle(String text, Theme theme) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", UIConstants.FONT_MEDIUM));
        label.setStyle("-fx-text-fill: " + theme.colorToCss(theme.getAccentTextColor()) + "; -fx-font-weight: bold;");
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }
    
    private VBox createThemeSection(Theme theme) {
        VBox section = new VBox(UIConstants.SPACING_SMALL);
        
        ToggleGroup themeGroup = new ToggleGroup();
        RadioButton darkBtn = createRadioButton("Dark", Theme.ThemeType.DARK, themeGroup, theme);
        RadioButton lightBtn = createRadioButton("Light", Theme.ThemeType.LIGHT, themeGroup, theme);
        RadioButton neonBtn = createRadioButton("Neon", Theme.ThemeType.NEON, themeGroup, theme);
        RadioButton pastelBtn = createRadioButton("Pastel", Theme.ThemeType.PASTEL, themeGroup, theme);
        RadioButton retroBtn = createRadioButton("Retro", Theme.ThemeType.RETRO, themeGroup, theme);
        
        // Select current theme
        Theme.ThemeType currentType = themeManager.getCurrentTheme().getType();
        switch (currentType) {
            case DARK: darkBtn.setSelected(true); break;
            case LIGHT: lightBtn.setSelected(true); break;
            case NEON: neonBtn.setSelected(true); break;
            case PASTEL: pastelBtn.setSelected(true); break;
            case RETRO: retroBtn.setSelected(true); break;
        }
        
        section.getChildren().addAll(darkBtn, lightBtn, neonBtn, pastelBtn, retroBtn);
        return section;
    }
    
    private RadioButton createRadioButton(String text, Theme.ThemeType themeType, ToggleGroup group, Theme theme) {
        RadioButton button = new RadioButton(text);
        button.setToggleGroup(group);
        button.setStyle("-fx-text-fill: " + theme.colorToCss(theme.getPrimaryTextColor()) + ";");
        button.setOnAction(e -> {
            themeManager.setTheme(themeType);
            // Reload scene
            Scene newScene = createScene();
            stage.setScene(newScene);
        });
        return button;
    }
    
    private VBox createKeyBindingSection(Theme theme) {
        VBox section = new VBox(UIConstants.SPACING_SMALL);
        
        section.getChildren().add(createKeyBindingRow("Di chuyển trái:", settings.getMoveLeftKey(), 
            theme, button -> {
                waitingForKey = settings.getMoveLeftKey();
                waitingButton = button;
                button.setText("Nhấn phím...");
            }));
        section.getChildren().add(createKeyBindingRow("Di chuyển phải:", settings.getMoveRightKey(), 
            theme, button -> {
                waitingForKey = settings.getMoveRightKey();
                waitingButton = button;
                button.setText("Nhấn phím...");
            }));
        section.getChildren().add(createKeyBindingRow("Rơi xuống:", settings.getMoveDownKey(), 
            theme, button -> {
                waitingForKey = settings.getMoveDownKey();
                waitingButton = button;
                button.setText("Nhấn phím...");
            }));
        section.getChildren().add(createKeyBindingRow("Xoay:", settings.getRotateKey(), 
            theme, button -> {
                waitingForKey = settings.getRotateKey();
                waitingButton = button;
                button.setText("Nhấn phím...");
            }));
        section.getChildren().add(createKeyBindingRow("Rơi nhanh:", settings.getHardDropKey(), 
            theme, button -> {
                waitingForKey = settings.getHardDropKey();
                waitingButton = button;
                button.setText("Nhấn phím...");
            }));
        
        return section;
    }
    
    private HBox createKeyBindingRow(String label, KeyCode currentKey, Theme theme, 
                                     java.util.function.Consumer<Button> onClick) {
        HBox row = new HBox(UIConstants.SPACING_SMALL);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label keyLabel = new Label(label);
        keyLabel.setStyle("-fx-text-fill: " + theme.colorToCss(theme.getPrimaryTextColor()) + ";");
        keyLabel.setPrefWidth(150);
        
        Button keyButton = new Button(currentKey.getName());
        keyButton.setStyle("-fx-font-size: " + UIConstants.FONT_SMALL + "; -fx-padding: " + UIConstants.BUTTON_PADDING_SMALL + 
                          "; -fx-background-color: " + theme.colorToCss(theme.getButtonColor()) + 
                          "; -fx-text-fill: " + theme.colorToCss(theme.getButtonTextColor()) + ";");
        keyButton.setPrefWidth(150);
        keyButton.setOnAction(e -> onClick.accept(keyButton));
        
        row.getChildren().addAll(keyLabel, keyButton);
        return row;
    }
    
    private void handleKeyPress(KeyEvent event) {
        if (waitingForKey != null && waitingButton != null) {
            KeyCode newKey = event.getCode();
            
            // Update setting
            if (waitingForKey == settings.getMoveLeftKey()) {
                settings.setMoveLeftKey(newKey);
            } else if (waitingForKey == settings.getMoveRightKey()) {
                settings.setMoveRightKey(newKey);
            } else if (waitingForKey == settings.getMoveDownKey()) {
                settings.setMoveDownKey(newKey);
            } else if (waitingForKey == settings.getRotateKey()) {
                settings.setRotateKey(newKey);
            } else if (waitingForKey == settings.getHardDropKey()) {
                settings.setHardDropKey(newKey);
            }
            
            waitingButton.setText(newKey.getName());
            waitingForKey = null;
            waitingButton = null;
        }
    }
    
    private VBox createGameplaySection(Theme theme) {
        VBox section = new VBox(UIConstants.SPACING_SMALL);
        
        // Fall Speed Multiplier
        HBox speedRow = new HBox(UIConstants.SPACING_SMALL);
        speedRow.setAlignment(Pos.CENTER_LEFT);
        Label speedLabel = new Label("Tốc độ rơi: " + String.format("%.1fx", settings.getFallSpeedMultiplier()));
        speedLabel.setStyle("-fx-text-fill: " + theme.colorToCss(theme.getPrimaryTextColor()) + ";");
        speedLabel.setPrefWidth(150);
        
        Slider speedSlider = new Slider(0.5, 2.0, settings.getFallSpeedMultiplier());
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            settings.setFallSpeedMultiplier(newVal.doubleValue());
            speedLabel.setText("Tốc độ rơi: " + String.format("%.1fx", newVal.doubleValue()));
        });
        HBox.setHgrow(speedSlider, Priority.ALWAYS);
        speedRow.getChildren().addAll(speedLabel, speedSlider);
        
        // Assist Mode
        CheckBox assistCheck = createCheckBox("Chế độ hỗ trợ (Tự động xoay)", 
            settings.isAssistMode(), theme, settings::setAssistMode);
        
        // Practice Mode
        CheckBox practiceCheck = createCheckBox("Chế độ luyện tập (Không tính điểm)", 
            settings.isPracticeMode(), theme, settings::setPracticeMode);
        
        section.getChildren().addAll(speedRow, assistCheck, practiceCheck);
        return section;
    }
    
    private VBox createVisualSection(Theme theme) {
        VBox section = new VBox(UIConstants.SPACING_SMALL);
        
        // Text Size
        HBox textSizeRow = new HBox(UIConstants.SPACING_SMALL);
        textSizeRow.setAlignment(Pos.CENTER_LEFT);
        Label textSizeLabel = new Label("Kích thước chữ: " + String.format("%.1fx", settings.getTextSizeMultiplier()));
        textSizeLabel.setStyle("-fx-text-fill: " + theme.colorToCss(theme.getPrimaryTextColor()) + ";");
        textSizeLabel.setPrefWidth(150);
        
        Slider textSizeSlider = new Slider(0.8, 1.5, settings.getTextSizeMultiplier());
        textSizeSlider.setShowTickLabels(true);
        textSizeSlider.setShowTickMarks(true);
        textSizeSlider.setMajorTickUnit(0.1);
        textSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            settings.setTextSizeMultiplier(newVal.doubleValue());
            textSizeLabel.setText("Kích thước chữ: " + String.format("%.1fx", newVal.doubleValue()));
        });
        HBox.setHgrow(textSizeSlider, Priority.ALWAYS);
        textSizeRow.getChildren().addAll(textSizeLabel, textSizeSlider);
        
        // High Contrast
        CheckBox highContrastCheck = createCheckBox("Chế độ tương phản cao", 
            settings.isHighContrastMode(), theme, settings::setHighContrastMode);
        
        // Reduce Motion
        CheckBox reduceMotionCheck = createCheckBox("Giảm animation", 
            settings.isReduceMotion(), theme, settings::setReduceMotion);
        
        // Colorblind Mode
        HBox colorblindRow = new HBox(UIConstants.SPACING_SMALL);
        colorblindRow.setAlignment(Pos.CENTER_LEFT);
        Label colorblindLabel = new Label("Chế độ mù màu:");
        colorblindLabel.setStyle("-fx-text-fill: " + theme.colorToCss(theme.getPrimaryTextColor()) + ";");
        colorblindLabel.setPrefWidth(150);
        
        ComboBox<GameSettings.ColorblindMode> colorblindCombo = new ComboBox<>();
        colorblindCombo.getItems().addAll(GameSettings.ColorblindMode.values());
        colorblindCombo.setValue(settings.getColorblindMode());
        colorblindCombo.setStyle("-fx-background-color: " + theme.colorToCss(theme.getButtonColor()) + 
                                "; -fx-text-fill: " + theme.colorToCss(theme.getButtonTextColor()) + ";");
        colorblindCombo.setOnAction(e -> {
            settings.setColorblindMode(colorblindCombo.getValue());
        });
        colorblindRow.getChildren().addAll(colorblindLabel, colorblindCombo);
        
        section.getChildren().addAll(textSizeRow, highContrastCheck, reduceMotionCheck, colorblindRow);
        return section;
    }
    
    private VBox createAudioSection(Theme theme) {
        VBox section = new VBox(UIConstants.SPACING_SMALL);
        
        // Master Volume
        HBox masterVolRow = createVolumeRow("Âm lượng tổng:", settings.getMasterVolume(), theme, 
            volume -> settings.setMasterVolume(volume));
        
        // Sound Effects Volume
        HBox soundVolRow = createVolumeRow("Âm thanh hiệu ứng:", settings.getSoundEffectsVolume(), theme, 
            volume -> settings.setSoundEffectsVolume(volume));
        
        // Music Volume
        HBox musicVolRow = createVolumeRow("Âm nhạc:", settings.getMusicVolume(), theme, 
            volume -> settings.setMusicVolume(volume));
        
        section.getChildren().addAll(masterVolRow, soundVolRow, musicVolRow);
        return section;
    }
    
    private HBox createVolumeRow(String label, double currentVolume, Theme theme, 
                                 java.util.function.Consumer<Double> onVolumeChange) {
        HBox row = new HBox(UIConstants.SPACING_SMALL);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label volLabel = new Label(label + " " + (int)(currentVolume * 100) + "%");
        volLabel.setStyle("-fx-text-fill: " + theme.colorToCss(theme.getPrimaryTextColor()) + ";");
        volLabel.setPrefWidth(150);
        
        Slider volumeSlider = new Slider(0.0, 1.0, currentVolume);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(0.25);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            onVolumeChange.accept(newVal.doubleValue());
            volLabel.setText(label + " " + (int)(newVal.doubleValue() * 100) + "%");
        });
        HBox.setHgrow(volumeSlider, Priority.ALWAYS);
        row.getChildren().addAll(volLabel, volumeSlider);
        
        return row;
    }
    
    private CheckBox createCheckBox(String text, boolean selected, Theme theme, 
                                    java.util.function.Consumer<Boolean> onToggle) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(selected);
        checkBox.setStyle("-fx-text-fill: " + theme.colorToCss(theme.getPrimaryTextColor()) + ";");
        checkBox.setOnAction(e -> onToggle.accept(checkBox.isSelected()));
        return checkBox;
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

