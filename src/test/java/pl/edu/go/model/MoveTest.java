package pl.edu.go.model;

import org.junit.Test;


import static org.junit.Assert.*;

public class MoveTest {

    @Test
    public void createPlaceMove_hasCorrectFields() {
        Point p = new Point(3, 4);
        Move move = new Move(Move.Type.PLACE, p, Color.BLACK);

        assertEquals(Move.Type.PLACE, move.getType());
        assertEquals(Color.BLACK, move.getColor());
        assertEquals(p, move.getPos());
    }

    @Test
    public void createPassMove_hasNullPosition() {
        Move move = new Move(Move.Type.PASS, null, Color.WHITE);

        assertEquals(Move.Type.PASS, move.getType());
        assertEquals(Color.WHITE, move.getColor());
        assertNull(move.getPos());
    }

    @Test
    public void createResignMove_hasNullPosition() {
        Move move = new Move(Move.Type.RESIGN, null, Color.BLACK);

        assertEquals(Move.Type.RESIGN, move.getType());
        assertEquals(Color.BLACK, move.getColor());
        assertNull(move.getPos());
    }

    @Test
    public void toString_placeMove() {
        Point p = new Point(1, 2);
        Move move = new Move(Move.Type.PLACE, p, Color.BLACK);

        String text = move.toString();

        assertTrue(text.contains("BLACK"));
        assertTrue(text.contains("->"));
        assertTrue(text.contains(p.toString()));
    }

    @Test
    public void toString_passMove() {
        Move move = new Move(Move.Type.PASS, null, Color.WHITE);

        assertEquals("WHITE passes", move.toString());
    }

    @Test
    public void toString_resignMove() {
        Move move = new Move(Move.Type.RESIGN, null, Color.BLACK);

        assertEquals("BLACK resigns", move.toString());
    }
}
