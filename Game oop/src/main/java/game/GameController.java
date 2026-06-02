package game;

import game.rules.ChallengeRules;
import game.rules.GameRuleStrategy;
import game.rules.MarathonRules;
import game.rules.SprintRules;
import game.rules.ZenRules;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import utils.GameScore;
import utils.LeaderboardManager;

public class GameController {
    private static final int NEXT_QUEUE_SIZE = 3;
    private static final int MAX_UNDO_HISTORY = 30;

    private GameBoard board;
    private Piece currentPiece;
    private Queue<Piece> pieceQueue;
    // LIFO undo history. ArrayDeque-as-stack: push()/pop() at the head are O(1),
    // and removeLast() drops the OLDEST snapshot in O(1) when the cap is hit
    // (java.util.Stack would need remove(0), an O(n) Vector shift).
    private Deque<GameSnapshot> moveHistory;
    private Difficulty difficulty;
    private GameMode gameMode;
    private GameRuleStrategy ruleStrategy;
    private LeaderboardManager leaderboardManager;
    private boolean gameOver = false;
    private long lastFallTime = 0;
    private long startTime = 0;
    private int currentFallSpeed;
    private int piecesPlaced = 0;

    private int consecutiveClears = 0;
    private int bestCombo = 0;
    private long pieceSpawnTime = 0;

    private static class GameSnapshot {
        private final GameBoard.BoardState boardState;
        private final Piece currentPiece;
        private final List<Piece> queuedPieces;
        private final boolean gameOver;
        private final long lastFallTime;
        private final long startTime;
        private final int currentFallSpeed;
        private final int piecesPlaced;
        private final int consecutiveClears;
        private final int bestCombo;
        private final long pieceSpawnTime;

        private GameSnapshot(GameBoard.BoardState boardState, Piece currentPiece,
                             List<Piece> queuedPieces, boolean gameOver, long lastFallTime,
                             long startTime, int currentFallSpeed, int piecesPlaced,
                             int consecutiveClears, int bestCombo, long pieceSpawnTime) {
            this.boardState = boardState;
            this.currentPiece = currentPiece;
            this.queuedPieces = queuedPieces;
            this.gameOver = gameOver;
            this.lastFallTime = lastFallTime;
            this.startTime = startTime;
            this.currentFallSpeed = currentFallSpeed;
            this.piecesPlaced = piecesPlaced;
            this.consecutiveClears = consecutiveClears;
            this.bestCombo = bestCombo;
            this.pieceSpawnTime = pieceSpawnTime;
        }
    }

    public GameController(Difficulty difficulty, GameMode gameMode) {
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.board = new GameBoard();
        this.board.initializeWithRows(difficulty.getInitialRows());
        this.leaderboardManager = new LeaderboardManager();
        this.currentPiece = new Piece();
        this.pieceQueue = new LinkedList<>();
        this.moveHistory = new ArrayDeque<>();
        fillPieceQueue();
        this.lastFallTime = System.currentTimeMillis();
        this.startTime = System.currentTimeMillis();
        this.pieceSpawnTime = System.currentTimeMillis();

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

        this.ruleStrategy.onGameStart(this);
    }

