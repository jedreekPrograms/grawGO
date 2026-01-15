package pl.edu.go.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testy jednostkowe klasy MoveFactory.
 */
public class MoveFactoryTest {

    private MoveAbstractFactory factory;

    @Before
    public void setUp() {
        factory = new MoveFactory();
    }

    // =========================
    // PLACE
    // =========================

    @Test
    public void createPlace_createsPlaceMoveWithCorrectFields() {
        Point p = new Point(4, 7);

        Move move = factory.createPlace(p, Color.BLACK);

        assertNotNull(move);
        assertEquals(Move.Type.PLACE, move.getType());
        assertEquals(p, move.getPos());
        assertEquals(Color.BLACK, move.getColor());
    }

    // =========================
    // PASS
    // =========================

    @Test
    public void createPass_createsPassMoveWithNullPosition() {
        Move move = factory.createPass(Color.WHITE);

        assertNotNull(move);
        assertEquals(Move.Type.PASS, move.getType());
        assertNull(move.getPos());
        assertEquals(Color.WHITE, move.getColor());
    }

    // =========================
    // RESIGN
    // =========================

    @Test
    public void createResign_createsResignMoveWithNullPosition() {
        Move move = factory.createResign(Color.BLACK);

        assertNotNull(move);
        assertEquals(Move.Type.RESIGN, move.getType());
        assertNull(move.getPos());
        assertEquals(Color.BLACK, move.getColor());
    }

    // =========================
    // POLYMORPHISM
    // =========================

    @Test
    public void factory_isInstanceOfMoveAbstractFactory() {
        assertTrue(factory instanceof MoveAbstractFactory);
    }
}
