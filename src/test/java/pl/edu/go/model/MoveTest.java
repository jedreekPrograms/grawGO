package pl.edu.go.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testy jednostkowe klasy Move.
 */
public class MoveTest {

    // =========================
    // PLACE
    // =========================

    @Test
    public void constructor_place_setsAllFieldsCorrectly() {
        Point p = new Point(3, 4);

        Move move = new Move(Move.Type.PLACE, p, Color.BLACK);

        assertEquals(Move.Type.PLACE, move.getType());
        assertEquals(p, move.getPos());
        assertEquals(Color.BLACK, move.getColor());
    }

    @Test
    public void toString_place_returnsCorrectFormat() {
        Move move = new Move(
                Move.Type.PLACE,
                new Point(2, 2),
                Color.WHITE
        );

        String text = move.toString();

        assertEquals("WHITE -> (2,2)", text);
    }

    // =========================
    // PASS
    // =========================

    @Test
    public void constructor_pass_hasNullPosition() {
        Move move = new Move(Move.Type.PASS, null, Color.BLACK);

        assertEquals(Move.Type.PASS, move.getType());
        assertNull(move.getPos());
        assertEquals(Color.BLACK, move.getColor());
    }

    @Test
    public void toString_pass_returnsCorrectText() {
        Move move = new Move(Move.Type.PASS, null, Color.WHITE);

        assertEquals("WHITE passes", move.toString());
    }

    // =========================
    // RESIGN
    // =========================

    @Test
    public void constructor_resign_hasNullPosition() {
        Move move = new Move(Move.Type.RESIGN, null, Color.BLACK);

        assertEquals(Move.Type.RESIGN, move.getType());
        assertNull(move.getPos());
        assertEquals(Color.BLACK, move.getColor());
    }

    @Test
    public void toString_resign_returnsCorrectText() {
        Move move = new Move(Move.Type.RESIGN, null, Color.BLACK);

        assertEquals("BLACK resigns", move.toString());
    }

    // =========================
    // GENERAL
    // =========================

    @Test
    public void move_isImmutable() {
        Point p = new Point(1, 1);
        Move move = new Move(Move.Type.PLACE, p, Color.BLACK);

        assertSame(p, move.getPos());
        assertEquals(Color.BLACK, move.getColor());
        assertEquals(Move.Type.PLACE, move.getType());
    }
}
