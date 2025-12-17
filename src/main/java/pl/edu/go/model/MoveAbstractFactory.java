package pl.edu.go.model;

/**
 * Interfejs fabryki odpowiedzialnej za tworzenie ruchów.
 */
public interface MoveAbstractFactory {

    /**
     * Tworzy ruch postawienia kamienia.
     *
     * @param pos pozycja na planszy
     * @param c kolor gracza
     * @return ruch typu PLACE
     */
    Move createPlace(Point pos, Color c);

    /**
     * Tworzy ruch spasowania.
     *
     * @param c kolor gracza
     * @return ruch typu PASS
     */
    Move createPass(Color c);

    /**
     * Tworzy ruch poddania gry.
     *
     * @param c kolor gracza
     * @return ruch typu RESIGN
     */
    Move createResign(Color c);
}
