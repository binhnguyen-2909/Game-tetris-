package ui;

/**
 * Constants cho kích thước và styling thống nhất của UI
 */
public class UIConstants {
    // Kích thước cửa sổ chuẩn (hình chữ nhật đứng)
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 600;
    
    // Tỷ lệ kích thước dựa trên window size
    // Font sizes (tỷ lệ với height)
    public static final int FONT_TITLE = WINDOW_HEIGHT / 12;        // 50
    public static final int FONT_SUBTITLE = WINDOW_HEIGHT / 15;     // 40
    public static final int FONT_LARGE = WINDOW_HEIGHT / 20;        // 30
    public static final int FONT_MEDIUM = WINDOW_HEIGHT / 30;       // 20
    public static final int FONT_SMALL = WINDOW_HEIGHT / 37;        // 16
    public static final int FONT_TINY = WINDOW_HEIGHT / 42;         // 14
    
    // Button sizes (tỷ lệ với width)
    public static final int BUTTON_WIDTH = WINDOW_WIDTH / 4;        // 225
    public static final int BUTTON_WIDTH_SMALL = (int)(WINDOW_WIDTH / 4.5); // 200
    public static final int BUTTON_HEIGHT = WINDOW_HEIGHT / 15;     // 40
    public static final int BUTTON_PADDING = WINDOW_HEIGHT / 40;    // 15
    public static final int BUTTON_PADDING_SMALL = WINDOW_HEIGHT / 60; // 10
    
    // Spacing và padding (tỷ lệ với width/height nhỏ nhất)
    public static final int SPACING_LARGE = WINDOW_HEIGHT / 30;     // 20
    public static final int SPACING_MEDIUM = WINDOW_HEIGHT / 40;    // 15
    public static final int SPACING_SMALL = WINDOW_HEIGHT / 60;     // 10
    public static final int PADDING = WINDOW_HEIGHT / 30;           // 20
    
    // TextField width
    public static final int TEXT_FIELD_WIDTH = WINDOW_WIDTH / 3;    // 300
    public static final int TEXT_FIELD_PADDING = WINDOW_HEIGHT / 60; // 10
}

