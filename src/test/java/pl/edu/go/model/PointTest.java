package pl.edu.go.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PointTest {

    @Test
    public void constructor_setsCoordinates() {
        Point p = new Point(3, 7);

        assertEquals(3, p.x);
        assertEquals(7, p.y);
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Point p = new Point(1, 2);

        assertTrue(p.equals(p));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        Point p1 = new Point(4, 5);
        Point p2 = new Point(4, 5);

        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
    }

    @Test
    public void equals_differentX_returnsFalse() {
        Point p1 = new Point(4, 5);
        Point p2 = new Point(3, 5);

        assertFalse(p1.equals(p2));
    }

    @Test
    public void equals_differentY_returnsFalse() {
        Point p1 = new Point(4, 5);
        Point p2 = new Point(4, 6);

        assertFalse(p1.equals(p2));
    }

    @Test
    public void equals_null_returnsFalse() {
        Point p = new Point(1, 1);

        assertFalse(p.equals(null));
    }

    @Test
    public void equals_differentClass_returnsFalse() {
        Point p = new Point(1, 1);

        assertFalse(p.equals("not a point"));
    }

    @Test
    public void hashCode_equalObjects_haveSameHash() {
        Point p1 = new Point(2, 3);
        Point p2 = new Point(2, 3);

        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void hashCode_differentObjects_canHaveDifferentHash() {
        Point p1 = new Point(2, 3);
        Point p2 = new Point(3, 2);

        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void toString_formatIsCorrect() {
        Point p = new Point(5, 6);

        assertEquals("(5,6)", p.toString());
    }
}
