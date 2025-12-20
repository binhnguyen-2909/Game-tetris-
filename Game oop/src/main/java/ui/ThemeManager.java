package ui;

import java.io.*;
import java.util.prefs.Preferences;

/**
 * Quản lý theme hiện tại và lưu preference
 */
public class ThemeManager {
    private static ThemeManager instance;
    private Theme currentTheme;
    private Preferences preferences;
    
    private ThemeManager() {
        preferences = Preferences.userNodeForPackage(ThemeManager.class);
        loadTheme();
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    public void setTheme(Theme.ThemeType themeType) {
        currentTheme = Theme.getTheme(themeType);
        saveTheme();
    }
    
    private void loadTheme() {
        String themeName = preferences.get("theme", "DARK");
        try {
            Theme.ThemeType themeType = Enum.valueOf(Theme.ThemeType.class, themeName);
            currentTheme = Theme.getTheme(themeType);
        } catch (IllegalArgumentException e) {
            currentTheme = Theme.getDarkTheme();
        }
    }
    
    private void saveTheme() {
        preferences.put("theme", currentTheme.getType().name());
    }
}

