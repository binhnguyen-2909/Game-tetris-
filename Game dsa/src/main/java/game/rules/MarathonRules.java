package game.rules;

import game.GameController;
import game.GameBoard;

public class MarathonRules implements GameRuleStrategy {
    private int currentFallSpeed;
    private int baseSpeed;

    @Override
    public void onGameStart(GameController controller) {
        // Marathon bắt đầu với tốc độ của difficulty
        this.baseSpeed = controller.getDifficulty().getFallSpeed();
        this.currentFallSpeed = baseSpeed;
    }

    @Override
    public void update(GameController controller, long deltaTime) {
        // Marathon mode logic: tăng tốc độ theo thời gian
        long elapsedTime = controller.getElapsedTime();
        int speedLevel = (int) (elapsedTime / 30000);
        int newBaseSpeed = Math.max(100, controller.getDifficulty().getFallSpeed() - (speedLevel * 50));

        // Apply multipliers from settings
        ui.GameSettings settings = ui.GameSettings.getInstance();
        this.currentFallSpeed = (int) (newBaseSpeed / settings.getFallSpeedMultiplier());

        // Auto drop logic handled by controller based on speed
        controller.setCurrentFallSpeed(currentFallSpeed);
    }

    @Override
    public boolean isGameOver(GameBoard board, int piecesPlaced) {
        // Marathon chỉ end khi board đầy (đã được check ở controller chung, nhưng có
        // thể check thêm ở đây nếu cần)
        return false;
    }

    @Override
    public boolean isVictory(GameBoard board) {
        return false; // Marathon không có thắng, chỉ chơi đến khi thua
    }

    @Override
    public String getModeInfo(GameBoard board, int currentSpeed, int piecesPlaced) {
        return "MARATHON - Tốc độ: " + currentFallSpeed + "ms";
    }
}
