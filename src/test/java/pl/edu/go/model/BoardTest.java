package pl.edu.go.model;

import org.junit.Before;
import org.junit.Test;


import java.util.Set;

import static org.junit.Assert.*;

public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board(5);
    }

    @Test
    public void constructor_initializesEmptyBoard() {
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

    @Test
    public void placeStone_placesStoneCorrectly() {
        int result = board.placeStone(Color.BLACK, 2, 2);

        assertEquals(0, result);
        assertEquals(Color.BLACK, board.get(2, 2));
    }

    @Test
    public void placeStone_onOccupiedField_returnsMinusOne() {
        board.placeStone(Color.BLACK, 2, 2);
        int result = board.placeStone(Color.WHITE, 2, 2);

        assertEquals(-1, result);
        assertEquals(Color.BLACK, board.get(2, 2));
    }

    @Test
    public void placeStone_outOfBounds_returnsMinusOne() {
        assertEquals(-1, board.placeStone(Color.BLACK, -1, 0));
        assertEquals(-1, board.placeStone(Color.BLACK, 5, 5));
    }

    @Test
    public void getGroup_singleStone() {
        board.placeStone(Color.BLACK, 1, 1);

        Set<Point> group = board.getGroup(1, 1);

        assertEquals(1, group.size());
        assertTrue(group.contains(new Point(1, 1)));
    }

    @Test
    public void getGroup_connectedStones() {
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
    public void getLiberties_correctCount() {
        board.placeStone(Color.BLACK, 2, 2);

        Set<Point> group = board.getGroup(2, 2);
        Set<Point> liberties = board.getLiberties(group);

        assertEquals(4, liberties.size());
    }

    @Test
    public void captureSingleStone() {
        board.placeStone(Color.WHITE, 2, 2);

        board.placeStone(Color.BLACK, 1, 2);
        board.placeStone(Color.BLACK, 3, 2);
        board.placeStone(Color.BLACK, 2, 1);
        int captured = board.placeStone(Color.BLACK, 2, 3);

        assertEquals(1, captured);
        assertEquals(Color.EMPTY, board.get(2, 2));
    }

    @Test
    public void suicideMove_isRejected() {
        board.placeStone(Color.WHITE, 1, 2);
        board.placeStone(Color.WHITE, 3, 2);
        board.placeStone(Color.WHITE, 2, 1);
        board.placeStone(Color.WHITE, 2, 3);

        int result = board.placeStone(Color.BLACK, 2, 2);

        assertEquals(-1, result);
        assertEquals(Color.EMPTY, board.get(2, 2));
    }

    @Test
    public void computeHash_sameBoards_haveSameHash() {
        Board b1 = new Board(3);
        Board b2 = new Board(3);

        b1.placeStone(Color.BLACK, 1, 1);
        b2.placeStone(Color.BLACK, 1, 1);

        assertEquals(b1.computeHash(), b2.computeHash());
    }

    @Test
    public void computeHash_differentBoards_haveDifferentHash() {
        Board b1 = new Board(3);
        Board b2 = new Board(3);

        b1.placeStone(Color.BLACK, 1, 1);
        b2.placeStone(Color.WHITE, 1, 1);

        assertNotEquals(b1.computeHash(), b2.computeHash());
    }
}
