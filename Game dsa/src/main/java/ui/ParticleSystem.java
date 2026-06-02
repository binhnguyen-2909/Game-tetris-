package ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Quản lý hệ thống particle effects
 */
public class ParticleSystem {
    private ArrayList<ParticleEffect> particles;
    private Random random;
    
    public ParticleSystem() {
        particles = new ArrayList<>();
        random = new Random();
    }
    
    /**
     * Tạo particle explosion khi xóa dòng
     */
    public void createRowClearExplosion(double x, double y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 50 + random.nextDouble() * 100;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - 50;
            double size = 3 + random.nextDouble() * 5;
            particles.add(new ParticleEffect(x, y, vx, vy, color, size));
        }
    }
    
    /**
     * Tạo combo effect
     */
    public void createComboEffect(double x, double y, Color color, int combo) {
        for (int i = 0; i < combo * 5; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 30 + random.nextDouble() * 50;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - 30;
            double size = 2 + random.nextDouble() * 4;
            particles.add(new ParticleEffect(x, y, vx, vy, color, size));
        }
    }
    
    /**
     * Update và vẽ tất cả particles
     */
    public void updateAndDraw(GraphicsContext gc, double deltaTime) {
        Iterator<ParticleEffect> it = particles.iterator();
        while (it.hasNext()) {
            ParticleEffect particle = it.next();
            if (!particle.update(deltaTime)) {
                it.remove();
            } else {
                particle.draw(gc);
            }
        }
    }
    
    public void clear() {
        particles.clear();
    }
    
    public int getParticleCount() {
        return particles.size();
    }
}

