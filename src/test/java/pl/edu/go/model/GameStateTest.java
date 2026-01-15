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

    // =========================
    // INIT
    // =========================

    @Test
    public void initialState_isCorrect() {
        assertEquals(Color.BLACK, gameState.getNextToMove());
        assertEquals(0, gameState.getBlackCaptures());
        assertEquals(0, gameState.getWhiteCaptures());
        assertEquals(GameState.Status.PLAYING, gameState.getStatus());
        assertNotNull(gameState.getBoard());
    }

    // =========================
    // PLACE
    // =========================

    @Test
    public void applyMove_placeStone_placesStone() {
        Move move = moveFactory.createPlace(new Point(2, 2), Color.BLACK);

        boolean result = gameState.applyMove(move);

        assertTrue(result);
        assertEquals(Color.BLACK, gameState.getBoard().get(2, 2));
    }

    @Test
    public void applyMove_placeWrongColorRejected() {
        Move move = moveFactory.createPlace(new Point(2, 2), Color.WHITE);

        boolean result = gameState.applyMove(move);

        assertFalse(result);
        assertEquals(Color.EMPTY, gameState.getBoard().get(2, 2));
    }

    @Test
    public void applyMove_illegalPlacementRejected() {
        gameState.applyMove(moveFactory.createPlace(new Point(2, 2), Color.BLACK));

        boolean result = gameState.applyMove(
                moveFactory.createPlace(new Point(2, 2), Color.BLACK)
        );

        assertFalse(result);
    }

    // =========================
    // PASS
    // =========================

    @Test
    public void applyMove_singlePass_keepsGamePlaying() {
        boolean result = gameState.applyMove(
                moveFactory.createPass(Color.BLACK)
        );

        assertTrue(result);
        assertEquals(GameState.Status.PLAYING, gameState.getStatus());
        assertEquals(Move.Type.PASS, gameState.getLastMoveType());
    }

    @Test
    public void applyMove_twoPasses_switchesToStopped() {
        gameState.applyMove(moveFactory.createPass(Color.BLACK));
        gameState.applyMove(moveFactory.createPass(Color.BLACK));

        assertEquals(GameState.Status.STOPPED, gameState.getStatus());
    }

    // =========================
    // RESIGN
    // =========================

    @Test
    public void applyMove_resign_endsGame() {
        boolean result = gameState.applyMove(
                moveFactory.createResign(Color.BLACK)
        );

        assertTrue(result);
        assertEquals(GameState.Status.FINISHED, gameState.getStatus());
        assertTrue(gameState.isGameOver());
        assertNotNull(gameState.getFinalResult());
    }

    @Test
    public void applyMove_afterGameFinishedRejected() {
        gameState.applyMove(moveFactory.createResign(Color.BLACK));

        boolean result = gameState.applyMove(
                moveFactory.createPlace(new Point(1, 1), Color.BLACK)
        );

        assertFalse(result);
    }

    // =========================
    // CAPTURE
    // =========================

    @Test
    public void capture_increasesBlackCaptures() {
        gameState.applyMove(moveFactory.createPlace(new Point(0, 1), Color.BLACK));
        gameState.applyMove(moveFactory.createPlace(new Point(1, 1), Color.WHITE));

        gameState.applyMove(moveFactory.createPlace(new Point(1, 0), Color.BLACK));
        gameState.applyMove(moveFactory.createPlace(new Point(2, 1), Color.BLACK));
        gameState.applyMove(moveFactory.createPlace(new Point(1, 2), Color.BLACK));

        assertEquals(1, gameState.getBlackCaptures());
        assertEquals(Color.EMPTY, gameState.getBoard().get(1, 1));
    }

    // =========================
    // STOPPED / DEAD STONES
    // =========================

    @Test
    public void toggleDeadStone_onlyAllowedInStopped() {
        boolean result = gameState.toggleDeadStone(new Point(0, 0));

        assertFalse(result);
    }

    @Test
    public void confirmEndGame_finishesGame() {
        gameState.setStatus(GameState.Status.STOPPED);

        gameState.confirmEndGame();

        assertEquals(GameState.Status.FINISHED, gameState.getStatus());
        assertNotNull(gameState.getFinalResult());
    }

    // =========================
    // ACCEPT
    // =========================

    @Test
    public void accept_bothPlayersEndsScoringPhase() {
        gameState.setStatus(GameState.Status.STOPPED);

        assertFalse(gameState.accept(Color.BLACK));
        assertTrue(gameState.accept(Color.WHITE));
    }
}
