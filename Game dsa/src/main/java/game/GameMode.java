package game;

/**
 * Enum các chế độ chơi
 */
public enum GameMode {
    MARATHON("Marathon", "Chơi không giới hạn, tốc độ tăng dần"),
    SPRINT("Sprint", "Xóa 40 dòng trong thời gian ngắn nhất"),
    CHALLENGE("Challenge", "Các thử thách đặc biệt"),
    ZEN("Zen", "Thư giãn - không tự động rơi");

    private final String name;
    private final String description;

    GameMode(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}

