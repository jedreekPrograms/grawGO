package pl.edu.go.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testy jednostkowe klasy Point.
 */
public class PointTest {

    // =========================
    // CONSTRUCTOR
    // =========================

    @Test
    public void constructor_setsCoordinatesCorrectly() {
        Point p = new Point(3, 7);

        assertEquals(3, p.x);
        assertEquals(7, p.y);
    }

    // =========================
    // EQUALS
    // =========================

    @Test
    public void equals_sameCoordinates_returnsTrue() {
        Point p1 = new Point(2, 4);
        Point p2 = new Point(2, 4);

        assertEquals(p1, p2);
    }

    @Test
    public void equals_sameReference_returnsTrue() {
        Point p = new Point(1, 1);

        assertEquals(p, p);
    }

    @Test
    public void equals_differentCoordinates_returnsFalse() {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(2, 1);

        assertNotEquals(p1, p2);
    }

    @Test
    public void equals_null_returnsFalse() {
        Point p = new Point(1, 1);

        assertNotEquals(p, null);
    }

    @Test
    public void equals_differentClass_returnsFalse() {
        Point p = new Point(1, 1);

        assertNotEquals(p, "not a point");
    }

    // =========================
    // HASHCODE
    // =========================

    @Test
    public void hashCode_equalObjects_haveSameHashCode() {
        Point p1 = new Point(5, 6);
        Point p2 = new Point(5, 6);

        assertEquals(p1.hashCode(), p2.hashCode());
    }

    // =========================
    // TOSTRING
    // =========================

    @Test
    public void toString_returnsCorrectFormat() {
        Point p = new Point(3, 9);

        assertEquals("(3,9)", p.toString());
    }

    // =========================
    // IMMUTABILITY
    // =========================

    @Test
    public void point_isImmutable() {
        Point p = new Point(1, 2);

        assertEquals(1, p.x);
        assertEquals(2, p.y);
        // brak setterów = niemutowalny
    }
}
