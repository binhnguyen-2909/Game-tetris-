package game.rules;

import game.GameController;
import game.GameBoard;

public interface GameRuleStrategy {
    boolean isGameOver(GameBoard board, int piecesPlaced);

    boolean isVictory(GameBoard board);

    void update(GameController controller, long deltaTime);

    String getModeInfo(GameBoard board, int currentSpeed, int piecesPlaced);

    void onGameStart(GameController controller);
}
