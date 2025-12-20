package utils;

public class GameScore implements Comparable<GameScore> {
    private String playerName;
    private int score;
    private String difficulty;
    private long timestamp;

    public GameScore(String playerName, int score, String difficulty) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public int compareTo(GameScore other) {
        return Integer.compare(other.score, this.score); // Sắp xếp giảm dần
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return playerName + "|" + score + "|" + difficulty + "|" + timestamp;
    }

    public static GameScore fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length == 4) {
            GameScore score = new GameScore(parts[0], Integer.parseInt(parts[1]), parts[2]);
            return score;
        }
        return null;
    }
}
