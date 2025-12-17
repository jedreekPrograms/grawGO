package pl.edu.go.model;

/**
 * Klasa reprezentująca ruch wykonany przez gracza.
 * Ruch może polegać na postawieniu kamienia,
 * spasowaniu lub poddaniu się.
 */
public class Move {

    /**
     * Typ ruchu w grze.
     */
    public enum Type {
        PLACE,   // postawienie kamienia
        PASS,    // pominięcie ruchu
        RESIGN   // poddanie gry
    }

    /** Typ ruchu. */
    private final Type type;

    /** Pozycja ruchu (null dla PASS i RESIGN). */
    private final Point pos;

    /** Kolor gracza wykonującego ruch. */
    private final Color color;

    /**
     * Tworzy nowy ruch.
     *
     * @param type typ ruchu
     * @param pos pozycja ruchu lub null
     * @param color kolor gracza
     */
    public Move(Type type, Point pos, Color color) {
        this.type = type;
        this.pos = pos;
        this.color = color;
    }

    /** @return typ ruchu */
    public Type getType() {
        return type;
    }

    /** @return pozycja ruchu lub null */
    public Point getPos() {
        return pos;
    }

    /** @return kolor gracza */
    public Color getColor() {
        return color;
    }

    /**
     * Zwraca tekstową reprezentację ruchu.
     *
     * @return opis ruchu
     */
    @Override
    public String toString() {
        switch (type) {
            case PLACE: return color + " -> " + pos;
            case PASS: return color + " passes";
            case RESIGN: return color + " resigns";
            default: return "";
        }
    }
}
