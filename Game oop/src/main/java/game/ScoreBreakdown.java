package game;

/**
 * Class lưu thông tin breakdown điểm để hiển thị
 */
public class ScoreBreakdown {
    private int baseScore;
    private double chainMultiplier;
    private double comboMultiplier;
    private int speedBonus;
    private int tSpinBonus;
    private int perfectClearBonus;
    private int totalScore;
    private int rowsCleared;
    
    public ScoreBreakdown(int baseScore, double chainMultiplier, double comboMultiplier,
                         int speedBonus, int tSpinBonus, int perfectClearBonus, int rowsCleared) {
        this.baseScore = baseScore;
        this.chainMultiplier = chainMultiplier;
        this.comboMultiplier = comboMultiplier;
        this.speedBonus = speedBonus;
        this.tSpinBonus = tSpinBonus;
        this.perfectClearBonus = perfectClearBonus;
        this.rowsCleared = rowsCleared;
        
        // Tính tổng điểm
        this.totalScore = (int)(baseScore * chainMultiplier * comboMultiplier) 
                         + speedBonus + tSpinBonus + perfectClearBonus;
    }
    
    public int getBaseScore() {
        return baseScore;
    }
    
    public double getChainMultiplier() {
        return chainMultiplier;
    }
    
    public double getComboMultiplier() {
        return comboMultiplier;
    }
    
    public int getSpeedBonus() {
        return speedBonus;
    }
    
    public int getTSpinBonus() {
        return tSpinBonus;
    }
    
    public int getPerfectClearBonus() {
        return perfectClearBonus;
    }
    
    public int getTotalScore() {
        return totalScore;
    }
    
    public int getRowsCleared() {
        return rowsCleared;
    }
    
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("+" + totalScore + " điểm\n");
        
        if (rowsCleared > 0) {
            String chainName = getChainName(rowsCleared);
            sb.append("├─ " + chainName + ": ×" + String.format("%.1f", chainMultiplier) + "\n");
        }
        
        if (comboMultiplier > 1.0) {
            sb.append("├─ Combo: ×" + String.format("%.1f", comboMultiplier) + "\n");
        }
        
        if (speedBonus > 0) {
            sb.append("├─ Speed Bonus: +" + speedBonus + "\n");
        }
        
        if (tSpinBonus > 0) {
            String tSpinType = getTSpinType(rowsCleared);
            sb.append("├─ " + tSpinType + ": +" + tSpinBonus + "\n");
        }
        
        if (perfectClearBonus > 0) {
            sb.append("└─ Perfect Clear: +" + perfectClearBonus + "\n");
        }
        
        return sb.toString();
    }
    
    private String getChainName(int rows) {
        switch (rows) {
            case 1: return "Single";
            case 2: return "Double";
            case 3: return "Triple";
            case 4: return "Tetris";
            default: return rows + " Lines";
        }
    }
    
    private String getTSpinType(int rows) {
        switch (rows) {
            case 1: return "Single T-Spin";
            case 2: return "Double T-Spin";
            case 3: return "Triple T-Spin";
            default: return "T-Spin";
        }
    }
}

