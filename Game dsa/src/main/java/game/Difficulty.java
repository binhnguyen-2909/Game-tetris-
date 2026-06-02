package game;

public enum Difficulty {
    EASY(1500, 2),
    MEDIUM(1000, 1),
    HARD(500, 0);

    private final int fallSpeed; // ms
    private final int initialRows;

    Difficulty(int fallSpeed, int initialRows) {
        this.fallSpeed = fallSpeed;
        this.initialRows = initialRows;
    }

    public int getFallSpeed() {
        return fallSpeed;
    }

    public int getInitialRows() {
        return initialRows;
    }
}
