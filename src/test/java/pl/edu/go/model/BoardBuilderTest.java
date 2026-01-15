package pl.edu.go.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testy jednostkowe klasy BoardBuilder.
 */
public class BoardBuilderTest {

    @Test
    public void build_withoutSize_usesDefaultSize() {
        BoardBuilder builder = new BoardBuilder();

        Board board = builder.build();

        assertNotNull(board);
        assertEquals(19, board.getSize());
    }

    @Test
    public void build_withCustomSize_createsBoardWithGivenSize() {
        BoardBuilder builder = new BoardBuilder();

        Board board = builder.size(9).build();

        assertNotNull(board);
        assertEquals(9, board.getSize());
    }

    @Test
    public void size_returnsSameBuilder_forChaining() {
        BoardBuilder builder = new BoardBuilder();

        BoardBuilder returned = builder.size(13);

        assertSame(builder, returned);
    }

    @Test
    public void multipleSizeCalls_lastOneWins() {
        BoardBuilder builder = new BoardBuilder();

        Board board = builder.size(9).size(5).build();

        assertEquals(5, board.getSize());
    }

    @Test
    public void build_createsNewBoardInstanceEachTime() {
        BoardBuilder builder = new BoardBuilder().size(9);

        Board board1 = builder.build();
        Board board2 = builder.build();

        assertNotSame(board1, board2);
        assertEquals(board1.getSize(), board2.getSize());
    }
}
