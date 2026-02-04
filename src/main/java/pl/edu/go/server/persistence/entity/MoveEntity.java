package pl.edu.go.server.persistence.entity;

import jakarta.persistence.*;
import pl.edu.go.model.Color;

/**
 * Encja JPA reprezentująca pojedynczy ruch w grze Go.
 * <p>
 * Przechowuje informacje o numerze ruchu, jego współrzędnych,
 * kolorze gracza wykonującego ruch, typie ruchu
 * oraz powiązanej grze.
 * </p>
 */
@Entity
public class MoveEntity {

    /**
     * Unikalny identyfikator ruchu.
     * Generowany automatycznie przez mechanizm JPA.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Numer ruchu w danej grze (kolejność wykonania).
     */
    private int moveNumber;

    /**
     * Współrzędna X ruchu na planszy.
     * Może być {@code null} dla ruchów specjalnych (np. PASS).
     */
    private Integer x;

    /**
     * Współrzędna Y ruchu na planszy.
     * Może być {@code null} dla ruchów specjalnych (np. PASS).
     */
    private Integer y;

    /**
     * Kolor gracza wykonującego ruch.
     */
    @Enumerated(EnumType.STRING)
    private Color color;

    /**
     * Typ ruchu (np. NORMAL, PASS, RESIGN).
     */
    @Enumerated(EnumType.STRING)
    private MoveType type;

    /**
     * Gra, do której należy dany ruch.
     * Relacja wiele ruchów do jednej gry.
     */
    @ManyToOne
    private GameEntity game;

    /**
     * Ustawia grę, do której należy ruch.
     *
     * @param g encja gry
     */
    public void setGame(GameEntity g) {
        this.game = g;
    }

    /**
     * Ustawia numer ruchu.
     *
     * @param nr numer ruchu w grze
     */
    public void setMoveNumber(int nr) {
        this.moveNumber = nr;
    }

    /**
     * Ustawia współrzędną X ruchu.
     *
     * @param x współrzędna X
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * Ustawia współrzędną Y ruchu.
     *
     * @param y współrzędna Y
     */
    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * Ustawia kolor gracza wykonującego ruch.
     *
     * @param c kolor gracza
     */
    public void setColor(Color c) {
        this.color = c;
    }

    /**
     * Ustawia typ ruchu.
     *
     * @param type typ ruchu
     */
    public void setType(MoveType type) {
        this.type = type;
    }

    /**
     * Zwraca typ ruchu.
     *
     * @return typ ruchu
     */
    public MoveType getType() {
        return type;
    }

    /**
     * Zwraca współrzędną X ruchu.
     *
     * @return współrzędna X
     */
    public int getX() {
        return x;
    }

    /**
     * Zwraca współrzędną Y ruchu.
     *
     * @return współrzędna Y
     */
    public int getY() {
        return y;
    }

    /**
     * Zwraca kolor gracza wykonującego ruch.
     *
     * @return kolor gracza
     */
    public Color getColor() {
        return color;
    }
}
