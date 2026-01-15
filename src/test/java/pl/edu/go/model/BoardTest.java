package pl.edu.go.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Testy jednostkowe klasy Board.
 *
 * Testujemy WYŁĄCZNIE:
 * - logikę planszy Go
 * - bez GameState
 * - bez tur
 */
public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board(5);
    }

    // =========================
    // PODSTAWY
    // =========================

    @Test
    public void newBoard_isEmpty() {
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                assertEquals(Color.EMPTY, board.get(x, y));
            }
        }
    }

    @Test
    public void get_outOfBounds_returnsNull() {
        assertNull(board.get(-1, 0));
        assertNull(board.get(0, -1));
        assertNull(board.get(5, 0));
        assertNull(board.get(0, 5));
    }

    // =========================
    // PLACE STONE
    // =========================

    @Test
    public void placeStone_onEmptyField_placesStone() {
        int captured = board.placeStone(Color.BLACK, 2, 2);

        assertEquals(0, captured);
        assertEquals(Color.BLACK, board.get(2, 2));
    }

    @Test
    public void placeStone_onOccupiedField_isIllegal() {
        board.placeStone(Color.BLACK, 2, 2);

        int result = board.placeStone(Color.WHITE, 2, 2);

        assertEquals(-1, result);
        assertEquals(Color.BLACK, board.get(2, 2));
    }

    @Test
    public void placeStone_outOfBounds_isIllegal() {
        assertEquals(-1, board.placeStone(Color.BLACK, -1, 0));
        assertEquals(-1, board.placeStone(Color.BLACK, 5, 5));
    }

    // =========================
    // CAPTURE
    // =========================

    @Test
    public void placeStone_capturesSingleStone() {
        board.placeStone(Color.WHITE, 1, 1);

        board.placeStone(Color.BLACK, 0, 1);
        board.placeStone(Color.BLACK, 2, 1);
        board.placeStone(Color.BLACK, 1, 0);

        int captured = board.placeStone(Color.BLACK, 1, 2);

        assertEquals(1, captured);
        assertEquals(Color.EMPTY, board.get(1, 1));
    }

    @Test
    public void placeStone_capturesGroup() {
        board.placeStone(Color.WHITE, 1, 1);
        board.placeStone(Color.WHITE, 1, 2);

        board.placeStone(Color.BLACK, 0, 1);
        board.placeStone(Color.BLACK, 0, 2);
        board.placeStone(Color.BLACK, 1, 0);
        board.placeStone(Color.BLACK, 1, 3);
        board.placeStone(Color.BLACK, 2, 1);

        int captured = board.placeStone(Color.BLACK, 2, 2);

        assertEquals(2, captured);
        assertEquals(Color.EMPTY, board.get(1, 1));
        assertEquals(Color.EMPTY, board.get(1, 2));
    }

    // =========================
    // SUICIDE
    // =========================

    @Test
    public void placeStone_suicideWithoutCapture_isIllegal() {
        board.placeStone(Color.BLACK, 0, 1);
        board.placeStone(Color.BLACK, 1, 0);
        board.placeStone(Color.BLACK, 2, 1);
        board.placeStone(Color.BLACK, 1, 2);

        int result = board.placeStone(Color.WHITE, 1, 1);

        assertEquals(-1, result);
        assertEquals(Color.EMPTY, board.get(1, 1));
    }

    @Test
    public void placeStone_suicideWithCapture_isAllowed() {
        board.placeStone(Color.WHITE, 1, 0);
        board.placeStone(Color.WHITE, 0, 1);
        board.placeStone(Color.WHITE, 2, 1);
        board.placeStone(Color.BLACK, 1, 1);

        int result = board.placeStone(Color.WHITE, 1, 2);

        assertEquals(1, result);
        assertEquals(Color.WHITE, board.get(1, 2));
    }

    // =========================
    // GROUP & LIBERTIES
    // =========================

    @Test
    public void getGroup_returnsConnectedStones() {
        board.placeStone(Color.BLACK, 1, 1);
        board.placeStone(Color.BLACK, 1, 2);
        board.placeStone(Color.BLACK, 2, 2);

        Set<Point> group = board.getGroup(1, 1);

        assertEquals(3, group.size());
        assertTrue(group.contains(new Point(1, 1)));
        assertTrue(group.contains(new Point(1, 2)));
        assertTrue(group.contains(new Point(2, 2)));
    }

    @Test
    public void getLiberties_returnsCorrectCount() {
        board.placeStone(Color.BLACK, 1, 1);

        Set<Point> group = board.getGroup(1, 1);
        Set<Point> liberties = board.getLiberties(group);

        assertEquals(4, liberties.size());
    }

    // =========================
    // HASH / KO
    // =========================

    @Test
    public void computeHash_sameBoards_haveSameHash() {
        Board b1 = new Board(5);
        Board b2 = new Board(5);

        b1.placeStone(Color.BLACK, 2, 2);
        b2.placeStone(Color.BLACK, 2, 2);

        assertEquals(b1.computeHash(), b2.computeHash());
    }

    @Test
    public void computeHash_differentBoards_haveDifferentHash() {
        Board b1 = new Board(5);
        Board b2 = new Board(5);

        b1.placeStone(Color.BLACK, 2, 2);
        b2.placeStone(Color.WHITE, 2, 2);

        assertNotEquals(b1.computeHash(), b2.computeHash());
    }

    // =========================
    // COPY CONSTRUCTOR
    // =========================

    @Test
    public void copyConstructor_createsIndependentCopy() {
        board.placeStone(Color.BLACK, 2, 2);

        Board copy = new Board(board);
        copy.placeStone(Color.WHITE, 3, 3);

        assertEquals(Color.BLACK, board.get(2, 2));
        assertEquals(Color.EMPTY, board.get(3, 3));
    }

    // =========================
    // STRING REPRESENTATION
    // =========================

    @Test
    public void toSingleLineString_returnsCorrectFormat() {
        board.placeStone(Color.BLACK, 0, 0);
        board.placeStone(Color.WHITE, 1, 0);

        String s = board.toSingleLineString();

        assertTrue(s.startsWith("BW"));
        assertEquals(4, s.chars().filter(c -> c == '/').count());
    }
}
