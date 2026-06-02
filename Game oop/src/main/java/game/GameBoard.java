package game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class GameBoard {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;
    private static final int EMPTY = 0;
    private static final int MIN_CLUSTER_SIZE_TO_CLEAR = 5;
    // Column where new pieces spawn (matches Piece's default x and
    // GameController.spawnNextPiece). Used by the block-out / game-over test.
    private static final int SPAWN_COLUMN = 3;

    private int[][] grid;
    private int score = 0;
    private int combo = 0;
    private int totalRowsCleared = 0;
    private int totalGroupsCleared = 0;
    private ScoreBreakdown lastScoreBreakdown;
    private List<Integer> lastClearedRows = new ArrayList<>();
    private List<BoardCell> lastClearedCells = new ArrayList<>();
    private long lastClearTime = 0;

    public static class BoardCell {
        private final int row;
        private final int col;

        public BoardCell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    public static class ClearResult {
        private final int rowsCleared;
        private final int groupsCleared;
        private final int clusterCellsCleared;

        public ClearResult(int rowsCleared, int groupsCleared, int clusterCellsCleared) {
            this.rowsCleared = rowsCleared;
            this.groupsCleared = groupsCleared;
            this.clusterCellsCleared = clusterCellsCleared;
        }

        public int getRowsCleared() {
            return rowsCleared;
        }

        public int getGroupsCleared() {
            return groupsCleared;
        }

        public int getClusterCellsCleared() {
            return clusterCellsCleared;
        }

        public boolean hasClears() {
            return rowsCleared > 0 || clusterCellsCleared > 0;
        }

        public int getScoreUnits() {
            int groupUnits = clusterCellsCleared > 0
                    ? Math.max(1, (clusterCellsCleared + WIDTH - 1) / WIDTH)
                    : 0;
            return rowsCleared + groupUnits;
        }
    }

    public static class BoardState {
        private final int[][] grid;
        private final int score;
        private final int combo;
        private final int totalRowsCleared;
        private final int totalGroupsCleared;
        private final ScoreBreakdown lastScoreBreakdown;
        private final List<Integer> lastClearedRows;
        private final List<BoardCell> lastClearedCells;
        private final long lastClearTime;

        private BoardState(int[][] grid, int score, int combo, int totalRowsCleared,
                           int totalGroupsCleared, ScoreBreakdown lastScoreBreakdown,
                           List<Integer> lastClearedRows, List<BoardCell> lastClearedCells,
                           long lastClearTime) {
            this.grid = grid;
            this.score = score;
            this.combo = combo;
            this.totalRowsCleared = totalRowsCleared;
            this.totalGroupsCleared = totalGroupsCleared;
            this.lastScoreBreakdown = lastScoreBreakdown;
            this.lastClearedRows = lastClearedRows;
            this.lastClearedCells = lastClearedCells;
            this.lastClearTime = lastClearTime;
        }
    }

    private static class ClusterClearStats {
        int groupsCleared;
        int cellsCleared;
    }

    public GameBoard() {
        grid = new int[HEIGHT][WIDTH];
    }

    public boolean canPlace(Piece piece) {
        int[][] shape = piece.getShape();
        int pieceX = piece.getX();
        int pieceY = piece.getY();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int boardX = pieceX + j;
                    int boardY = pieceY + i;

                    if (boardX < 0 || boardX >= WIDTH || boardY >= HEIGHT) {
                        return false;
                    }
                    if (boardY >= 0 && grid[boardY][boardX] != EMPTY) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void placePiece(Piece piece) {
        int[][] shape = piece.getShape();
        int pieceX = piece.getX();
        int pieceY = piece.getY();
        int colorId = encodePieceType(piece.getType());

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int boardX = pieceX + j;
                    int boardY = pieceY + i;
                    if (boardY >= 0 && boardY < HEIGHT && boardX >= 0 && boardX < WIDTH) {
                        grid[boardY][boardX] = colorId;
                    }
                }
            }
        }
    }

    public ClearResult clearCompletedLinesAndGroups() {
        lastClearedRows.clear();
        lastClearedCells.clear();

        int rowsClearedThisTurn = clearFullRowsInternal();
        ClusterClearStats clusterStats = clearSameColorGroups();

        if (rowsClearedThisTurn > 0 || clusterStats.cellsCleared > 0) {
            totalRowsCleared += rowsClearedThisTurn;
            totalGroupsCleared += clusterStats.groupsCleared;
            combo = rowsClearedThisTurn + clusterStats.groupsCleared;
            lastClearTime = System.currentTimeMillis();
        } else {
            combo = 0;
        }

        return new ClearResult(rowsClearedThisTurn, clusterStats.groupsCleared, clusterStats.cellsCleared);
    }

    /**
     * Kept for older callers. New game flow uses clearCompletedLinesAndGroups().
     */
    public int clearFullRows() {
        return clearCompletedLinesAndGroups().getRowsCleared();
    }

    private int clearFullRowsInternal() {
        ArrayList<Integer> fullRows = new ArrayList<>();
        for (int row = 0; row < HEIGHT; row++) {
            if (isRowFull(row)) {
                fullRows.add(row);
            }
        }

        // Remove top-most full row first. removeRow() only shifts rows ABOVE the
        // removed row, so the indices of the remaining (lower) full rows stay valid.
        // Iterating bottom-to-top would invalidate those indices and clear the wrong rows.
        for (int i = 0; i < fullRows.size(); i++) {
            removeRow(fullRows.get(i));
        }

        lastClearedRows.addAll(fullRows);
        return fullRows.size();
    }

    private ClusterClearStats clearSameColorGroups() {
        boolean[][] visited = new boolean[HEIGHT][WIDTH];
        ClusterClearStats stats = new ClusterClearStats();

        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                int colorId = grid[row][col];
                if (colorId == EMPTY || visited[row][col]) {
                    continue;
                }

                List<BoardCell> cluster = findConnectedClusterWithBfs(row, col, colorId, visited);
                if (cluster.size() >= MIN_CLUSTER_SIZE_TO_CLEAR) {
                    lastClearedCells.addAll(cluster);
                    stats.groupsCleared++;
                    stats.cellsCleared += floodFillClear(row, col, colorId, new boolean[HEIGHT][WIDTH]);
                }
            }
        }

        return stats;
    }

    private List<BoardCell> findConnectedClusterWithBfs(int startRow, int startCol, int colorId,
                                                        boolean[][] visited) {
        List<BoardCell> cluster = new ArrayList<>();
        Queue<BoardCell> queue = new ArrayDeque<>();
        queue.add(new BoardCell(startRow, startCol));
        visited[startRow][startCol] = true;

        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            BoardCell cell = queue.poll();
            cluster.add(cell);

            for (int i = 0; i < dRow.length; i++) {
                int nextRow = cell.getRow() + dRow[i];
                int nextCol = cell.getCol() + dCol[i];
                if (isInside(nextRow, nextCol)
                        && !visited[nextRow][nextCol]
                        && grid[nextRow][nextCol] == colorId) {
                    visited[nextRow][nextCol] = true;
                    queue.add(new BoardCell(nextRow, nextCol));
                }
            }
        }

        return cluster;
    }

    private int floodFillClear(int row, int col, int colorId, boolean[][] visited) {
        if (!isInside(row, col) || visited[row][col] || grid[row][col] != colorId) {
            return 0;
        }

        visited[row][col] = true;
        grid[row][col] = EMPTY;

        return 1
                + floodFillClear(row - 1, col, colorId, visited)
                + floodFillClear(row + 1, col, colorId, visited)
                + floodFillClear(row, col - 1, colorId, visited)
                + floodFillClear(row, col + 1, colorId, visited);
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < HEIGHT && col >= 0 && col < WIDTH;
    }

    private boolean isRowFull(int row) {
        for (int col = 0; col < WIDTH; col++) {
            if (grid[row][col] == EMPTY) {
                return false;
            }
        }
        return true;
    }

    private void removeRow(int row) {
        for (int i = row; i > 0; i--) {
            grid[i] = grid[i - 1].clone();
        }
        grid[0] = new int[WIDTH];
    }

    public ScoreBreakdown calculateScore(ClearResult clearResult, double chainMultiplier,
                                         double comboMultiplier, int speedBonus,
                                         int tSpinBonus, int consecutiveClears) {
        int rowsCleared = clearResult.getRowsCleared();
        int clusterCells = clearResult.getClusterCellsCleared();
        int groupsCleared = clearResult.getGroupsCleared();
        int baseScore = (WIDTH * rowsCleared) + (clusterCells * 2) + (groupsCleared * 25);

        int perfectClearBonus = 0;
        if (isPerfectClear()) {
            perfectClearBonus = 5000;
        }

        int totalScore = (int) (baseScore * chainMultiplier * comboMultiplier)
                + speedBonus + tSpinBonus + perfectClearBonus;

        score += totalScore;

        lastScoreBreakdown = new ScoreBreakdown(baseScore, chainMultiplier, comboMultiplier,
                speedBonus, tSpinBonus, perfectClearBonus, rowsCleared,
                clusterCells, groupsCleared);

        return lastScoreBreakdown;
    }

    public ScoreBreakdown calculateScore(int rowsCleared, double chainMultiplier,
                                         double comboMultiplier, int speedBonus,
                                         int tSpinBonus, int consecutiveClears) {
        ClearResult clearResult = new ClearResult(rowsCleared, 0, 0);
        return calculateScore(clearResult, chainMultiplier, comboMultiplier,
                speedBonus, tSpinBonus, consecutiveClears);
    }

    public boolean isPerfectClear() {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (grid[row][col] != EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public static double getChainMultiplier(int clearUnits) {
        switch (clearUnits) {
            case 1:
                return 1.0;
            case 2:
                return 1.5;
            case 3:
                return 2.0;
            case 4:
                return 3.0;
            default:
                return 4.0;
        }
    }

    public boolean isGameOver(Piece piece) {
        // Block-out test: a piece can no longer enter the board at the SPAWN cell.
        // Reset BOTH x and y to the spawn position so the result does not depend on
        // where the player has slid the live piece -- otherwise moving the active
        // piece under a topped-out column would falsely end the game.
        Piece testPiece = new Piece(piece);
        testPiece.setX(SPAWN_COLUMN);
        testPiece.setY(0);
        return !canPlace(testPiece);
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getCombo() {
        return combo;
    }

    public ScoreBreakdown getLastScoreBreakdown() {
        return lastScoreBreakdown;
    }

    public void initializeWithRows(int numRows) {
        PieceType[] pieceTypes = PieceType.values();
        for (int row = HEIGHT - numRows; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (Math.random() > 0.3) {
                    int randomPieceIndex = (int) (Math.random() * pieceTypes.length);
                    grid[row][col] = randomPieceIndex + 1;
                }
            }
        }
    }

    public int getTotalRowsCleared() {
        return totalRowsCleared;
    }

    public int getTotalGroupsCleared() {
        return totalGroupsCleared;
    }

    public void resetRowsCleared() {
        totalRowsCleared = 0;
    }

    public List<Integer> getLastClearedRows() {
        return lastClearedRows;
    }

    public List<BoardCell> getLastClearedCells() {
        return lastClearedCells;
    }

    public long getLastClearTime() {
        return lastClearTime;
    }

    public PieceType getPieceTypeAt(int row, int col) {
        if (!isInside(row, col) || grid[row][col] == EMPTY) {
            return null;
        }

        int index = grid[row][col] - 1;
        PieceType[] values = PieceType.values();
        if (index < 0 || index >= values.length) {
            return null;
        }

        return values[index];
    }

    public BoardState createState() {
        return new BoardState(copyGrid(grid), score, combo, totalRowsCleared, totalGroupsCleared,
                lastScoreBreakdown, new ArrayList<>(lastClearedRows),
                new ArrayList<>(lastClearedCells), lastClearTime);
    }

    public void restoreState(BoardState state) {
        grid = copyGrid(state.grid);
        score = state.score;
        combo = state.combo;
        totalRowsCleared = state.totalRowsCleared;
        totalGroupsCleared = state.totalGroupsCleared;
        lastScoreBreakdown = state.lastScoreBreakdown;
        lastClearedRows = new ArrayList<>(state.lastClearedRows);
        lastClearedCells = new ArrayList<>(state.lastClearedCells);
        lastClearTime = state.lastClearTime;
    }

    private int encodePieceType(PieceType type) {
        return type.ordinal() + 1;
    }

    private static int[][] copyGrid(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }
}
