package ui;

import javafx.scene.paint.Color;

/**
 * Theme system với các màu sắc và styles khác nhau
 */
public class Theme {
    public enum ThemeType {
        DARK, LIGHT, NEON, PASTEL, RETRO
    }
    
    private ThemeType type;
    private String name;
    
    // Background colors
    private Color backgroundColor;
    private Color boardBackgroundColor;
    private Color gridColor;
    
    // Text colors
    private Color primaryTextColor;
    private Color secondaryTextColor;
    private Color accentTextColor;
    
    // Button colors
    private Color buttonColor;
    private Color buttonHoverColor;
    private Color buttonTextColor;
    
    // Piece colors
    private Color[] pieceColors;
    
    // Effect colors
    private Color ghostPieceColor;
    private Color comboGlowColor;
    
    // Constructor cho Dark theme (default)
    private Theme(ThemeType type, String name) {
        this.type = type;
        this.name = name;
        initializeColors();
    }
    
    private void initializeColors() {
        switch (type) {
            case DARK:
                backgroundColor = Color.rgb(30, 30, 30);
                boardBackgroundColor = Color.rgb(20, 20, 20);
                gridColor = Color.rgb(50, 50, 50);
                primaryTextColor = Color.rgb(255, 255, 255);
                secondaryTextColor = Color.rgb(200, 200, 200);
                accentTextColor = Color.rgb(0, 255, 0);
                buttonColor = Color.rgb(0, 170, 0);
                buttonHoverColor = Color.rgb(0, 220, 0);
                buttonTextColor = Color.WHITE;
                pieceColors = new Color[]{
                    Color.rgb(0, 255, 255),   // I - Cyan
                    Color.rgb(255, 255, 0),   // O - Yellow
                    Color.rgb(128, 0, 128),   // T - Purple
                    Color.rgb(255, 165, 0),   // L - Orange
                    Color.rgb(0, 0, 255),     // J - Blue
                    Color.rgb(0, 255, 0),     // S - Green
                    Color.rgb(255, 0, 0)      // Z - Red
                };
                ghostPieceColor = Color.rgb(100, 100, 100, 0.3);
                comboGlowColor = Color.rgb(255, 200, 0);
                break;
                
            case LIGHT:
                backgroundColor = Color.rgb(245, 245, 245);
                boardBackgroundColor = Color.rgb(255, 255, 255);
                gridColor = Color.rgb(220, 220, 220);
                primaryTextColor = Color.rgb(30, 30, 30);
                secondaryTextColor = Color.rgb(80, 80, 80);
                accentTextColor = Color.rgb(0, 150, 0);
                buttonColor = Color.rgb(0, 120, 0);
                buttonHoverColor = Color.rgb(0, 170, 0);
                buttonTextColor = Color.WHITE;
                pieceColors = new Color[]{
                    Color.rgb(0, 150, 200),   // I
                    Color.rgb(200, 180, 0),   // O
                    Color.rgb(150, 0, 150),   // T
                    Color.rgb(200, 120, 0),   // L
                    Color.rgb(0, 80, 200),    // J
                    Color.rgb(0, 150, 0),     // S
                    Color.rgb(200, 0, 0)      // Z
                };
                ghostPieceColor = Color.rgb(150, 150, 150, 0.4);
                comboGlowColor = Color.rgb(255, 150, 0);
                break;
                
            case NEON:
                backgroundColor = Color.rgb(10, 10, 30);
                boardBackgroundColor = Color.rgb(5, 5, 20);
                gridColor = Color.rgb(30, 30, 60);
                primaryTextColor = Color.rgb(0, 255, 255);
                secondaryTextColor = Color.rgb(255, 0, 255);
                accentTextColor = Color.rgb(0, 255, 0);
                buttonColor = Color.rgb(255, 0, 255);
                buttonHoverColor = Color.rgb(255, 100, 255);
                buttonTextColor = Color.BLACK;
                pieceColors = new Color[]{
                    Color.rgb(0, 255, 255),   // I - Cyan Neon
                    Color.rgb(255, 255, 0),   // O - Yellow Neon
                    Color.rgb(255, 0, 255),   // T - Magenta Neon
                    Color.rgb(255, 100, 0),   // L - Orange Neon
                    Color.rgb(0, 200, 255),   // J - Blue Neon
                    Color.rgb(0, 255, 100),   // S - Green Neon
                    Color.rgb(255, 0, 100)    // Z - Pink Neon
                };
                ghostPieceColor = Color.rgb(100, 100, 255, 0.3);
                comboGlowColor = Color.rgb(255, 0, 255);
                break;
                
            case PASTEL:
                backgroundColor = Color.rgb(255, 240, 245);
                boardBackgroundColor = Color.rgb(255, 250, 252);
                gridColor = Color.rgb(230, 220, 225);
                primaryTextColor = Color.rgb(100, 80, 90);
                secondaryTextColor = Color.rgb(150, 130, 140);
                accentTextColor = Color.rgb(200, 150, 180);
                buttonColor = Color.rgb(255, 180, 200);
                buttonHoverColor = Color.rgb(255, 200, 220);
                buttonTextColor = Color.rgb(100, 50, 70);
                pieceColors = new Color[]{
                    Color.rgb(180, 220, 255), // I - Light Blue
                    Color.rgb(255, 240, 180), // O - Light Yellow
                    Color.rgb(220, 180, 255), // T - Light Purple
                    Color.rgb(255, 200, 180), // L - Light Orange
                    Color.rgb(180, 200, 255), // J - Light Blue
                    Color.rgb(180, 255, 200), // S - Light Green
                    Color.rgb(255, 180, 200)  // Z - Light Pink
                };
                ghostPieceColor = Color.rgb(200, 200, 200, 0.5);
                comboGlowColor = Color.rgb(255, 200, 100);
                break;
                
            case RETRO:
                backgroundColor = Color.rgb(139, 69, 19);
                boardBackgroundColor = Color.rgb(160, 82, 45);
                gridColor = Color.rgb(101, 67, 33);
                primaryTextColor = Color.rgb(255, 215, 0);
                secondaryTextColor = Color.rgb(255, 255, 224);
                accentTextColor = Color.rgb(255, 140, 0);
                buttonColor = Color.rgb(139, 0, 0);
                buttonHoverColor = Color.rgb(178, 34, 34);
                buttonTextColor = Color.rgb(255, 215, 0);
                pieceColors = new Color[]{
                    Color.rgb(0, 191, 255),   // I - Sky Blue
                    Color.rgb(255, 215, 0),   // O - Gold
                    Color.rgb(138, 43, 226),  // T - Blue Violet
                    Color.rgb(255, 140, 0),   // L - Dark Orange
                    Color.rgb(0, 0, 205),     // J - Medium Blue
                    Color.rgb(50, 205, 50),   // S - Lime Green
                    Color.rgb(220, 20, 60)    // Z - Crimson
                };
                ghostPieceColor = Color.rgb(105, 105, 105, 0.4);
                comboGlowColor = Color.rgb(255, 215, 0);
                break;
        }
    }
    