    public void update() {
        if (gameOver) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        ruleStrategy.update(this, elapsedTime);

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
                    bestPiece = new Piece(currentPiece);
                    bestY = testY;
                    break;
                }
                testY++;
            }
            if (bestPiece != null) {
                break;
            }
        }

        if (bestPiece != null) {
            currentPiece = bestPiece;
            currentPiece.setY(bestY);
        } else {
            currentPiece.setY(originalY);
            currentPiece.rotateClockwise();
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
            lockCurrentPiece();
        }
    }

    private void lockCurrentPiece() {
        saveMoveSnapshot();
        board.placePiece(currentPiece);
        piecesPlaced++;

        handlePiecePlacement();
        spawnNextPiece();
        checkGameOverConditions();
    }

    private void handlePiecePlacement() {
        long currentTime = System.currentTimeMillis();
        long timeTaken = currentTime - pieceSpawnTime;
        int speedBonus = calculateSpeedBonus(timeTaken);

        GameBoard.ClearResult clearResult = board.clearCompletedLinesAndGroups();

        if (clearResult.hasClears()) {
            consecutiveClears++;
            if (consecutiveClears > bestCombo) {
                bestCombo = consecutiveClears;
            }
            double comboMultiplier = 1.0 + (consecutiveClears - 1) * 0.5;
            double chainMultiplier = GameBoard.getChainMultiplier(Math.max(1, clearResult.getScoreUnits()));

            int tSpinBonus = 0;
            if (clearResult.getRowsCleared() > 0
                    && currentPiece.getType() == PieceType.T
                    && currentPiece.wasRotated()) {
                tSpinBonus = getTSpinBonus(clearResult.getRowsCleared());
            }

            ui.GameSettings settings = ui.GameSettings.getInstance();
            if (!settings.isPracticeMode()) {
                board.calculateScore(clearResult, chainMultiplier, comboMultiplier,
                        speedBonus, tSpinBonus, consecutiveClears);
            } else {
                board.resetScore();
            }
        } else {
            consecutiveClears = 0;
        }
    }

    private void spawnNextPiece() {
        currentPiece = pieceQueue.poll();
        if (currentPiece == null) {
            currentPiece = new Piece();
        }
        currentPiece.resetRotationFlag();
        currentPiece.setX(3);
        currentPiece.setY(0);
        fillPieceQueue();
        pieceSpawnTime = System.currentTimeMillis();
    }

    private void fillPieceQueue() {
        while (pieceQueue.size() < NEXT_QUEUE_SIZE) {
            pieceQueue.add(new Piece());
        }
    }

    private int calculateSpeedBonus(long timeTakenMs) {
        if (timeTakenMs >= 1000) {
            return 0;
        }
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
        if (!gameOver) {
            dropPiece();
        }
    }

    public void speedDrop() {
        if (gameOver) {
            return;
        }

        while (board.canPlace(currentPiece)) {
            currentPiece.moveDown();
        }
        currentPiece.setY(currentPiece.getY() - 1);
        lockCurrentPiece();
    }

    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) {
            return false;
        }

        GameSnapshot snapshot = moveHistory.pop();
        board.restoreState(snapshot.boardState);
        currentPiece = new Piece(snapshot.currentPiece);
        pieceQueue = new LinkedList<>();
        for (Piece piece : snapshot.queuedPieces) {
            pieceQueue.add(new Piece(piece));
        }
        fillPieceQueue();

        gameOver = snapshot.gameOver;
        lastFallTime = System.currentTimeMillis();
        startTime = snapshot.startTime;
        currentFallSpeed = snapshot.currentFallSpeed;
        piecesPlaced = snapshot.piecesPlaced;
        consecutiveClears = snapshot.consecutiveClears;
        bestCombo = snapshot.bestCombo;
        pieceSpawnTime = System.currentTimeMillis();
        return true;
    }

    private void saveMoveSnapshot() {
        if (moveHistory.size() >= MAX_UNDO_HISTORY) {
            moveHistory.removeLast(); // drop the oldest snapshot (tail), O(1)
        }
        moveHistory.push(new GameSnapshot(
                board.createState(),
                new Piece(currentPiece),
                copyQueuedPieces(),
                gameOver,
                lastFallTime,
                startTime,
                currentFallSpeed,
                piecesPlaced,
                consecutiveClears,
                bestCombo,
                pieceSpawnTime));
    }

    private List<Piece> copyQueuedPieces() {
        List<Piece> copies = new ArrayList<>();
        for (Piece piece : pieceQueue) {
            copies.add(new Piece(piece));
        }
        return copies;
    }

    private void checkGameOverConditions() {
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
        fillPieceQueue();
        return pieceQueue.peek();
    }

    public List<Piece> getNextPieces() {
        fillPieceQueue();
        return copyQueuedPieces();
    }

    public int getScore() {
        return board.getScore();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean canUndo() {
        return !moveHistory.isEmpty();
    }

    public int getUndoCount() {
        return moveHistory.size();
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

    public int getTotalGroupsCleared() {
        return board.getTotalGroupsCleared();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public int getPiecesPlaced() {
        return piecesPlaced;
    }

    public boolean isVictory() {
        if (!gameOver) {
            return false;
        }
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

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setCurrentFallSpeed(int speed) {
        this.currentFallSpeed = speed;
    }
}
