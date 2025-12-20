package game;

import java.util.Random;

public class Piece {
    private int[][] shape;
    private PieceType type;
    private int x, y;
    private boolean wasRotated = false; // Track việc xoay cho T-Spin bonus
    private static final Random random = new Random();

    public Piece() {
        this.type = PieceType.values()[random.nextInt(PieceType.values().length)];
        this.shape = type.getShape();
        this.x = 3; // Vị trí bắt đầu
        this.y = 0;
    }

    public void rotateClockwise() {
        int[][] rotated = rotateMatrix(shape);
        this.shape = rotated;
        this.wasRotated = true; // Đánh dấu đã xoay
    }

    public void rotateCounterClockwise() {
        // Xoay ngược chiều kim đồng hồ = xoay 3 lần theo chiều kim đồng hồ
        for (int i = 0; i < 3; i++) {
            rotateClockwise();
        }
    }

    private int[][] rotateMatrix(int[][] matrix) {
        int size = matrix.length;
        int[][] rotated = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rotated[j][size - 1 - i] = matrix[i][j];
            }
        }
        return rotated;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public void moveDown() {
        y++;
    }

    public int[][] getShape() {
        return shape;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public PieceType getType() {
        return type;
    }

    public boolean wasRotated() {
        return wasRotated;
    }

    public void resetRotationFlag() {
        this.wasRotated = false;
    }
}