    // Factory methods
    public static Theme getDarkTheme() {
        return new Theme(ThemeType.DARK, "Dark");
    }
    
    public static Theme getLightTheme() {
        return new Theme(ThemeType.LIGHT, "Light");
    }
    
    public static Theme getNeonTheme() {
        return new Theme(ThemeType.NEON, "Neon");
    }
    
    public static Theme getPastelTheme() {
        return new Theme(ThemeType.PASTEL, "Pastel");
    }
    
    public static Theme getRetroTheme() {
        return new Theme(ThemeType.RETRO, "Retro");
    }
    
    public static Theme getTheme(ThemeType type) {
        switch (type) {
            case LIGHT: return getLightTheme();
            case NEON: return getNeonTheme();
            case PASTEL: return getPastelTheme();
            case RETRO: return getRetroTheme();
            default: return getDarkTheme();
        }
    }
    
    // Getters
    public ThemeType getType() { return type; }
    public String getName() { return name; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getBoardBackgroundColor() { return boardBackgroundColor; }
    public Color getGridColor() { return gridColor; }
    public Color getPrimaryTextColor() { return primaryTextColor; }
    public Color getSecondaryTextColor() { return secondaryTextColor; }
    public Color getAccentTextColor() { return accentTextColor; }
    public Color getButtonColor() { return buttonColor; }
    public Color getButtonHoverColor() { return buttonHoverColor; }
    public Color getButtonTextColor() { return buttonTextColor; }
    public Color[] getPieceColors() { return pieceColors; }
    public Color getGhostPieceColor() { return ghostPieceColor; }
    public Color getComboGlowColor() { return comboGlowColor; }
    
    /**
     * Lấy màu cho piece type
     */
    public Color getPieceColor(game.PieceType pieceType) {
        int index = pieceType.ordinal();
        if (index >= 0 && index < pieceColors.length) {
            return pieceColors[index];
        }
        return Color.WHITE;
    }
    
    /**
     * Lấy màu cho piece type (với colorblind support)
     */
    public Color getPieceColor(game.PieceType pieceType, GameSettings.ColorblindMode colorblindMode) {
        int index = pieceType.ordinal();
        Color baseColor = (index >= 0 && index < pieceColors.length) ? pieceColors[index] : Color.WHITE;
        
        // Apply colorblind filter
        if (colorblindMode != GameSettings.ColorblindMode.NONE) {
            return applyColorblindFilter(baseColor, colorblindMode);
        }
        
        return baseColor;
    }
    
    /**
     * Apply colorblind filter (simplified simulation)
     */
    private Color applyColorblindFilter(Color color, GameSettings.ColorblindMode mode) {
        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();
        
        switch (mode) {
            case PROTANOPIA:
                // Red-green colorblind (protanopia)
                // Shift red towards green
                return Color.color(
                    r * 0.567 + g * 0.433,
                    r * 0.558 + g * 0.442,
                    r * 0.0 + g * 0.242 + b * 0.758
                );
            case DEUTERANOPIA:
                // Red-green colorblind (deuteranopia)
                return Color.color(
                    r * 0.625 + g * 0.375,
                    r * 0.7 + g * 0.3,
                    r * 0.0 + g * 0.3 + b * 0.7
                );
            case TRITANOPIA:
                // Blue-yellow colorblind
                return Color.color(
                    r * 0.95 + g * 0.05,
                    r * 0.433 + g * 0.567,
                    r * 0.475 + g * 0.525
                );
            default:
                return color;
        }
    }
    
    /**
     * Chuyển Color thành CSS string
     */
    public String colorToCss(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }
}

