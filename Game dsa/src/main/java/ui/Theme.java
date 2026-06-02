package ui;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

public class Theme {
    public enum ThemeType {
        DARK, LIGHT, NEON, PASTEL, RETRO
    }

    private static final Map<ThemeType, Palette> PALETTES = createPalettes();

    private ThemeType type;
    private String name;

    private Color backgroundColor;
    private Color boardBackgroundColor;
    private Color gridColor;

    private Color primaryTextColor;
    private Color secondaryTextColor;
    private Color accentTextColor;

    private Color buttonColor;
    private Color buttonHoverColor;
    private Color buttonTextColor;

    private Color[] pieceColors;

    private Color ghostPieceColor;
    private Color comboGlowColor;

    private static class Palette {
        private final String name;
        private final Color backgroundColor;
        private final Color boardBackgroundColor;
        private final Color gridColor;
        private final Color primaryTextColor;
        private final Color secondaryTextColor;
        private final Color accentTextColor;
        private final Color buttonColor;
        private final Color buttonHoverColor;
        private final Color buttonTextColor;
        private final Color[] pieceColors;
        private final Color ghostPieceColor;
        private final Color comboGlowColor;

        private Palette(String name, Color backgroundColor, Color boardBackgroundColor,
                        Color gridColor, Color primaryTextColor, Color secondaryTextColor,
                        Color accentTextColor, Color buttonColor, Color buttonHoverColor,
                        Color buttonTextColor, Color[] pieceColors, Color ghostPieceColor,
                        Color comboGlowColor) {
            this.name = name;
            this.backgroundColor = backgroundColor;
            this.boardBackgroundColor = boardBackgroundColor;
            this.gridColor = gridColor;
            this.primaryTextColor = primaryTextColor;
            this.secondaryTextColor = secondaryTextColor;
            this.accentTextColor = accentTextColor;
            this.buttonColor = buttonColor;
            this.buttonHoverColor = buttonHoverColor;
            this.buttonTextColor = buttonTextColor;
            this.pieceColors = pieceColors;
            this.ghostPieceColor = ghostPieceColor;
            this.comboGlowColor = comboGlowColor;
        }
    }

    private Theme(ThemeType type, Palette palette) {
        this.type = type;
        this.name = palette.name;
        this.backgroundColor = palette.backgroundColor;
        this.boardBackgroundColor = palette.boardBackgroundColor;
        this.gridColor = palette.gridColor;
        this.primaryTextColor = palette.primaryTextColor;
        this.secondaryTextColor = palette.secondaryTextColor;
        this.accentTextColor = palette.accentTextColor;
        this.buttonColor = palette.buttonColor;
        this.buttonHoverColor = palette.buttonHoverColor;
        this.buttonTextColor = palette.buttonTextColor;
        this.pieceColors = palette.pieceColors.clone();
        this.ghostPieceColor = palette.ghostPieceColor;
        this.comboGlowColor = palette.comboGlowColor;
    }

