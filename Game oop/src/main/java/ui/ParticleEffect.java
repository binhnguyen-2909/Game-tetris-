package ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Class cho particle effects
 */
public class ParticleEffect {
    private double x, y;
    private double vx, vy;
    private Color color;
    private double life; // 0.0 to 1.0
    private double size;
    
    public ParticleEffect(double x, double y, double vx, double vy, Color color, double size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.life = 1.0;
        this.size = size;
    }
    
    public boolean update(double deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;
        vy += 300 * deltaTime; // Gravity
        life -= deltaTime * 2; // Fade out
        return life > 0;
    }
    
    public void draw(GraphicsContext gc) {
        if (life <= 0) return;
        
        Color drawColor = Color.color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            life
        );
        
        gc.setFill(drawColor);
        gc.fillOval(x - size/2, y - size/2, size, size);
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
}

