package utils;

import java.io.*;
import java.util.*;

public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "src/resources/leaderboard.txt";
    private static final int MAX_SCORES = 10;
    private List<GameScore> scores;

    public LeaderboardManager() {
        scores = new ArrayList<>();
        loadScores();
    }

    public void addScore(GameScore score) {
        scores.add(score);
        scores.sort(null); // Sắp xếp
        if (scores.size() > MAX_SCORES) {
            scores.remove(scores.size() - 1);
        }
        saveScores();
    }

    public List<GameScore> getTopScores() {
        if (scores.isEmpty()) {
            return new ArrayList<>();
        }
        int endIndex = Math.min(MAX_SCORES, scores.size());
        return new ArrayList<>(scores.subList(0, endIndex));
    }

    private void saveScores() {
        try {
            File file = new File(LEADERBOARD_FILE);
            // Tạo thư mục nếu chưa tồn tại
            file.getParentFile().mkdirs();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (GameScore score : scores) {
                    writer.println(score.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadScores() {
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                GameScore score = GameScore.fromString(line);
                if (score != null) {
                    scores.add(score);
                }
            }
            scores.sort(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
