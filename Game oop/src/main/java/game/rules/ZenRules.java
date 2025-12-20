package game.rules;

import game.GameController;
import game.GameBoard;

public class ZenRules implements GameRuleStrategy {
    @Override
    public void onGameStart(GameController controller) {
        // Zen mode: không có gravity (hoặc rất chậm), người chơi tự drop
        controller.setCurrentFallSpeed(Integer.MAX_VALUE); // Gần như không rơi
    }

    @Override
    public void update(GameController controller, long deltaTime) {
        // Zen không tăng tốc độ
    }

    @Override
    public boolean isGameOver(GameBoard board, int piecesPlaced) {
        return false;
    }

    @Override
    public boolean isVictory(GameBoard board) {
        return false;
    }

    @Override
    public String getModeInfo(GameBoard board, int currentSpeed, int piecesPlaced) {
        return "ZEN - Thư giãn";
    }
}
