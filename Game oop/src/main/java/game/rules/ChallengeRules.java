package game.rules;

import game.GameController;
import game.GameBoard;

public class ChallengeRules implements GameRuleStrategy {
    private int challengeTargetScore = 5000;
    private int currentFallSpeed;

    @Override
    public void onGameStart(GameController controller) {
        // Challenge bắt đầu với tốc độ cao hơn
        this.currentFallSpeed = 800;
        ui.GameSettings settings = ui.GameSettings.getInstance();
        controller.setCurrentFallSpeed((int) (currentFallSpeed / settings.getFallSpeedMultiplier()));
    }

    @Override
    public void update(GameController controller, long deltaTime) {
        // Challenge mode: tăng độ khó mỗi khi đạt mốc điểm hoặc xóa hàng
        int score = controller.getScore();
        if (score > challengeTargetScore) {
            currentFallSpeed = Math.max(100, currentFallSpeed - 50);
            controller.setCurrentFallSpeed(currentFallSpeed);
            challengeTargetScore += 5000; // Tăng target
        }
    }

    @Override
    public boolean isGameOver(GameBoard board, int piecesPlaced) {
        // Có thể thêm điều kiện giới hạn số quân cờ (option)
        return false;
    }

    @Override
    public boolean isVictory(GameBoard board) {
        return false; // Chơi đến khi thua
    }

    @Override
    public String getModeInfo(GameBoard board, int currentSpeed, int piecesPlaced) {
        return "CHALLENGE - Target: " + challengeTargetScore;
    }
}
