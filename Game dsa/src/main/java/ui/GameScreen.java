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
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Platform;

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
    private Text groupsClearedText;
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
    private java.util.List<CellClearEffect> cellClearEffects = new java.util.ArrayList<>();
    private boolean isGameOverAnimating = false;
    private double gameOverProgress = 0.0;

    private static final int CELL_SIZE = 26;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int PREVIEW_SLOTS = 3;

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

    private static class CellClearEffect {
        int row;
        int col;
        double life;

        CellClearEffect(int row, int col) {
            this.row = row;
            this.col = col;
            this.life = 1.0;
        }

        boolean update(double deltaTime) {
            life -= deltaTime * 2.5;
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
        UIStyle.applyScreenBackground(root, currentTheme);

        // Vùng giữa: Canvas chơi game, đặt trong một khung bo góc có đổ bóng
        gameCanvas = new Canvas(BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
        StackPane boardFrame = new StackPane(gameCanvas);
        boardFrame.setPadding(new Insets(12));
        boardFrame.setMaxSize(BOARD_WIDTH * CELL_SIZE + 24, BOARD_HEIGHT * CELL_SIZE + 24);
        boardFrame.setStyle(
                "-fx-background-color: " + UIStyle.rgba(currentTheme.getBoardBackgroundColor(), 0.95) + ";"
                        + "-fx-background-radius: 14;"
                        + "-fx-border-color: " + UIStyle.rgba(currentTheme.getAccentTextColor(), 0.75) + ";"
                        + "-fx-border-radius: 14; -fx-border-width: 2;"
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 22, 0.15, 0, 8);");
        StackPane boardArea = new StackPane(boardFrame);
        boardArea.setPadding(new Insets(UIConstants.SPACING_MEDIUM));

        // Vùng phải: bảng thông tin dạng "card"
        VBox rightPanel = new VBox(UIConstants.SPACING_SMALL);
        rightPanel.setPadding(new Insets(UIConstants.SPACING_MEDIUM));
        rightPanel.setPrefWidth(300);
        rightPanel.setStyle(UIStyle.cardCss(currentTheme));
        BorderPane.setMargin(rightPanel, new Insets(UIConstants.SPACING_MEDIUM,
                UIConstants.SPACING_MEDIUM, UIConstants.SPACING_MEDIUM, 0));

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

        groupsClearedText = new Text("Groups Cleared: 0");
        groupsClearedText.setFont(Font.font("Arial", (int) (UIConstants.FONT_SMALL * textSizeMultiplier)));
        groupsClearedText.setStyle("-fx-fill: " + currentTheme.colorToCss(currentTheme.getPrimaryTextColor()) + ";");

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
                groupsClearedText,
                comboText,
                piecesPlacedText,
                bestComboText,
                timeText,
                scoreBreakdownText);

        root.setCenter(boardArea);
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

        if (code == KeyCode.U) {
            gameController.undoLastMove();
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

        // Cập nhật trạng thái animation TRƯỚC khi vẽ (chưa vẽ gì ở đây).
        if (!settings.isReduceMotion()) {
            updateAnimations(deltaTime);
            updateFloatingScores(deltaTime);
        }

        // Vẽ game board (lấp nền canvas) -> sau đó mới vẽ hiệu ứng đè LÊN TRÊN.
        drawBoard();

        // Vẽ preview
        drawPreview();

        // Vẽ particle + floating scores SAU board, nếu không thì board sẽ xoá chúng.
        if (!settings.isReduceMotion()) {
            particleSystem.updateAndDraw(gameCanvas.getGraphicsContext2D(), deltaTime);
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
        groupsClearedText.setText("Groups Cleared: " + gameController.getTotalGroupsCleared());

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
                    breakdown.getRowsCleared() != lastDisplayedBreakdown.getRowsCleared() ||
                    breakdown.getClusterCellsCleared() != lastDisplayedBreakdown.getClusterCellsCleared() ||
                    breakdown.getGroupsCleared() != lastDisplayedBreakdown.getGroupsCleared()) {
                // Breakdown mới - reset timer và lưu breakdown mới
                lastBreakdownShowTime = currentTime;
                lastDisplayedBreakdown = breakdown;
            }

            // Hiển thị breakdown nếu chưa hết thời gian
            if (currentTime - lastBreakdownShowTime < BREAKDOWN_DISPLAY_DURATION) {
                scoreBreakdownText.setText(breakdown.getDescription());
                // Dùng màu nhấn của theme thay vì xanh lá cố định để hợp mọi theme.
                scoreBreakdownText.setStyle("-fx-fill: "
                        + currentTheme.colorToCss(currentTheme.getComboGlowColor()) + ";");
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

        double cw = gameCanvas.getWidth();
        double ch = gameCanvas.getHeight();

        // Nền bảng: gradient dọc nhẹ để tạo chiều sâu
        Color bgColor = currentTheme.getBoardBackgroundColor();
        GameSettings settings = GameSettings.getInstance();
        if (settings.isHighContrastMode()) {
            gc.setFill(bgColor);
        } else {
            gc.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, UIStyle.lighten(bgColor, 0.05)),
                    new Stop(1, UIStyle.darken(bgColor, 0.12))));
        }
        gc.fillRect(0, 0, cw, ch);

        int[][] grid = gameController.getBoard().getGrid();

        // Lưới mờ, theo theme (đậm hơn khi bật high contrast)
        Color gridColor = currentTheme.getGridColor();
        if (settings.isHighContrastMode()) {
            gridColor = gridColor.interpolate(Color.WHITE, 0.5);
            gc.setGlobalAlpha(1.0);
        } else {
            gc.setGlobalAlpha(0.35);
        }
        gc.setStroke(gridColor);
        gc.setLineWidth(settings.isHighContrastMode() ? 1.0 : 0.5);
        for (int i = 0; i <= BOARD_WIDTH; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, ch);
        }
        for (int i = 0; i <= BOARD_HEIGHT; i++) {
            gc.strokeLine(0, i * CELL_SIZE, cw, i * CELL_SIZE);
        }
        gc.setGlobalAlpha(1.0);

        // Vẽ các ô đã đặt: mỗi ô là một "khối" bo góc, có gradient + bevel + gloss.
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (grid[row][col] != 0) {
                    game.PieceType pieceType = gameController.getBoard().getPieceTypeAt(row, col);
                    Color blockColor = pieceType == null
                            ? Color.WHITE
                            : currentTheme.getPieceColor(pieceType, settings.getColorblindMode());
                    drawBlock(gc, col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, blockColor, 1.0,
                            settings.isHighContrastMode());
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
        if (!cellClearEffects.isEmpty()) {
            for (CellClearEffect effect : cellClearEffects) {
                gc.setFill(currentTheme.getComboGlowColor());
                gc.setGlobalAlpha(effect.life * 0.75);
                gc.fillRect(effect.col * CELL_SIZE, effect.row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
            gc.setGlobalAlpha(1.0);
        }

        if (isGameOverAnimating || (gameController.isGameOver() && gameOverProgress >= 1.0)) {
            double overlay = Math.min(1.0, gameOverProgress);
            gc.setFill(Color.rgb(0, 0, 0, 0.72));
            double height = ch * overlay;
            gc.fillRect(0, ch - height, cw, height);

            if (gameOverProgress >= 1.0) {
                boolean victory = gameController.isVictory();
                Color titleColor = victory ? Color.rgb(80, 230, 120) : Color.rgb(255, 90, 90);
                String msg = victory ? "CHIẾN THẮNG!" : "GAME OVER";
                gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
                gc.setFill(Color.rgb(0, 0, 0, 0.6));
                gc.fillText(msg, cw / 2 + 2, ch / 2 + 2); // bóng chữ
                gc.setFill(titleColor);
                gc.fillText(msg, cw / 2, ch / 2);
                gc.setFont(Font.font("Arial", 14));
                gc.setFill(Color.rgb(220, 220, 220));
                gc.fillText("Điểm: " + gameController.getScore(), cw / 2, ch / 2 + 28);
                gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
            }
        }
    }

    /**
     * Vẽ ghost piece (bóng của viên gạch sẽ rơi xuống)
     */
    private void drawGhostPiece(GraphicsContext gc, Piece piece) {
        // Tính vị trí rơi xuống trên một BẢN SAO, không đụng tới piece gốc
        // (drawGhostPiece chạy trong vòng vẽ; mutate state game ở đây rất dễ sinh lỗi).
        Piece probe = new Piece(piece);
        int testY = piece.getY();
        while (true) {
            probe.setY(testY);
            if (!gameController.getBoard().canPlace(probe)) {
                break;
            }
            testY++;
        }
        testY--; // Quay lại vị trí cuối cùng có thể đặt

        // Chỉ vẽ nếu ghost piece ở vị trí khác với piece hiện tại
        if (testY <= piece.getY()) {
            return; // Không cần vẽ nếu ghost ở cùng vị trí
        }

        // Ghost piece: khối bo góc trong suốt + viền sáng, nhuộm theo màu viên hiện tại
        GameSettings settings = GameSettings.getInstance();
        Color pieceColor = currentTheme.getPieceColor(piece.getType(), settings.getColorblindMode());
        int[][] shape = piece.getShape();
        double gap = Math.max(1.0, CELL_SIZE * 0.06);
        double s = CELL_SIZE - gap * 2;
        double arc = s * 0.30;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    double x = (piece.getX() + j) * CELL_SIZE + gap;
                    double y = (testY + i) * CELL_SIZE + gap;
                    gc.setGlobalAlpha(0.18);
                    gc.setFill(pieceColor);
                    gc.fillRoundRect(x, y, s, s, arc, arc);
                    gc.setGlobalAlpha(0.55);
                    gc.setStroke(UIStyle.lighten(pieceColor, 0.2));
                    gc.setLineWidth(1.5);
                    gc.strokeRoundRect(x, y, s, s, arc, arc);
                }
            }
        }
        gc.setGlobalAlpha(1.0);
    }

    private void drawPiece(GraphicsContext gc, Piece piece, Color color) {
        GameSettings settings = GameSettings.getInstance();
        int[][] shape = piece.getShape();
        boolean hc = settings.isHighContrastMode();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    double x = (piece.getX() + j) * CELL_SIZE;
                    double y = (piece.getY() + i) * CELL_SIZE;
                    drawBlock(gc, x, y, CELL_SIZE, color, 1.0, hc);
                }
            }
        }
    }

    /**
     * Vẽ một "khối" gạch đẹp: bo góc, gradient dọc (sáng trên -> tối dưới),
     * dải gloss sáng phía trên và viền trong tối để tách các khối.
     * highContrast = true -> vẽ phẳng + viền trắng rõ để đảm bảo tương phản.
     */
    private void drawBlock(GraphicsContext gc, double px, double py, double size,
                           Color color, double alpha, boolean highContrast) {
        gc.setGlobalAlpha(alpha);
        if (highContrast) {
            gc.setFill(color);
            gc.fillRect(px, py, size, size);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.0);
            gc.strokeRect(px + 0.5, py + 0.5, size - 1, size - 1);
            gc.setGlobalAlpha(1.0);
            return;
        }

        double gap = Math.max(1.0, size * 0.06);
        double x = px + gap;
        double y = py + gap;
        double s = size - gap * 2;
        if (s <= 0) { // ô quá nhỏ -> vẽ phẳng cho an toàn
            gc.setFill(color);
            gc.fillRect(px, py, size, size);
            gc.setGlobalAlpha(1.0);
            return;
        }
        double arc = s * 0.30;

        // Thân khối: gradient sáng (trên) -> màu gốc -> tối (dưới)
        LinearGradient body = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, UIStyle.lighten(color, 0.32)),
                new Stop(0.5, color),
                new Stop(1, UIStyle.darken(color, 0.30)));
        gc.setFill(body);
        gc.fillRoundRect(x, y, s, s, arc, arc);

        // Gloss: dải sáng mờ phía trên cho cảm giác bóng
        gc.setFill(Color.color(1, 1, 1, 0.22));
        gc.fillRoundRect(x + s * 0.12, y + s * 0.10, s * 0.76, s * 0.30, arc * 0.6, arc * 0.6);

        // Viền trong tối để tách các khối liền kề
        gc.setStroke(UIStyle.darken(color, 0.45));
        gc.setLineWidth(Math.max(1.0, size * 0.04));
        gc.strokeRoundRect(x, y, s, s, arc, arc);

        gc.setGlobalAlpha(1.0);
    }

    private void drawPreview() {
        GraphicsContext gc = previewCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());

        java.util.List<Piece> nextPieces = gameController.getNextPieces();
        GameSettings settings = GameSettings.getInstance();

        int slotsToDraw = Math.min(PREVIEW_SLOTS, nextPieces.size());
        if (slotsToDraw == 0) {
            return;
        }

        double slotHeight = previewCanvas.getHeight() / PREVIEW_SLOTS;
        double previewCellSize = Math.min(18, slotHeight / 3.2);

        for (int slot = 0; slot < slotsToDraw; slot++) {
            Piece nextPiece = nextPieces.get(slot);
            int[][] shape = nextPiece.getShape();
            Color pieceColor = currentTheme.getPieceColor(nextPiece.getType(), settings.getColorblindMode());
            gc.setFill(pieceColor);

            int minX = shape[0].length;
            int maxX = 0;
            int minY = shape.length;
            int maxY = 0;
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

            if (!found) {
                continue;
            }

            double pieceWidth = (maxX - minX + 1) * previewCellSize;
            double pieceHeight = (maxY - minY + 1) * previewCellSize;
            double slotTop = slot * slotHeight;
            double startX = (previewCanvas.getWidth() - pieceWidth) / 2 - minX * previewCellSize;
            double startY = slotTop + (slotHeight - pieceHeight) / 2 - minY * previewCellSize;

            boolean hc = settings.isHighContrastMode();
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] == 1) {
                        drawBlock(gc, startX + j * previewCellSize, startY + i * previewCellSize,
                                previewCellSize, pieceColor, 1.0, hc);
                    }
                }
            }
        }
    }

    /**
     * Update animations
     */
    private void updateAnimations(double deltaTime) {
        long clearTime = gameController.getBoard().getLastClearTime();
        if (clearTime > lastProcessedClearTime) {
            lastProcessedClearTime = clearTime;
            java.util.List<Integer> clearedRows = gameController.getBoard().getLastClearedRows();
            java.util.List<game.GameBoard.BoardCell> clearedCells = gameController.getBoard().getLastClearedCells();

            GameSettings settings = GameSettings.getInstance();
            if (!settings.isReduceMotion()) {
                for (int row : clearedRows) {
                    rowClearEffects.add(new RowClearEffect(row));

                    double centerY = (row + 0.5) * CELL_SIZE;
                    double centerX = BOARD_WIDTH * CELL_SIZE / 2.0;
                    Color effectColor = currentTheme.getComboGlowColor();
                    particleSystem.createRowClearExplosion(centerX, centerY, effectColor, 20);
                }

                for (game.GameBoard.BoardCell cell : clearedCells) {
                    cellClearEffects.add(new CellClearEffect(cell.getRow(), cell.getCol()));
                }

                game.ScoreBreakdown breakdown = gameController.getLastScoreBreakdown();
                if (breakdown != null) {
                    double centerX = BOARD_WIDTH * CELL_SIZE / 2.0;
                    double centerY = BOARD_HEIGHT * CELL_SIZE / 2.0;
                    if (!clearedRows.isEmpty()) {
                        centerY = (clearedRows.get(0) + 0.5) * CELL_SIZE;
                    } else if (!clearedCells.isEmpty()) {
                        centerX = (clearedCells.get(0).getCol() + 0.5) * CELL_SIZE;
                        centerY = (clearedCells.get(0).getRow() + 0.5) * CELL_SIZE;
                    }
                    addFloatingScore(centerX, centerY, "+" + breakdown.getTotalScore(),
                            currentTheme.getAccentTextColor());
                }
            }
        }

        java.util.Iterator<RowClearEffect> it = rowClearEffects.iterator();
        while (it.hasNext()) {
            RowClearEffect effect = it.next();
            if (!effect.update(deltaTime)) {
                it.remove();
            }
        }

        java.util.Iterator<CellClearEffect> cellIt = cellClearEffects.iterator();
        while (cellIt.hasNext()) {
            CellClearEffect effect = cellIt.next();
            if (!effect.update(deltaTime)) {
                cellIt.remove();
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
