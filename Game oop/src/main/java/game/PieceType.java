package game;

public enum PieceType {
    // Tetris-style pieces
    I(new int[][] {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}}),
    O(new int[][] {{1,1}, {1,1}}),
    T(new int[][] {{0,1,0}, {1,1,1}, {0,0,0}}),
    L(new int[][] {{1,0,0}, {1,1,1}, {0,0,0}}),
    J(new int[][] {{0,0,1}, {1,1,1}, {0,0,0}}),
    S(new int[][] {{0,1,1}, {1,1,0}, {0,0,0}}),
    Z(new int[][] {{1,1,0}, {0,1,1}, {0,0,0}});

    private final int[][] shape;

    PieceType(int[][] shape) {
        this.shape = shape;
    }

    public int[][] getShape() {
        return copyShape(shape);
    }

    private static int[][] copyShape(int[][] shape) {
        int[][] copy = new int[shape.length][];
        for (int i = 0; i < shape.length; i++) {
            copy[i] = shape[i].clone();
        }
        return copy;
    }
}
