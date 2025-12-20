package game;

public class GameBoard {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;
    private int[][] grid;
    private int score = 0;
    private int combo = 0;
    private int totalRowsCleared = 0; // Tổng số hàng đã xóa (cho Sprint mode)
    private ScoreBreakdown lastScoreBreakdown; // Breakdown điểm lần cuối
    private java.util.List<Integer> lastClearedRows = new java.util.ArrayList<>();
    private long lastClearTime = 0;

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
                    if (boardY >= 0 && grid[boardY][boardX] != 0) {
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

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int boardX = pieceX + j;
                    int boardY = pieceY + i;
                    if (boardY >= 0 && boardY < HEIGHT && boardX >= 0 && boardX < WIDTH) {
                        grid[boardY][boardX] = 1;
                    }
                }
            }
        }
    }

    /**
     * Xóa các hàng đầy và trả về số hàng đã xóa
     * @return số hàng đã xóa (0 nếu không có hàng nào)
     */
    public int clearFullRows() {
        int rowsClearedThisTurn = 0;
        // Tìm tất cả các hàng đầy trước
        java.util.ArrayList<Integer> fullRows = new java.util.ArrayList<>();
        lastClearedRows.clear();
        for (int row = 0; row < HEIGHT; row++) {
            if (isRowFull(row)) {
                fullRows.add(row);
            }
        }
        
        rowsClearedThisTurn = fullRows.size();
        
        // Xóa các hàng từ dưới lên để tránh vấn đề index
        for (int i = fullRows.size() - 1; i >= 0; i--) {
            int row = fullRows.get(i);
            removeRow(row);
        }

        if (rowsClearedThisTurn > 0) {
            totalRowsCleared += rowsClearedThisTurn;
            combo = rowsClearedThisTurn; // Lưu số hàng xóa cho multiplier
            lastClearedRows.addAll(fullRows);
            lastClearTime = System.currentTimeMillis();
        } else {
            combo = 0; // Reset combo nếu không xóa hàng
        }
        
        return rowsClearedThisTurn;
    }

    private boolean isRowFull(int row) {
        for (int col = 0; col < WIDTH; col++) {
            if (grid[row][col] == 0) {
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

    /**
     * Tính điểm với các hệ số và bonus
     * @param chainMultiplier hệ số chain (1.0, 1.5, 2.0, 3.0, 4.0)
     * @param comboMultiplier hệ số combo (1.0 + (consecutiveClears - 1) * 0.5)
     * @param speedBonus bonus tốc độ
     * @param tSpinBonus bonus T-Spin
     * @return ScoreBreakdown object
     */
    public ScoreBreakdown calculateScore(int rowsCleared, double chainMultiplier, 
                                        double comboMultiplier, int speedBonus, 
                                        int tSpinBonus, int consecutiveClears) {
        // Base score = số ô trên hàng × số hàng xóa
        int baseScore = WIDTH * rowsCleared;
        
        // Perfect Clear bonus
        int perfectClearBonus = 0;
        if (isPerfectClear()) {
            perfectClearBonus = 5000;
        }
        
        // Tính tổng điểm
        int totalScore = (int)(baseScore * chainMultiplier * comboMultiplier) 
                        + speedBonus + tSpinBonus + perfectClearBonus;
        
        score += totalScore;
        
        // Tạo breakdown
        lastScoreBreakdown = new ScoreBreakdown(baseScore, chainMultiplier, comboMultiplier,
                                               speedBonus, tSpinBonus, perfectClearBonus, rowsCleared);
        
        return lastScoreBreakdown;
    }
    
    /**
     * Kiểm tra Perfect Clear - toàn bộ bảng trống
     */
    public boolean isPerfectClear() {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (grid[row][col] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Lấy chain multiplier dựa trên số hàng xóa
     */
    public static double getChainMultiplier(int rowsCleared) {
        switch (rowsCleared) {
            case 1: return 1.0;  // Single
            case 2: return 1.5;  // Double
            case 3: return 2.0;  // Triple
            case 4: return 3.0;  // Tetris
            default: return 4.0; // 5+ dòng
        }
    }

    public boolean isGameOver(Piece piece) {
        piece.setY(0);
        return !canPlace(piece);
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
        // Khởi tạo một số hàng ở dưới cùng với các ô ngẫu nhiên
        for (int row = HEIGHT - numRows; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                // Tạo một số ô trống ngẫu nhiên để game vẫn có thể chơi được
                if (Math.random() > 0.3) { // 70% ô được điền
                    grid[row][col] = 1;
                }
            }
        }
    }

    public int getTotalRowsCleared() {
        return totalRowsCleared;
    }

    public void resetRowsCleared() {
        totalRowsCleared = 0;
    }

    public java.util.List<Integer> getLastClearedRows() {
        return lastClearedRows;
    }

    public long getLastClearTime() {
        return lastClearTime;
    }
}
