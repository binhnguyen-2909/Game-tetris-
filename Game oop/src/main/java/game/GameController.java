package game;

import game.rules.ChallengeRules;
import game.rules.GameRuleStrategy;
import game.rules.MarathonRules;
import game.rules.SprintRules;
import game.rules.ZenRules;
import utils.GameScore;
import utils.LeaderboardManager;

public class GameController {
    private GameBoard board;
    private Piece currentPiece;
    private Piece nextPiece;
    private Difficulty difficulty;
    private GameMode gameMode;
    private GameRuleStrategy ruleStrategy; // Strategy pattern
    private LeaderboardManager leaderboardManager;
    private boolean gameOver = false;
    private long lastFallTime = 0;
    private long startTime = 0;
    private int currentFallSpeed;
    private int piecesPlaced = 0;

    // Combo tracking
    private int consecutiveClears = 0;
    private int bestCombo = 0;

    // Speed bonus tracking
    private long pieceSpawnTime = 0;

    public GameController(Difficulty difficulty, GameMode gameMode) {
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.board = new GameBoard();
        this.board.initializeWithRows(difficulty.getInitialRows());
        this.leaderboardManager = new LeaderboardManager();
        this.currentPiece = new Piece();
        this.nextPiece = new Piece();
        this.lastFallTime = System.currentTimeMillis();
        this.startTime = System.currentTimeMillis();
        this.pieceSpawnTime = System.currentTimeMillis();

        // Initialize strategy based on mode
        switch (gameMode) {
            case MARATHON:
                this.ruleStrategy = new MarathonRules();
                break;
            case SPRINT:
                this.ruleStrategy = new SprintRules();
                break;
            case CHALLENGE:
                this.ruleStrategy = new ChallengeRules();
                break;
            case ZEN:
                this.ruleStrategy = new ZenRules();
                break;
            default:
                this.ruleStrategy = new MarathonRules();
                break;
        }

        // Initialize strategy defaults
        this.ruleStrategy.onGameStart(this);
    }

    public void update() {
        if (gameOver)
            return;

        // Delegate update logic to strategy
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        ruleStrategy.update(this, elapsedTime);

        // Handle auto-drop (gravity) logic here if strategy didn't handle it
        // differently
        if (currentTime - lastFallTime >= currentFallSpeed) {
            dropPiece();
            lastFallTime = currentTime;
        }

        checkGameOverConditions();
    }

    public void moveLeft() {
        currentPiece.moveLeft();
        if (!board.canPlace(currentPiece)) {
            currentPiece.moveRight();
        }
    }

    public void moveRight() {
        currentPiece.moveRight();
        if (!board.canPlace(currentPiece)) {
            currentPiece.moveLeft();
        }
    }

    public void rotate() {
        ui.GameSettings settings = ui.GameSettings.getInstance();

        if (settings.isAssistMode()) {
            performAssistRotation();
        } else {
            performNormalRotation();
        }
    }

    private void performNormalRotation() {
        currentPiece.rotateClockwise();
        if (!board.canPlace(currentPiece)) {
            currentPiece.rotateCounterClockwise();
        }
    }

    private void performAssistRotation() {
        Piece bestPiece = null;
        int bestY = currentPiece.getY();
        int originalY = currentPiece.getY();

        for (int rotation = 0; rotation < 4; rotation++) {
            currentPiece.rotateClockwise();
            int testY = originalY;
            while (board.canPlace(currentPiece)) {
                currentPiece.setY(testY);
                if (isGoodPosition(currentPiece)) {
                    bestPiece = currentPiece;
                    bestY = testY;
                    break;
                }
                testY++;
            }
            if (bestPiece != null)
                break;
        }

        if (bestPiece != null) {
            currentPiece.setY(bestY);
        } else {
            currentPiece.setY(originalY);
            currentPiece.rotateClockwise(); // Rotate back to start + 1
            if (!board.canPlace(currentPiece)) {
                currentPiece.rotateCounterClockwise();
            }
        }
    }

    private boolean isGoodPosition(Piece piece) {
        return board.canPlace(piece) && piece.getY() >= 0;
    }