    private static Map<ThemeType, Palette> createPalettes() {
        HashMap<ThemeType, Palette> palettes = new HashMap<>();

        palettes.put(ThemeType.DARK, new Palette(
                "Dark",
                Color.rgb(30, 30, 30),
                Color.rgb(20, 20, 20),
                Color.rgb(50, 50, 50),
                Color.rgb(255, 255, 255),
                Color.rgb(200, 200, 200),
                Color.rgb(0, 255, 0),
                Color.rgb(0, 170, 0),
                Color.rgb(0, 220, 0),
                Color.WHITE,
                new Color[]{
                    Color.rgb(0, 255, 255),
                    Color.rgb(255, 255, 0),
                    Color.rgb(128, 0, 128),
                    Color.rgb(255, 165, 0),
                    Color.rgb(0, 0, 255),
                    Color.rgb(0, 255, 0),
                    Color.rgb(255, 0, 0)
                },
                Color.rgb(100, 100, 100, 0.3),
                Color.rgb(255, 200, 0)));

        palettes.put(ThemeType.LIGHT, new Palette(
                "Light",
                Color.rgb(245, 245, 245),
                Color.rgb(255, 255, 255),
                Color.rgb(220, 220, 220),
                Color.rgb(30, 30, 30),
                Color.rgb(80, 80, 80),
                Color.rgb(0, 150, 0),
                Color.rgb(0, 120, 0),
                Color.rgb(0, 170, 0),
                Color.WHITE,
                new Color[]{
                    Color.rgb(0, 150, 200),
                    Color.rgb(200, 180, 0),
                    Color.rgb(150, 0, 150),
                    Color.rgb(200, 120, 0),
                    Color.rgb(0, 80, 200),
                    Color.rgb(0, 150, 0),
                    Color.rgb(200, 0, 0)
                },
                Color.rgb(150, 150, 150, 0.4),
                Color.rgb(255, 150, 0)));

        palettes.put(ThemeType.NEON, new Palette(
                "Neon",
                Color.rgb(10, 10, 30),
                Color.rgb(5, 5, 20),
                Color.rgb(30, 30, 60),
                Color.rgb(0, 255, 255),
                Color.rgb(255, 0, 255),
                Color.rgb(0, 255, 0),
                Color.rgb(255, 0, 255),
                Color.rgb(255, 100, 255),
                Color.BLACK,
                new Color[]{
                    Color.rgb(0, 255, 255),
                    Color.rgb(255, 255, 0),
                    Color.rgb(255, 0, 255),
                    Color.rgb(255, 100, 0),
                    Color.rgb(0, 200, 255),
                    Color.rgb(0, 255, 100),
                    Color.rgb(255, 0, 100)
                },
                Color.rgb(100, 100, 255, 0.3),
                Color.rgb(255, 0, 255)));

        palettes.put(ThemeType.PASTEL, new Palette(
                "Pastel",
                Color.rgb(255, 240, 245),
                Color.rgb(255, 250, 252),
                Color.rgb(230, 220, 225),
                Color.rgb(100, 80, 90),
                Color.rgb(150, 130, 140),
                Color.rgb(200, 150, 180),
                Color.rgb(255, 180, 200),
                Color.rgb(255, 200, 220),
                Color.rgb(100, 50, 70),
                new Color[]{
                    Color.rgb(180, 220, 255),
                    Color.rgb(255, 240, 180),
                    Color.rgb(220, 180, 255),
                    Color.rgb(255, 200, 180),
                    Color.rgb(180, 200, 255),
                    Color.rgb(180, 255, 200),
                    Color.rgb(255, 180, 200)
                },
                Color.rgb(200, 200, 200, 0.5),
                Color.rgb(255, 200, 100)));

        palettes.put(ThemeType.RETRO, new Palette(
                "Retro",
                Color.rgb(139, 69, 19),
                Color.rgb(160, 82, 45),
                Color.rgb(101, 67, 33),
                Color.rgb(255, 215, 0),
                Color.rgb(255, 255, 224),
                Color.rgb(255, 140, 0),
                Color.rgb(139, 0, 0),
                Color.rgb(178, 34, 34),
                Color.rgb(255, 215, 0),
                new Color[]{
                    Color.rgb(0, 191, 255),
                    Color.rgb(255, 215, 0),
                    Color.rgb(138, 43, 226),
                    Color.rgb(255, 140, 0),
                    Color.rgb(0, 0, 205),
                    Color.rgb(50, 205, 50),
                    Color.rgb(220, 20, 60)
                },
                Color.rgb(105, 105, 105, 0.4),
                Color.rgb(255, 215, 0)));

        return palettes;
    }

    public static Theme getDarkTheme() {
        return getTheme(ThemeType.DARK);
    }

    public static Theme getLightTheme() {
        return getTheme(ThemeType.LIGHT);
    }

    public static Theme getNeonTheme() {
        return getTheme(ThemeType.NEON);
    }

    public static Theme getPastelTheme() {
        return getTheme(ThemeType.PASTEL);
    }

    public static Theme getRetroTheme() {
        return getTheme(ThemeType.RETRO);
    }

    public static Theme getTheme(ThemeType type) {
        ThemeType selectedType = PALETTES.containsKey(type) ? type : ThemeType.DARK;
        return new Theme(selectedType, PALETTES.get(selectedType));
    }

    public ThemeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getBoardBackgroundColor() {
        return boardBackgroundColor;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public Color getPrimaryTextColor() {
        return primaryTextColor;
    }

    public Color getSecondaryTextColor() {
        return secondaryTextColor;
    }

    public Color getAccentTextColor() {
        return accentTextColor;
    }

    public Color getButtonColor() {
        return buttonColor;
    }

    public Color getButtonHoverColor() {
        return buttonHoverColor;
    }

    public Color getButtonTextColor() {
        return buttonTextColor;
    }

    public Color[] getPieceColors() {
        return pieceColors;
    }

    public Color getGhostPieceColor() {
        return ghostPieceColor;
    }

    public Color getComboGlowColor() {
        return comboGlowColor;
    }

    public Color getPieceColor(game.PieceType pieceType) {
        int index = pieceType.ordinal();
        if (index >= 0 && index < pieceColors.length) {
            return pieceColors[index];
        }
        return Color.WHITE;
    }

    public Color getPieceColor(game.PieceType pieceType, GameSettings.ColorblindMode colorblindMode) {
        int index = pieceType.ordinal();
        Color baseColor = (index >= 0 && index < pieceColors.length) ? pieceColors[index] : Color.WHITE;

        if (colorblindMode != GameSettings.ColorblindMode.NONE) {
            return applyColorblindFilter(baseColor, colorblindMode);
        }

        return baseColor;
    }

    private Color applyColorblindFilter(Color color, GameSettings.ColorblindMode mode) {
        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();

        switch (mode) {
            case PROTANOPIA:
                return Color.color(
                    r * 0.567 + g * 0.433,
                    r * 0.558 + g * 0.442,
                    r * 0.0 + g * 0.242 + b * 0.758
                );
            case DEUTERANOPIA:
                return Color.color(
                    r * 0.625 + g * 0.375,
                    r * 0.7 + g * 0.3,
                    r * 0.0 + g * 0.3 + b * 0.7
                );
            case TRITANOPIA:
                return Color.color(
                    r * 0.95 + g * 0.05,
                    r * 0.433 + g * 0.567,
                    r * 0.475 + g * 0.525
                );
            default:
                return color;
        }
    }

    public String colorToCss(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}
