package pl.edu.go.model;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class MoveFactoryTest {

    private MoveAbstractFactory factory;

    @Before
    public void setUp() {
        factory = new MoveFactory();
    }

    @Test
    public void createPlace_createsPlaceMove() {
        Point p = new Point(2, 3);
        Move move = factory.createPlace(p, Color.BLACK);

        assertNotNull(move);
        assertEquals(Move.Type.PLACE, move.getType());
        assertEquals(Color.BLACK, move.getColor());
        assertEquals(p, move.getPos());
    }

    @Test
    public void createPass_createsPassMove() {
        Move move = factory.createPass(Color.WHITE);

        assertNotNull(move);
        assertEquals(Move.Type.PASS, move.getType());
        assertEquals(Color.WHITE, move.getColor());
        assertNull(move.getPos());
    }

    @Test
    public void createResign_createsResignMove() {
        Move move = factory.createResign(Color.BLACK);

        assertNotNull(move);
        assertEquals(Move.Type.RESIGN, move.getType());
        assertEquals(Color.BLACK, move.getColor());
        assertNull(move.getPos());
    }

    @Test
    public void factoryCreatesNewMoveEachTime() {
        Move m1 = factory.createPass(Color.BLACK);
        Move m2 = factory.createPass(Color.BLACK);

        assertNotSame(m1, m2);
    }
}
