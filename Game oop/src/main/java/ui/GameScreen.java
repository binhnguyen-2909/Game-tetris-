package ui;

import game.GameController;
import game.Piece;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Platform;
import ui.UIConstants;

public class GameScreen {
    private GameController gameController;
    private Canvas gameCanvas;
    private Canvas previewCanvas;
    private Text scoreText;
    private Text modeInfoText;
    private Text timeText;
    private Text comboText;
    private Text scoreBreakdownText;
    private Text linesClearedText;
    private Text piecesPlacedText;
    private Text bestComboText;
    private Stage stage;
    private Runnable onGameOver;
    private Runnable onPause;
    private boolean isPaused = false;
    private AnimationTimer gameLoop;
    private long lastBreakdownShowTime = 0;
    private game.ScoreBreakdown lastDisplayedBreakdown = null;
    private static final long BREAKDOWN_DISPLAY_DURATION = 3000; // 3 giây

    // Theme system
    private Theme currentTheme;
    private ThemeManager themeManager;

    // Animation và effects
    private ParticleSystem particleSystem;
    private long lastFrameTime;
    private java.util.ArrayList<FloatingScore> floatingScores;

    // Animation states
    private boolean isRowClearing = false;
    private double rowClearAnimationProgress = 0.0;
    private int lastRowsCleared = 0;
    private long lastProcessedClearTime = 0;
    private java.util.List<RowClearEffect> rowClearEffects = new java.util.ArrayList<>();
    private boolean isGameOverAnimating = false;
    private double gameOverProgress = 0.0;

    private static final int CELL_SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    /**
     * Class để hiển thị điểm bay lên
     */
    private static class FloatingScore {
        double x, y;
        String text;
        double life;
        Color color;

        FloatingScore(double x, double y, String text, Color color) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.life = 1.0;
            this.color = color;
        }

