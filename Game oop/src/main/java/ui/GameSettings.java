package ui;

import javafx.scene.input.KeyCode;
import java.util.prefs.Preferences;

/**
 * Quản lý tất cả settings của game
 */
public class GameSettings {
    private static GameSettings instance;
    private Preferences preferences;

    // Key bindings
    private KeyCode moveLeftKey;
    private KeyCode moveRightKey;
    private KeyCode moveDownKey;
    private KeyCode rotateKey;
    private KeyCode hardDropKey;

    // Gameplay settings
    private double fallSpeedMultiplier; // 0.5 - 2.0
    private boolean assistMode; // Auto-rotate
    private boolean practiceMode; // Không tính điểm

    // Visual settings
    private double textSizeMultiplier; // 0.8 - 1.5
    private boolean highContrastMode;
    private boolean reduceMotion;
    private ColorblindMode colorblindMode;

    // Audio settings
    private double masterVolume; // 0.0 - 1.0
    private double soundEffectsVolume;
    private double musicVolume;

    public enum ColorblindMode {
        NONE, PROTANOPIA, DEUTERANOPIA, TRITANOPIA
    }

    private GameSettings() {
        preferences = Preferences.userNodeForPackage(GameSettings.class);
        loadSettings();
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    private void loadSettings() {
        // Key bindings (default)
        try {
            moveLeftKey = KeyCode.valueOf(preferences.get("moveLeftKey", "LEFT"));
        } catch (IllegalArgumentException e) {
            moveLeftKey = KeyCode.LEFT;
        }
        try {
            moveRightKey = KeyCode.valueOf(preferences.get("moveRightKey", "RIGHT"));
        } catch (IllegalArgumentException e) {
            moveRightKey = KeyCode.RIGHT;
        }
        try {
            moveDownKey = KeyCode.valueOf(preferences.get("moveDownKey", "DOWN"));
        } catch (IllegalArgumentException e) {
            moveDownKey = KeyCode.DOWN;
        }
        try {
            rotateKey = KeyCode.valueOf(preferences.get("rotateKey", "SPACE"));
        } catch (IllegalArgumentException e) {
            rotateKey = KeyCode.SPACE;
        }
        try {
            hardDropKey = KeyCode.valueOf(preferences.get("hardDropKey", "DOWN"));
        } catch (IllegalArgumentException e) {
            hardDropKey = KeyCode.DOWN;
        }

        // Gameplay
        fallSpeedMultiplier = preferences.getDouble("fallSpeedMultiplier", 1.0);
        assistMode = preferences.getBoolean("assistMode", false);
        practiceMode = preferences.getBoolean("practiceMode", false);

        // Visual
        textSizeMultiplier = preferences.getDouble("textSizeMultiplier", 1.0);
        highContrastMode = preferences.getBoolean("highContrastMode", false);
        reduceMotion = preferences.getBoolean("reduceMotion", false);
        String colorblindStr = preferences.get("colorblindMode", "NONE");
        try {
            colorblindMode = ColorblindMode.valueOf(colorblindStr);
        } catch (IllegalArgumentException e) {
            colorblindMode = ColorblindMode.NONE;
        }

        // Audio
        masterVolume = preferences.getDouble("masterVolume", 1.0);
        soundEffectsVolume = preferences.getDouble("soundEffectsVolume", 1.0);
        musicVolume = preferences.getDouble("musicVolume", 1.0);
    }

    private void saveSettings() {
        // Key bindings
        preferences.put("moveLeftKey", moveLeftKey.name());
        preferences.put("moveRightKey", moveRightKey.name());
        preferences.put("moveDownKey", moveDownKey.name());
        preferences.put("rotateKey", rotateKey.name());
        preferences.put("hardDropKey", hardDropKey.name());

        // Gameplay
        preferences.putDouble("fallSpeedMultiplier", fallSpeedMultiplier);
        preferences.putBoolean("assistMode", assistMode);
        preferences.putBoolean("practiceMode", practiceMode);

        // Visual
        preferences.putDouble("textSizeMultiplier", textSizeMultiplier);
        preferences.putBoolean("highContrastMode", highContrastMode);
        preferences.putBoolean("reduceMotion", reduceMotion);
        preferences.put("colorblindMode", colorblindMode.name());

        // Audio
        preferences.putDouble("masterVolume", masterVolume);
        preferences.putDouble("soundEffectsVolume", soundEffectsVolume);
        preferences.putDouble("musicVolume", musicVolume);
    }

    // Key bindings getters/setters
    public KeyCode getMoveLeftKey() {
        return moveLeftKey;
    }

    public void setMoveLeftKey(KeyCode key) {
        this.moveLeftKey = key;
        saveSettings();
    }

    public KeyCode getMoveRightKey() {
        return moveRightKey;
    }

    public void setMoveRightKey(KeyCode key) {
        this.moveRightKey = key;
        saveSettings();
    }

    public KeyCode getMoveDownKey() {
        return moveDownKey;
    }

    public void setMoveDownKey(KeyCode key) {
        this.moveDownKey = key;
        saveSettings();
    }

    public KeyCode getRotateKey() {
        return rotateKey;
    }

    public void setRotateKey(KeyCode key) {
        this.rotateKey = key;
        saveSettings();
    }

    public KeyCode getHardDropKey() {
        return hardDropKey;
    }

    public void setHardDropKey(KeyCode key) {
        this.hardDropKey = key;
        saveSettings();
    }

    // Gameplay getters/setters
    public double getFallSpeedMultiplier() {
        return fallSpeedMultiplier;
    }

    public void setFallSpeedMultiplier(double multiplier) {
        this.fallSpeedMultiplier = Math.max(0.5, Math.min(2.0, multiplier));
        saveSettings();
    }

    public boolean isAssistMode() {
        return assistMode;
    }

    public void setAssistMode(boolean enabled) {
        this.assistMode = enabled;
        saveSettings();
    }

    public boolean isPracticeMode() {
        return practiceMode;
    }

    public void setPracticeMode(boolean enabled) {
        this.practiceMode = enabled;
        saveSettings();
    }

    // Visual getters/setters
    public double getTextSizeMultiplier() {
        return textSizeMultiplier;
    }

    public void setTextSizeMultiplier(double multiplier) {
        this.textSizeMultiplier = Math.max(0.8, Math.min(1.5, multiplier));
        saveSettings();
    }

    public boolean isHighContrastMode() {
        return highContrastMode;
    }

    public void setHighContrastMode(boolean enabled) {
        this.highContrastMode = enabled;
        saveSettings();
    }

    public boolean isReduceMotion() {
        return reduceMotion;
    }

    public void setReduceMotion(boolean enabled) {
        this.reduceMotion = enabled;
        saveSettings();
    }

    public ColorblindMode getColorblindMode() {
        return colorblindMode;
    }

    public void setColorblindMode(ColorblindMode mode) {
        this.colorblindMode = mode;
        saveSettings();
    }

    // Audio getters/setters
    public double getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(double volume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, volume));
        saveSettings();
    }

    public double getSoundEffectsVolume() {
        return soundEffectsVolume;
    }

    public void setSoundEffectsVolume(double volume) {
        this.soundEffectsVolume = Math.max(0.0, Math.min(1.0, volume));
        saveSettings();
    }

    public double getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        saveSettings();
    }
}
