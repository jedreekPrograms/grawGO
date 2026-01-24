package pl.edu.go.model;

/**
 * Klasa pomocnicza do budowania obiektu Board.
 */
public class BoardBuilder {

    /** Domyślny rozmiar planszy. */
    private int size = 9;

    /**
     * Ustawia rozmiar planszy.
     *
     * @param size rozmiar planszy
     * @return bieżący obiekt buildera
     */
    public BoardBuilder size(int size) {
        this.size = size;
        return this;
    }

    /**
     * Tworzy nową planszę.
     *
     * @return obiekt Board
     */
    public Board build() {
        return new Board(size);
    }
}
