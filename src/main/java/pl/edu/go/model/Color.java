package pl.edu.go.model;

public enum Color {
    BLACK, WHITE, EMPTY;

    public Color opponent() {
        if (this == BLACK) {
            return WHITE;
        } else if (this == WHITE) {
            return BLACK;
        } else {
            return EMPTY;
        }
    }
}
