package pl.edu.go.model;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Testy jednostkowe klasy TerritoryCalculator.
 */
public class TerritoryCalculatorTest {

    private TerritoryCalculator calculator;
    private GameState gameState;

    @Before
    public void setUp() {
        calculator = new TerritoryCalculator();
        gameState = new GameState(5);
    }

    // ==================================================
    // PODSTAWY
    // ==================================================

    @Test
    public void emptyBoard_whiteWinsByKomi() {
        TerritoryCalculator.GameResult result =
                calculator.calculateScore(gameState, new HashSet<>(), 6.5);

        assertEquals(0.0, result.blackScore(), 0.001);
        assertEquals(6.5, result.whiteScore(), 0.001);
        assertEquals("WHITE", result.winner());
    }

    // ==================================================
    // TERYTORIUM
    // ==================================================



    @Test
    public void territoryTouchingBothColors_isNeutral() {
        /*
         * B . W
         */
        gameState.getBoard().placeStone(Color.BLACK, 0, 1);
        gameState.getBoard().placeStone(Color.WHITE, 2, 1);

        TerritoryCalculator.GameResult result =
                calculator.calculateScore(gameState, Set.of(), 0);

        assertEquals(0.0, result.blackScore(), 0.001);
        assertEquals(0.0, result.whiteScore(), 0.001);
        assertEquals("WHITE", result.winner()); // remis → white (komi = 0)
    }

    // ==================================================
    // MARTWE KAMIENIE
    // ==================================================

    @Test
    public void deadWhiteStone_countsAsBlackCapture() {
        gameState.getBoard().placeStone(Color.WHITE, 2, 2);

        Set<Point> dead = new HashSet<>();
        dead.add(new Point(2, 2));

        TerritoryCalculator.GameResult result =
                calculator.calculateScore(gameState, dead, 0);

        assertEquals(1.0, result.blackScore(), 0.001);
        assertEquals(0.0, result.whiteScore(), 0.001);
        assertEquals("BLACK", result.winner());
    }

    @Test
    public void deadBlackStone_countsAsWhiteCapture() {
        gameState.getBoard().placeStone(Color.BLACK, 2, 2);

        Set<Point> dead = new HashSet<>();
        dead.add(new Point(2, 2));

        TerritoryCalculator.GameResult result =
                calculator.calculateScore(gameState, dead, 0);

        assertEquals(0.0, result.blackScore(), 0.001);
        assertEquals(1.0, result.whiteScore(), 0.001);
        assertEquals("WHITE", result.winner());
    }





}
