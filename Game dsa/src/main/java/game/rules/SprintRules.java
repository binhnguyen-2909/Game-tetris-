package game.rules;

import game.GameController;
import game.GameBoard;

public class SprintRules implements GameRuleStrategy {
    private static final int SPRINT_TARGET_ROWS = 40;

    @Override
    public void onGameStart(GameController controller) {
        controller.getBoard().resetRowsCleared();
        // Set fixed speed based on difficulty settings
        int baseSpeed = controller.getDifficulty().getFallSpeed();
        ui.GameSettings settings = ui.GameSettings.getInstance();
        controller.setCurrentFallSpeed((int) (baseSpeed / settings.getFallSpeedMultiplier()));
    }

    @Override
    public void update(GameController controller, long deltaTime) {
        // Sprint mode không tăng tốc độ theo thời gian
        // Logic rơi tự động do controller xử lý
    }

    @Override
    public boolean isGameOver(GameBoard board, int piecesPlaced) {
        // Game over nếu đã xóa đủ 40 dòng (chiến thắng)
        return board.getTotalRowsCleared() >= SPRINT_TARGET_ROWS;
    }

    @Override
    public boolean isVictory(GameBoard board) {
        return board.getTotalRowsCleared() >= SPRINT_TARGET_ROWS;
    }

    @Override
    public String getModeInfo(GameBoard board, int currentSpeed, int piecesPlaced) {
        return "SPRINT - " + board.getTotalRowsCleared() + "/" + SPRINT_TARGET_ROWS + " dòng";
    }
}
