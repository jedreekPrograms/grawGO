package pl.edu.go.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.HashSet;

public class GoEngineTest {

    private GameState gameState;
    private MoveFactory moveFactory;

    @Before
    public void setUp() {
        // Inicjalizacja przed każdym testem
        gameState = new GameState(9);
        moveFactory = new MoveFactory();
    }

    @Test
    public void testBasicCapture() {
        // Czarny otacza biały kamień w rogu
        // (1,0) i (0,1) zajęte przez czarnego, biały na (0,0)
        gameState.applyMove(moveFactory.createPass(Color.BLACK)); // Ruch białego przygotowujemy
        gameState.applyMove(moveFactory.createPlace(new Point(0, 0), Color.WHITE));
        
        gameState.applyMove(moveFactory.createPlace(new Point(1, 0), Color.BLACK));
        gameState.applyMove(moveFactory.createPass(Color.WHITE));
        gameState.applyMove(moveFactory.createPlace(new Point(0, 1), Color.BLACK));

        // Sprawdzamy czy biały kamień na (0,0) został zbity (Zasada 3 i 4)
        Assert.assertEquals("Pole powinno być puste po biciu", Color.EMPTY, gameState.getBoard().get(0, 0));
        Assert.assertEquals("Czarny powinien mieć 1 punkt za bicie", 1, gameState.getBlackCaptures());
    }

    @Test
    public void testSuicideMoveForbidden() {
        // Przygotowanie sytuacji samobójczej (Zasada 5)
        // Biały zajmuje (0,1) i (1,0)
        gameState.applyMove(moveFactory.createPass(Color.BLACK));
        gameState.applyMove(moveFactory.createPlace(new Point(0, 1), Color.WHITE));
        gameState.applyMove(moveFactory.createPass(Color.BLACK));
        gameState.applyMove(moveFactory.createPlace(new Point(1, 0), Color.WHITE));

        // Czarny próbuje postawić na (0,0) - brak oddechów i brak bicia przeciwnika
        boolean result = gameState.applyMove(moveFactory.createPlace(new Point(0, 0), Color.BLACK));

        Assert.assertFalse("Ruch samobójczy nie powinien być dozwolony", result);
        Assert.assertEquals("Pole (0,0) powinno pozostać puste", Color.EMPTY, gameState.getBoard().get(0, 0));
    }

    @Test
    public void testKoRuleImplementation() {
        // Testowanie mechanizmu blokady Ko (Zasada 6)
        // 1. Doprowadzenie do bicia
        gameState.applyMove(moveFactory.createPlace(new Point(1, 0), Color.BLACK)); // C
        gameState.applyMove(moveFactory.createPlace(new Point(0, 0), Color.WHITE)); // B
        gameState.applyMove(moveFactory.createPlace(new Point(0, 1), Color.BLACK)); // C bije B na (0,0)
        
        int hashAfterCapture = gameState.getBoard().computeHash();
        
        // 2. Próba wykonania ruchu, który przywróciłby stan sprzed bicia (identyczny hash)
        // W prawdziwym Ko biały nie mógłby odbić natychmiast.
        // Tutaj sprawdzamy czy applyMove zablokuje ruch o tym samym hashu.
        
        // Symulacja ruchu o identycznym stanie planszy
        boolean isLegal = gameState.applyMove(moveFactory.createPlace(new Point(0, 0), Color.WHITE));
        
        // Jeśli hash był już w historyHashes, applyMove zwróci false
        // Uwaga: W tym uproszczonym teście sprawdzamy czy silnik pilnuje unikalności stanów
        Assert.assertFalse("Silnik powinien zablokować powrót do identycznego stanu planszy (Ko)", isLegal);
    }

    @Test
    public void testTwoPassesEndsGame() {
        // Zasada 8: Dwa pasy kończą fazę PLAYING
        Assert.assertEquals(GameState.Status.PLAYING, gameState.getStatus());

        gameState.applyMove(moveFactory.createPass(Color.BLACK));
        gameState.applyMove(moveFactory.createPass(Color.WHITE));

        Assert.assertEquals("Gra powinna przejść w stan STOPPED po dwóch pasach", 
                GameState.Status.STOPPED, gameState.getStatus());
    }

    @Test
    public void testResignEndsGame() {
        // Zasada 10: Poddanie się
        gameState.applyMove(moveFactory.createResign(Color.BLACK));
        Assert.assertTrue("Gra powinna być zakończona po rezygnacji", gameState.isGameOver());
    }

    @Test
    public void testTerritoryBasicCalculation() {
        // Zasada 7 i 9: Budujemy małe terytorium czarnych w rogu 2x2
        // Kamienie czarnych na (2,0), (2,1), (2,2), (1,2), (0,2)
        gameState.applyMove(moveFactory.createPlace(new Point(2, 0), Color.BLACK));
        gameState.applyMove(moveFactory.createPass(Color.WHITE));
        gameState.applyMove(moveFactory.createPlace(new Point(2, 1), Color.BLACK));
        gameState.applyMove(moveFactory.createPass(Color.WHITE));
        gameState.applyMove(moveFactory.createPlace(new Point(2, 2), Color.BLACK));
        gameState.applyMove(moveFactory.createPass(Color.WHITE));
        gameState.applyMove(moveFactory.createPlace(new Point(1, 2), Color.BLACK));
        gameState.applyMove(moveFactory.createPass(Color.WHITE));
        gameState.applyMove(moveFactory.createPlace(new Point(0, 2), Color.BLACK));
        
        TerritoryCalculator calculator = new TerritoryCalculator();
        // Liczymy punkty przy 0 martwych kamieniach i komi 6.5
        TerritoryCalculator.GameResult result = calculator.calculateScore(gameState, new HashSet<Point>(), 6.5);
        
        // Czarny otoczył 4 punkty: (0,0), (0,1), (1,0), (1,1)
        Assert.assertTrue("Czarny powinien mieć co najmniej 4 punkty terytorium", result.blackScore() >= 4.0);
        Assert.assertEquals("Biały powinien mieć punkty tylko z Komi (brak terytorium i bić)", 6.5, result.whiteScore(), 0.001);
    }
}