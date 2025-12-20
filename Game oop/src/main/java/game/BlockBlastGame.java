package game;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import ui.MainMenu;
import ui.GameModeScreen;
import ui.DifficultyScreen;
import ui.GameScreen;
import ui.GameOverScreen;
import ui.LeaderboardScreen;
import ui.SettingsScreen;
import ui.SettingsScreenExtended;
import ui.PauseScreen;
import utils.LeaderboardManager;

public class BlockBlastGame extends Application {
    private Stage primaryStage;
    private MainMenu mainMenu;
    private GameModeScreen gameModeScreen;
    private DifficultyScreen difficultyScreen;
    private LeaderboardManager leaderboardManager;
    private GameController currentGameController;
    private GameMode selectedGameMode;
    private GameScreen currentGameScreen;
    private javafx.scene.Scene gameScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.leaderboardManager = new LeaderboardManager();

        primaryStage.setTitle("Block Blast Game");
        primaryStage.setWidth(ui.UIConstants.WINDOW_WIDTH);
        primaryStage.setHeight(ui.UIConstants.WINDOW_HEIGHT);
        primaryStage.setResizable(false); // Không cho phép thay đổi kích thước

        showMainMenu();

        primaryStage.show();
        primaryStage.requestFocus();
    }

    private void showMainMenu() {
        mainMenu = new MainMenu(primaryStage, this::showGameModeScreen, this::showLeaderboard, this::showSettings);
        Scene scene = mainMenu.createScene();
        primaryStage.setScene(scene);
    }
    
    private void showSettings() {
        SettingsScreenExtended settingsScreen = new SettingsScreenExtended(primaryStage, this::showMainMenu);
        Scene scene = settingsScreen.createScene();
        primaryStage.setScene(scene);
    }

    private void showGameModeScreen() {
        gameModeScreen = new GameModeScreen(primaryStage, mode -> {
            selectedGameMode = mode;
            showDifficultyScreen();
        });
        Scene scene = gameModeScreen.createScene(this::showMainMenu);
        primaryStage.setScene(scene);
    }

    private void showDifficultyScreen() {
        difficultyScreen = new DifficultyScreen(primaryStage, difficulty -> startGame(difficulty, selectedGameMode));
        Scene scene = difficultyScreen.createScene(this::showGameModeScreen);
        primaryStage.setScene(scene);
    }

    private void startGame(Difficulty difficulty, GameMode gameMode) {
        currentGameController = new GameController(difficulty, gameMode);
        currentGameScreen = new GameScreen(primaryStage, currentGameController, this::showGameOver, this::showPauseMenu);
        gameScene = currentGameScreen.createScene();
        primaryStage.setScene(gameScene);
        gameScene.getRoot().requestFocus();
    }
    
    private void showPauseMenu() {
        if (currentGameScreen == null || currentGameController == null) return;
        
        // Set paused state
        currentGameScreen.setPaused(true);
        
        PauseScreen pauseScreen = new PauseScreen(
            primaryStage,
            this::resumeGame,  // Resume
            this::backToMenuFromPause,  // Back to Menu
            this::showSettingsFromPause,  // Settings
            this::quitGame  // Quit
        );
        Scene scene = pauseScreen.createScene();
        primaryStage.setScene(scene);
    }
    
    private void resumeGame() {
        if (currentGameScreen == null || gameScene == null) return;
        
        // Unpause
        currentGameScreen.setPaused(false);
        
        // Quay lại game scene đã lưu
        primaryStage.setScene(gameScene);
        gameScene.getRoot().requestFocus();
    }
    
    private void backToMenuFromPause() {
        // Reset game state
        currentGameController = null;
        currentGameScreen = null;
        gameScene = null;
        
        // Quay về main menu
        showMainMenu();
    }
    
    private void showSettingsFromPause() {
        // Hiển thị settings, khi quay lại sẽ về pause menu
        SettingsScreenExtended settingsScreen = new SettingsScreenExtended(primaryStage, this::showPauseMenu);
        Scene scene = settingsScreen.createScene();
        primaryStage.setScene(scene);
    }

    private void quitGame() {
        // Thoát game
        primaryStage.close();
    }

    private void showGameOver() {
        if (currentGameController == null) return;
        
        int finalScore = currentGameController.getScore();
        boolean isVictory = currentGameController.isVictory();
        
        // Kiểm tra practice mode
        ui.GameSettings settings = ui.GameSettings.getInstance();
        boolean isPracticeMode = settings.isPracticeMode();
        
        GameOverScreen gameOverScreen = new GameOverScreen(
            primaryStage, 
            finalScore,
            isVictory,
            isPracticeMode,
            this::showMainMenu
        );
        Scene scene = gameOverScreen.createScene(playerName -> {
            // Không lưu điểm nếu practice mode
            if (!isPracticeMode) {
                currentGameController.saveScore(playerName);
                // Cập nhật leaderboard manager với score mới
                leaderboardManager = currentGameController.getLeaderboardManager();
            }
        });
        primaryStage.setScene(scene);
    }

    private void showLeaderboard() {
        LeaderboardScreen leaderboardScreen = new LeaderboardScreen(primaryStage, leaderboardManager);
        Scene scene = leaderboardScreen.createScene(this::showMainMenu);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