        boolean update(double deltaTime) {
            y -= 50 * deltaTime; // Bay lên
            life -= deltaTime * 0.8;
            return life > 0;
        }
    }

    private static class RowClearEffect {
        int row;
        double life; // 1.0 -> 0.0

        RowClearEffect(int row) {
            this.row = row;
            this.life = 1.0;
        }

        boolean update(double deltaTime) {
            life -= deltaTime * 2.0; // 0.5 seconds duration
            return life > 0;
        }
    }

    public GameScreen(Stage stage, GameController gameController, Runnable onGameOver, Runnable onPause) {
        this.stage = stage;
        this.gameController = gameController;
        this.onGameOver = onGameOver;
        this.onPause = onPause;
        this.themeManager = ThemeManager.getInstance();
        this.currentTheme = themeManager.getCurrentTheme();
        this.particleSystem = new ParticleSystem();
        this.floatingScores = new java.util.ArrayList<>();
        this.lastFrameTime = System.nanoTime();
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        String bgColor = currentTheme.colorToCss(currentTheme.getBackgroundColor());
        root.setStyle("-fx-background-color: " + bgColor + ";");

        // Vùng trái: Canvas chơi game
        gameCanvas = new Canvas(BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
        gameCanvas.setStyle("-fx-border-color: #00ff00; -fx-border-width: 2;");

        // Vùng phải
        VBox rightPanel = new VBox(UIConstants.SPACING_SMALL);
        rightPanel.setPadding(new Insets(UIConstants.SPACING_SMALL));
        String panelBgColor = currentTheme.colorToCss(
                currentTheme.getBackgroundColor().interpolate(currentTheme.getBoardBackgroundColor(), 0.5));
        rightPanel.setStyle("-fx-background-color: " + panelBgColor + ";");

        // Preview piece
        previewCanvas = new Canvas(5 * CELL_SIZE, 5 * CELL_SIZE);
        StackPane previewContainer = new StackPane(previewCanvas);
        previewContainer.setStyle("-fx-border-color: " + currentTheme.colorToCss(currentTheme.getGridColor()) +
                "; -fx-border-width: 2; -fx-background-color: " +
                currentTheme.colorToCss(currentTheme.getBoardBackgroundColor()) + ";");
        previewContainer.setMaxSize(5 * CELL_SIZE + 4, 5 * CELL_SIZE + 4); // +4 for border

        // Apply text size multiplier
        GameSettings settings = GameSettings.getInstance();
        double textSizeMultiplier = settings.getTextSizeMultiplier();

        // Mode info
        modeInfoText = new Text("");
        modeInfoText.setFont(Font.font("Arial", (int) (UIConstants.FONT_SMALL * textSizeMultiplier)));
        modeInfoText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getAccentTextColor()) + ";");

        // Score
        scoreText = new Text("ĐIỂM: 0");
        scoreText.setFont(Font.font("Arial", (int) (UIConstants.FONT_MEDIUM * textSizeMultiplier)));
        scoreText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getAccentTextColor()) + ";");

        // Score breakdown text (hiển thị breakdown điểm)
        scoreBreakdownText = new Text("");
        scoreBreakdownText.setFont(Font.font("Arial", (int) (UIConstants.FONT_TINY * textSizeMultiplier)));
        scoreBreakdownText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getAccentTextColor()) + ";");
        scoreBreakdownText.setWrappingWidth(200);

        Text previewLabel = new Text("VIÊN TIẾP THEO:");
        previewLabel.setFont(Font.font("Arial", (int) (UIConstants.FONT_SMALL * textSizeMultiplier)));
        previewLabel.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getPrimaryTextColor()) + ";");

        // Lines Cleared
        linesClearedText = new Text("Lines Cleared: 0");
        linesClearedText.setFont(Font.font("Arial", (int) (UIConstants.FONT_SMALL * textSizeMultiplier)));
        linesClearedText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getPrimaryTextColor()) + ";");

        // Combo Counter (sử dụng comboText hiện tại nhưng format lại)
        comboText = new Text("Combo Counter: 0");
        comboText.setFont(Font.font("Arial", (int) (UIConstants.FONT_SMALL * textSizeMultiplier)));
        comboText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getPrimaryTextColor()) + ";");

        // Pieces Placed
        piecesPlacedText = new Text("Pieces Placed: 0");
        piecesPlacedText.setFont(Font.font("Arial", (int) (UIConstants.FONT_SMALL * textSizeMultiplier)));
        piecesPlacedText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getPrimaryTextColor()) + ";");

        // Best Combo
        bestComboText = new Text("Best Combo: 0");
        bestComboText.setFont(Font.font("Arial", (int) (UIConstants.FONT_SMALL * textSizeMultiplier)));
        bestComboText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getPrimaryTextColor()) + ";");

        // Time Elapsed (format lại để hiển thị cho tất cả modes)
        timeText = new Text("Time Elapsed: 00:00");
        timeText.setFont(Font.font("Arial", (int) (UIConstants.FONT_SMALL * textSizeMultiplier)));
        timeText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getPrimaryTextColor()) + ";");

        rightPanel.getChildren().addAll(
                modeInfoText,
                previewLabel,
                previewContainer,
                scoreText,
                linesClearedText,
                comboText,
                piecesPlacedText,
                bestComboText,
                timeText,
                scoreBreakdownText);

        root.setLeft(gameCanvas);
        root.setRight(rightPanel);

        Scene scene = new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);

        // Xử lý input
        scene.setOnKeyPressed(this::handleKeyPress);
        scene.setOnKeyReleased(this::handleKeyRelease);

        // Game loop
        startGameLoop();

        return scene;
    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        // Phím P để pause/resume
        if (code == KeyCode.P || code == KeyCode.ESCAPE) {
            if (onPause != null && !isPaused) {
                onPause.run();
            }
            return;
        }

        // Không xử lý input khi pause
        if (isPaused) {
            return;
        }

        GameSettings settings = GameSettings.getInstance();
        if (code == settings.getMoveLeftKey()) {
            gameController.moveLeft();
        } else if (code == settings.getMoveRightKey()) {
            gameController.moveRight();
        } else if (code == settings.getMoveDownKey()) {
            // Zen mode: mũi tên xuống để rơi từng bước
            if (gameController.getGameMode() == game.GameMode.ZEN) {
                gameController.stepDown();
            } else {
                gameController.speedDrop();
            }
        } else if (code == settings.getRotateKey()) {
            gameController.rotate();
        } else if (code == settings.getHardDropKey()) {
            gameController.speedDrop();
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        // Có thể xử lý release event nếu cần
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Tính deltaTime
                double deltaTime = (now - lastFrameTime) / 1_000_000_000.0; // Convert to seconds
                lastFrameTime = now;

                // Chỉ update game khi không pause và chưa game over
                if (!isPaused && !gameController.isGameOver()) {
                    gameController.update();
                }

                // Vẫn update UI để vẽ (có thể hiển thị "PAUSED" overlay)
                updateUI(deltaTime);

                if (gameController.isGameOver()) {
                    if (!isGameOverAnimating) {
                        isGameOverAnimating = true;
                        gameOverProgress = 0.0;
                    }

                    gameOverProgress += deltaTime * 1.0; // 1 second animation

                    // Wait a bit after animation finishes before showing game over screen
                    if (gameOverProgress >= 1.5) {
                        stop();
                        // Hiển thị màn hình kết thúc
                        if (onGameOver != null) {
                            Platform.runLater(() -> onGameOver.run());
                        }
                    }
                }
            }
        };
        gameLoop.start();
    }

    private void updateUI(double deltaTime) {
        GameSettings settings = GameSettings.getInstance();

        // Update animations (skip if reduce motion)
        if (!settings.isReduceMotion()) {
            updateAnimations(deltaTime);
            updateFloatingScores(deltaTime);
            particleSystem.updateAndDraw(gameCanvas.getGraphicsContext2D(), deltaTime);
        }

        // Vẽ game board
        drawBoard();

        // Vẽ preview
        drawPreview();

        // Vẽ floating scores (skip if reduce motion)
        if (!settings.isReduceMotion()) {
            drawFloatingScores();
        }

        // Cập nhật thông tin mode
        modeInfoText.setText(gameController.getGameModeInfo());

        // Cập nhật điểm (hiển thị "LUYỆN TẬP" nếu practice mode)
        if (settings.isPracticeMode()) {
            scoreText.setText("LUYỆN TẬP - ĐIỂM: " + gameController.getScore());
        } else {
            scoreText.setText("ĐIỂM: " + gameController.getScore());
        }

        // Cập nhật Lines Cleared
        linesClearedText.setText("Lines Cleared: " + gameController.getTotalRowsCleared());

        // Cập nhật Combo Counter
        int combo = gameController.getConsecutiveClears();
        comboText.setText("Combo Counter: " + combo);
        if (combo > 0) {
            comboText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getComboGlowColor()) + ";");
        } else {
            comboText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getPrimaryTextColor()) + ";");
        }

        // Cập nhật Pieces Placed
        piecesPlacedText.setText("Pieces Placed: " + gameController.getPiecesPlaced());

        // Cập nhật Best Combo
        bestComboText.setText("Best Combo: " + gameController.getBestCombo());

        // Cập nhật Time Elapsed (hiển thị cho tất cả modes)
        long elapsedSeconds = gameController.getElapsedTime() / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        timeText.setText(String.format("Time Elapsed: %02d:%02d", minutes, seconds));

        // Hiển thị breakdown điểm nếu có
        game.ScoreBreakdown breakdown = gameController.getLastScoreBreakdown();
        long currentTime = System.currentTimeMillis();

        if (breakdown != null && breakdown.getTotalScore() > 0) {
            // Kiểm tra xem có breakdown mới không (so sánh với breakdown đã hiển thị trước
            // đó)
            if (lastDisplayedBreakdown == null ||
                    breakdown.getTotalScore() != lastDisplayedBreakdown.getTotalScore() ||
                    breakdown.getRowsCleared() != lastDisplayedBreakdown.getRowsCleared()) {
                // Breakdown mới - reset timer và lưu breakdown mới
                lastBreakdownShowTime = currentTime;
                lastDisplayedBreakdown = breakdown;
            }

            // Hiển thị breakdown nếu chưa hết thời gian
            if (currentTime - lastBreakdownShowTime < BREAKDOWN_DISPLAY_DURATION) {
                scoreBreakdownText.setText(breakdown.getDescription());
                scoreBreakdownText.setStyle("-fx-fill: #00ff00;");
            } else {
                scoreBreakdownText.setText("");
            }
        } else {
            // Ẩn breakdown nếu không có
            scoreBreakdownText.setText("");
            lastDisplayedBreakdown = null;
            lastBreakdownShowTime = 0;
        }
    }

    private void drawBoard() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        // Background với theme color
        Color bgColor = currentTheme.getBoardBackgroundColor();
        gc.setFill(bgColor);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        int[][] grid = gameController.getBoard().getGrid();

        // Vẽ grid với theme color (có high contrast support)
        GameSettings settings = GameSettings.getInstance();
        Color gridColor = currentTheme.getGridColor();
        if (settings.isHighContrastMode()) {
            // Tăng contrast của grid
            gridColor = gridColor.interpolate(Color.WHITE, 0.5);
        }
        gc.setStroke(gridColor);
        gc.setLineWidth(settings.isHighContrastMode() ? 1.0 : 0.5);
        for (int i = 0; i <= BOARD_WIDTH; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, gameCanvas.getHeight());
        }
        for (int i = 0; i <= BOARD_HEIGHT; i++) {
            gc.strokeLine(0, i * CELL_SIZE, gameCanvas.getWidth(), i * CELL_SIZE);
        }

        // Vẽ các ô đã được đặt với màu từ theme
        // TODO: Lưu màu của từng piece khi đặt để hiển thị đúng màu
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (grid[row][col] != 0) {
                    // Default color (TODO: lưu piece type để hiển thị đúng màu)
                    // Tạm dùng màu mặc định với colorblind support
                    Color blockColor = currentTheme.getPieceColors()[2];
                    if (settings.getColorblindMode() != GameSettings.ColorblindMode.NONE) {
                        blockColor = currentTheme.getPieceColor(game.PieceType.T, settings.getColorblindMode());
                    }
                    gc.setFill(blockColor);
                    int borderWidth = settings.isHighContrastMode() ? 0 : 1;
                    gc.fillRect(col * CELL_SIZE + borderWidth, row * CELL_SIZE + borderWidth,
                            CELL_SIZE - borderWidth * 2, CELL_SIZE - borderWidth * 2);
                }
            }
        }

        // Vẽ ghost piece (vị trí rơi xuống)
        drawGhostPiece(gc, gameController.getCurrentPiece());

        // Vẽ viên gạch hiện tại với màu từ theme (có colorblind support)
        Piece currentPiece = gameController.getCurrentPiece();
        Color pieceColor = currentTheme.getPieceColor(currentPiece.getType(), settings.getColorblindMode());
        drawPiece(gc, currentPiece, pieceColor);

        // Vẽ row clear effects
        if (!rowClearEffects.isEmpty()) {
            for (RowClearEffect effect : rowClearEffects) {
                gc.setFill(Color.WHITE);
                gc.setGlobalAlpha(effect.life * 0.8);
                gc.fillRect(0, effect.row * CELL_SIZE, BOARD_WIDTH * CELL_SIZE, CELL_SIZE);
            }
            gc.setGlobalAlpha(1.0);
        }

        // Vẽ game over animation
        if (isGameOverAnimating || (gameController.isGameOver() && gameOverProgress >= 1.0)) {
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            double height = gameCanvas.getHeight() * gameOverProgress;
            gc.fillRect(0, gameCanvas.getHeight() - height, gameCanvas.getWidth(), height);

            if (gameOverProgress >= 1.0) {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
                gc.fillText("GAME OVER",
                        gameCanvas.getWidth() / 2 - 90,
                        gameCanvas.getHeight() / 2);
            }
        }
    }

    /**
     * Vẽ ghost piece (bóng của viên gạch sẽ rơi xuống)
     */
    private void drawGhostPiece(GraphicsContext gc, Piece piece) {
        // Tính vị trí rơi xuống bằng cách simulate
        int ghostX = piece.getX();
        int ghostY = piece.getY();

        // Tạo một piece tạm để test (không modify piece gốc)
        // Vì không có copy constructor, ta sẽ simulate bằng cách test từng vị trí
        int testY = piece.getY();
        while (true) {
            // Test xem có thể đặt piece ở vị trí này không
            piece.setY(testY);
            if (!gameController.getBoard().canPlace(piece)) {
                break;
            }
            testY++;
        }
        testY--; // Quay lại vị trí cuối cùng có thể đặt
        piece.setY(ghostY); // Restore original position

        // Chỉ vẽ nếu ghost piece ở vị trí khác với piece hiện tại
        if (testY <= piece.getY()) {
            return; // Không cần vẽ nếu ghost ở cùng vị trí
        }

        // Vẽ ghost piece với màu trong suốt
        Color ghostColor = currentTheme.getGhostPieceColor();
        int[][] shape = piece.getShape();
        gc.setGlobalAlpha(ghostColor.getOpacity());
        gc.setStroke(ghostColor);
        gc.setLineWidth(2);

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int x = (piece.getX() + j) * CELL_SIZE;
                    int y = (testY + i) * CELL_SIZE;
                    // Vẽ outline
                    gc.strokeRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                }
            }
        }
        gc.setGlobalAlpha(1.0);
    }

    private void drawPiece(GraphicsContext gc, Piece piece, Color color) {
        GameSettings settings = GameSettings.getInstance();
        int[][] shape = piece.getShape();
        gc.setFill(color);
        gc.setGlobalAlpha(1.0);

        int borderWidth = settings.isHighContrastMode() ? 0 : 1;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int x = (piece.getX() + j) * CELL_SIZE;
                    int y = (piece.getY() + i) * CELL_SIZE;
                    // Vẽ với border nhẹ để trông đẹp hơn (trừ khi high contrast)
                    gc.fillRect(x + borderWidth, y + borderWidth, CELL_SIZE - borderWidth * 2,
                            CELL_SIZE - borderWidth * 2);
                    // Highlight (skip nếu high contrast hoặc reduce motion)
                    if (!settings.isHighContrastMode() && !settings.isReduceMotion()) {
                        Color lighter = color.interpolate(Color.WHITE, 0.3);
                        gc.setFill(lighter);
                        gc.fillRect(x + borderWidth, y + borderWidth, CELL_SIZE - borderWidth * 2,
                                (CELL_SIZE - borderWidth * 2) / 3);
                        gc.setFill(color);
                    }
                }
            }
        }
    }

    private void drawPreview() {
        GraphicsContext gc = previewCanvas.getGraphicsContext2D();
        // Clear canvas (background is handled by container)
        gc.clearRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());

        Piece nextPiece = gameController.getNextPiece();
        int[][] shape = nextPiece.getShape();
        GameSettings settings = GameSettings.getInstance();
        Color pieceColor = currentTheme.getPieceColor(nextPiece.getType(), settings.getColorblindMode());
        gc.setFill(pieceColor);

        // Calculate bounding box to center the piece visually
        int minX = shape[0].length, maxX = 0;
        int minY = shape.length, maxY = 0;
        boolean found = false;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    minX = Math.min(minX, j);
                    maxX = Math.max(maxX, j);
                    minY = Math.min(minY, i);
                    maxY = Math.max(maxY, i);
                    found = true;
                }
            }
        }

        if (!found)
            return;

        double pieceWidth = (maxX - minX + 1) * CELL_SIZE;
        double pieceHeight = (maxY - minY + 1) * CELL_SIZE;

        double startX = (previewCanvas.getWidth() - pieceWidth) / 2 - minX * CELL_SIZE;
        double startY = (previewCanvas.getHeight() - pieceHeight) / 2 - minY * CELL_SIZE;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    gc.fillRect(startX + j * CELL_SIZE + 1, startY + i * CELL_SIZE + 1,
                            CELL_SIZE - 2, CELL_SIZE - 2);
                }
            }
        }
    }

    /**
     * Update animations
     */
    private void updateAnimations(double deltaTime) {
        // Check for new row clears using timestamp
        long clearTime = gameController.getBoard().getLastClearTime();
        if (clearTime > lastProcessedClearTime) {
            lastProcessedClearTime = clearTime;
            java.util.List<Integer> clearedRows = gameController.getBoard().getLastClearedRows();

            GameSettings settings = GameSettings.getInstance();
            if (!settings.isReduceMotion()) {
                // Create effects for each cleared row
                for (int row : clearedRows) {
                    rowClearEffects.add(new RowClearEffect(row));

                    // Particles for this row
                    double centerY = (row + 0.5) * CELL_SIZE;
                    double centerX = BOARD_WIDTH * CELL_SIZE / 2.0;
                    Color effectColor = currentTheme.getComboGlowColor();
                    particleSystem.createRowClearExplosion(centerX, centerY, effectColor, 20);
                }

                // Floating score
                game.ScoreBreakdown breakdown = gameController.getLastScoreBreakdown();
                if (breakdown != null) {
                    double centerX = BOARD_WIDTH * CELL_SIZE / 2.0;
                    double centerY = (clearedRows.get(0) + 0.5) * CELL_SIZE; // Use top-most cleared row
                    addFloatingScore(centerX, centerY, "+" + breakdown.getTotalScore(),
                            currentTheme.getAccentTextColor());
                }
            }
        }

        // Update row clear effects
        java.util.Iterator<RowClearEffect> it = rowClearEffects.iterator();
        while (it.hasNext()) {
            RowClearEffect effect = it.next();
            if (!effect.update(deltaTime)) {
                it.remove();
            }
        }
    }

    /**
     * Update floating scores
     */
    private void updateFloatingScores(double deltaTime) {
        java.util.Iterator<FloatingScore> it = floatingScores.iterator();
        while (it.hasNext()) {
            FloatingScore score = it.next();
            if (!score.update(deltaTime)) {
                it.remove();
            }
        }
    }

    /**
     * Draw floating scores
     */
    private void drawFloatingScores() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        for (FloatingScore score : floatingScores) {
            Color drawColor = Color.color(
                    score.color.getRed(),
                    score.color.getGreen(),
                    score.color.getBlue(),
                    score.life);
            gc.setFill(drawColor);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.fillText(score.text, score.x, score.y);
        }
    }

    /**
     * Thêm floating score
     */
    private void addFloatingScore(double x, double y, String text, Color color) {
        floatingScores.add(new FloatingScore(x, y, text, color));
    }
}
