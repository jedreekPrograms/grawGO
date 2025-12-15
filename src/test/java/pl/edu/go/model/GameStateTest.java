package pl.edu.go.model;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class GameStateTest {

    private GameState gameState;
    private MoveFactory moveFactory;

    @Before
    public void setUp() {
        gameState = new GameState(5);
        moveFactory = new MoveFactory();
    }

    @Test
    public void initialState_isCorrect() {
        assertEquals(Color.BLACK, gameState.getNextToMove());
        assertEquals(0, gameState.getBlackCaptures());
        assertEquals(0, gameState.getWhiteCaptures());
        assertFalse(gameState.isGameOver());
        assertNotNull(gameState.getBoard());
    }

    @Test
    public void applyMove_placeStone_switchesTurn() {
        Move move = moveFactory.createPlace(new Point(2, 2), Color.BLACK);

        boolean result = gameState.applyMove(move);

        assertTrue(result);
        assertEquals(Color.WHITE, gameState.getNextToMove());
        assertEquals(Color.BLACK, gameState.getBoard().get(2, 2));
    }

    @Test
    public void applyMove_wrongColorRejected() {
        Move move = moveFactory.createPlace(new Point(2, 2), Color.WHITE);

        boolean result = gameState.applyMove(move);

        assertFalse(result);
        assertEquals(Color.BLACK, gameState.getNextToMove());
        assertEquals(Color.EMPTY, gameState.getBoard().get(2, 2));
    }

    @Test
    public void applyMove_pass_switchesTurn() {
        Move pass = moveFactory.createPass(Color.BLACK);

        boolean result = gameState.applyMove(pass);

        assertTrue(result);
        assertEquals(Color.WHITE, gameState.getNextToMove());
    }

    @Test
    public void applyMove_passWrongColorRejected() {
        Move pass = moveFactory.createPass(Color.WHITE);

        boolean result = gameState.applyMove(pass);

        assertFalse(result);
        assertEquals(Color.BLACK, gameState.getNextToMove());
    }

    @Test
    public void applyMove_resign_endsGame() {
        Move resign = moveFactory.createResign(Color.BLACK);

        boolean result = gameState.applyMove(resign);

        assertTrue(result);
        assertTrue(gameState.isGameOver());
    }

    @Test
    public void applyMove_afterGameOver_isRejected() {
        gameState.applyMove(moveFactory.createResign(Color.BLACK));

        boolean result = gameState.applyMove(
                moveFactory.createPlace(new Point(1, 1), Color.WHITE)
        );

        assertFalse(result);
    }

    @Test
    public void applyMove_captureUpdatesBlackCaptures() {
        // przygotowanie pozycji do bicia
        gameState.applyMove(moveFactory.createPlace(new Point(0, 1), Color.BLACK));
        gameState.applyMove(moveFactory.createPlace(new Point(1, 1), Color.WHITE));

        gameState.applyMove(moveFactory.createPlace(new Point(1, 0), Color.BLACK));
        gameState.applyMove(moveFactory.createPass(Color.WHITE));

        gameState.applyMove(moveFactory.createPlace(new Point(2, 1), Color.BLACK));
        gameState.applyMove(moveFactory.createPass(Color.WHITE));

        gameState.applyMove(moveFactory.createPlace(new Point(1, 2), Color.BLACK));

        assertEquals(1, gameState.getBlackCaptures());
        assertEquals(Color.EMPTY, gameState.getBoard().get(1, 1));
    }

    @Test
    public void applyMove_illegalPlacementRejected() {
        gameState.applyMove(moveFactory.createPlace(new Point(2, 2), Color.BLACK));

        boolean result = gameState.applyMove(
                moveFactory.createPlace(new Point(2, 2), Color.WHITE)
        );

        assertFalse(result);
        assertEquals(Color.WHITE, gameState.getNextToMove());
    }
}
