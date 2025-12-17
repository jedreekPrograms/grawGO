package pl.edu.go.model;

/**
 * Enum reprezentujący kolor gracza lub stan pola na planszy.
 * Możliwe wartości:
 * BLACK  – czarny gracz
 * WHITE  – biały gracz
 * EMPTY  – puste pole
 */
public enum Color {

    /** Czarny gracz lub pionek. */
    BLACK,

    /** Biały gracz lub pionek. */
    WHITE,

    /** Puste pole na planszy. */
    EMPTY;

    /**
     * Zwraca kolor przeciwnika dla aktualnego koloru.
     *
     * BLACK  -> WHITE
     * WHITE  -> BLACK
     * EMPTY  -> EMPTY
     *
     * @return kolor przeciwnika lub EMPTY, jeśli pole jest puste
     */
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
