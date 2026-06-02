package ui;

import java.util.Locale;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Shared visual design system for all JavaFX screens.
 *
 * Centralises the look-and-feel (gradients, rounded corners, drop shadows,
 * hover/press feedback) so every screen is theme-aware and consistent instead
 * of using hard-coded colours. All helpers derive their colours from the
 * active {@link Theme}, so switching theme restyles the whole UI.
 */
public final class UIStyle {
    private UIStyle() {
    }

    // ---- Colour helpers -------------------------------------------------

    /** Format an opaque colour as a CSS hex string (#RRGGBB). */
    public static String hex(Color c) {
        return String.format("#%02X%02X%02X",
                clamp255(c.getRed()), clamp255(c.getGreen()), clamp255(c.getBlue()));
    }

    /**
     * Format a colour as a CSS rgba() string with an explicit alpha.
     * Locale.ROOT forces a '.' decimal separator -- on a comma-decimal locale
     * (e.g. vi-VN) the default would emit "0,500" and break the CSS.
     */
    public static String rgba(Color c, double alpha) {
        return String.format(Locale.ROOT, "rgba(%d,%d,%d,%.3f)",
                clamp255(c.getRed()), clamp255(c.getGreen()), clamp255(c.getBlue()),
                Math.max(0, Math.min(1, alpha)));
    }

    private static int clamp255(double v) {
        return (int) Math.round(Math.max(0, Math.min(1, v)) * 255);
    }

    public static Color lighten(Color c, double amount) {
        return c.interpolate(Color.WHITE, amount);
    }

    public static Color darken(Color c, double amount) {
        return c.interpolate(Color.BLACK, amount);
    }

    // ---- Screen backgrounds & surfaces ---------------------------------

    /** Apply a soft vertical gradient background to a screen root. */
    public static void applyScreenBackground(Region root, Theme theme) {
        Color bg = theme.getBackgroundColor();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, "
                + hex(lighten(bg, 0.06)) + ", " + hex(darken(bg, 0.18)) + ");");
    }

    /** CSS for a raised "card" surface (rounded, translucent, soft shadow). */
    public static String cardCss(Theme theme) {
        return "-fx-background-color: " + rgba(theme.getBoardBackgroundColor(), 0.88) + ";"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: " + rgba(theme.getGridColor(), 0.85) + ";"
                + "-fx-border-radius: 16; -fx-border-width: 1.5;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.40), 18, 0.10, 0, 6);";
    }

    // ---- Titles & text --------------------------------------------------

    /** A bold title label with an accent-coloured glow. */
    public static Label title(String text, Theme theme, double size) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, size));
        Color accent = theme.getAccentTextColor();
        l.setStyle("-fx-text-fill: " + hex(accent) + ";"
                + "-fx-effect: dropshadow(gaussian, " + rgba(accent, 0.55) + ", 14, 0.20, 0, 0);");
        l.setMaxWidth(Double.MAX_VALUE);
        l.setAlignment(javafx.geometry.Pos.CENTER);
        return l;
    }

    public static Label bodyLabel(String text, Theme theme, double size) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", size));
        l.setStyle("-fx-text-fill: " + hex(theme.getPrimaryTextColor()) + ";");
        return l;
    }

    // ---- Buttons --------------------------------------------------------

    public static Button primaryButton(String text, Theme theme) {
        return primaryButton(text, theme, 18);
    }

    public static Button primaryButton(String text, Theme theme, double fontSize) {
        Button b = new Button(text);
        decorateButton(b, theme, theme.getButtonColor(), theme.getButtonHoverColor(), fontSize);
        return b;
    }

    /** Muted button for secondary actions (e.g. "Back"). */
    public static Button secondaryButton(String text, Theme theme) {
        return secondaryButton(text, theme, 15);
    }

    public static Button secondaryButton(String text, Theme theme, double fontSize) {
        Button b = new Button(text);
        Color base = theme.getGridColor().interpolate(theme.getBackgroundColor(), 0.35);
        decorateButton(b, theme, base, lighten(base, 0.18), fontSize);
        return b;
    }

    private static void decorateButton(Button b, Theme theme, Color base, Color hover, double fontSize) {
        String textHex = hex(theme.getButtonTextColor());
        b.setStyle(buttonCss(base, textHex, fontSize));
        b.setOnMouseEntered(e -> {
            b.setStyle(buttonCss(hover, textHex, fontSize));
            b.setScaleX(1.04);
            b.setScaleY(1.04);
        });
        b.setOnMouseExited(e -> {
            b.setStyle(buttonCss(base, textHex, fontSize));
            b.setScaleX(1.0);
            b.setScaleY(1.0);
        });
        b.setOnMousePressed(e -> {
            b.setScaleX(0.97);
            b.setScaleY(0.97);
        });
        b.setOnMouseReleased(e -> {
            b.setScaleX(1.04);
            b.setScaleY(1.04);
        });
    }

    private static String buttonCss(Color base, String textHex, double fontSize) {
        return "-fx-background-color: linear-gradient(to bottom, "
                + hex(lighten(base, 0.18)) + ", " + hex(darken(base, 0.20)) + ");"
                + "-fx-text-fill: " + textHex + ";"
                + "-fx-font-family: 'Arial'; -fx-font-weight: bold;"
                + "-fx-font-size: " + (int) fontSize + "px;"
                + "-fx-padding: 12 26 12 26;"
                + "-fx-background-radius: 12; -fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 10, 0.15, 0, 4);";
    }
}
