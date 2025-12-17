package pl.edu.go.model;

/**
 * Klasa reprezentująca punkt na płaszczyźnie 2D
 * o współrzędnych całkowitych (x, y).
 * Obiekt klasy Point jest niemutowalny.
 */
public class Point {

    /** Współrzędna pozioma punktu. */
    public final int x;

    /** Współrzędna pionowa punktu. */
    public final int y;

    /**
     * Tworzy nowy punkt o podanych współrzędnych.
     *
     * @param x współrzędna pozioma
     * @param y współrzędna pionowa
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sprawdza, czy dwa punkty są równe.
     * Punkty są równe, jeśli mają takie same współrzędne x i y.
     *
     * @param o obiekt do porównania
     * @return true jeśli punkty są równe, w przeciwnym razie false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    /**
     * Zwraca kod hash punktu zgodny z metodą equals.
     *
     * @return kod hash punktu
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    /**
     * Zwraca tekstową reprezentację punktu
     * w postaci (x,y).
     *
     * @return napis reprezentujący punkt
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