    public void dropPiece() {
        currentPiece.moveDown();
        if (!board.canPlace(currentPiece)) {
            currentPiece.setY(currentPiece.getY() - 1);
            board.placePiece(currentPiece);
            piecesPlaced++;

            handlePiecePlacement();

            spawnNextPiece();

            checkGameOverConditions();
        }
    }

    private void handlePiecePlacement() {
        long currentTime = System.currentTimeMillis();
        long timeTaken = currentTime - pieceSpawnTime;
        int speedBonus = calculateSpeedBonus(timeTaken);

        int rowsCleared = board.clearFullRows();

        if (rowsCleared > 0) {
            consecutiveClears++;
            if (consecutiveClears > bestCombo) {
                bestCombo = consecutiveClears;
            }
            double comboMultiplier = 1.0 + (consecutiveClears - 1) * 0.5;
            double chainMultiplier = GameBoard.getChainMultiplier(rowsCleared);

            int tSpinBonus = 0;
            if (currentPiece.getType() == PieceType.T && currentPiece.wasRotated()) {
                tSpinBonus = getTSpinBonus(rowsCleared);
            }

            ui.GameSettings settings = ui.GameSettings.getInstance();
            if (!settings.isPracticeMode()) {
                board.calculateScore(rowsCleared, chainMultiplier, comboMultiplier,
                        speedBonus, tSpinBonus, consecutiveClears);
            } else {
                board.resetScore();
            }
        } else {
            consecutiveClears = 0;
        }
    }

    private void spawnNextPiece() {
        currentPiece.resetRotationFlag();
        currentPiece = nextPiece;
        nextPiece = new Piece();
        pieceSpawnTime = System.currentTimeMillis();
    }

    private int calculateSpeedBonus(long timeTakenMs) {
        if (timeTakenMs >= 1000)
            return 0;
        return (int) ((1000 - timeTakenMs) / 100) * 100;
    }

    private int getTSpinBonus(int rowsCleared) {
        switch (rowsCleared) {
            case 1:
                return 500;
            case 2:
                return 1000;
            case 3:
                return 1500;
            default:
                return 500;
        }
    }

    public void stepDown() {
        // Delegated via dropPiece loop or manual control, strategy controls auto-drop
        if (!gameOver) {
            dropPiece();
        }
    }

    public void speedDrop() {
        while (board.canPlace(currentPiece)) {
            currentPiece.moveDown();
        }
        currentPiece.setY(currentPiece.getY() - 1);
        board.placePiece(currentPiece);
        piecesPlaced++;

        handlePiecePlacement();
        spawnNextPiece();
        checkGameOverConditions();
    }

    private void checkGameOverConditions() {
        // Delegate to strategy
        if (ruleStrategy.isVictory(board)) {
            gameOver = true;
            return;
        }

        if (ruleStrategy.isGameOver(board, piecesPlaced)) {
            gameOver = true;
            return;
        }

        if (board.isGameOver(currentPiece)) {
            gameOver = true;
        }
    }

    public GameBoard getBoard() {
        return board;
    }

    public Piece getCurrentPiece() {
        return currentPiece;
    }

    public Piece getNextPiece() {
        return nextPiece;
    }

    public int getScore() {
        return board.getScore();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void saveScore(String playerName) {
        String modeInfo = gameMode.toString() + " - " + difficulty.toString();
        GameScore score = new GameScore(playerName, board.getScore(), modeInfo);
        leaderboardManager.addScore(score);
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getTotalRowsCleared() {
        return board.getTotalRowsCleared();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public int getPiecesPlaced() {
        return piecesPlaced;
    }

    public boolean isVictory() {
        if (!gameOver)
            return false;
        return ruleStrategy.isVictory(board);
    }

    public String getGameModeInfo() {
        return ruleStrategy.getModeInfo(board, currentFallSpeed, piecesPlaced);
    }

    public int getConsecutiveClears() {
        return consecutiveClears;
    }

    public int getBestCombo() {
        return bestCombo;
    }

    public ScoreBreakdown getLastScoreBreakdown() {
        return board.getLastScoreBreakdown();
    }

    // Setters for Strategy to use
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setCurrentFallSpeed(int speed) {
        this.currentFallSpeed = speed;
    }
}
