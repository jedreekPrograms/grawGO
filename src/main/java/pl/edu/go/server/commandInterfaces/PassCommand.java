package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.GameState;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
/**
 * Implementacja komendy spasowania tury (pass) w grze Go.
 * Odpowiada za proces rezygnacji gracza z wykonania ruchu w bieżącej turze
 * oraz sprawdza, czy nastąpiła sekwencja dwóch pasów kończąca aktywną rozgrywkę.
 */
public class PassCommand implements GameCommand {
    /** Fabryka służąca do tworzenia obiektów ruchu typu PASS. */
    private final MoveFactory moveFactory = new MoveFactory();
    /**
     * Wykonuje logikę spasowania tury przez gracza.
     * Sprawdza, czy gracz ma prawo do ruchu, aplikuje pasowanie w modelu gry
     * i aktualizuje status sesji. Jeśli obaj gracze spasowali pod rząd,
     * gra przechodzi w fazę oznaczania martwych kamieni.
     *
     * @param args Tablica argumentów komendy (zazwyczaj pusta).
     * @param session Aktualna sesja gry.
     * @param sender Połączenie klienta wysyłającego komendę PASS.
     * @return true, jeśli komenda została pomyślnie przetworzona; 
     * false, jeśli gracz próbował spasować nie w swojej turze lub wystąpił błąd logiki.
     */
    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        GameState game = session.getGame();
        Color color = session.getPlayerColor(sender);
        // Weryfikacja, czy gracz wykonuje akcję w swojej turze
        if (game.getNextToMove() != color) {
            return false;
        }
        // Tworzenie i aplikowanie ruchu typu PASS w modelu gry
        Move move = moveFactory.createPass(color);
        boolean success = game.applyMove(move);
        
        if (!success) {
            return false; 
        }
        // Poinformowanie obu graczy o spasowaniu tury przez jednego z nich
        session.sendToBoth("PASS " + color);
        // Sprawdzenie, czy model gry zmienił status na STOPPED (po dwóch pasach z rzędu)
        if (game.getStatus() == GameState.Status.STOPPED) {
            // Rozpoczęcie fazy oznaczania martwych kamieni i obliczania punktów
            session.sendToBoth("STOPPED");

        } else {
            // Normalna zmiana tury, jeśli to dopiero pierwszy pas w sekwencji
            if (color == Color.BLACK) {
                game.setNextToMove(Color.WHITE);
            } else {
                game.setNextToMove(Color.BLACK);
            }
        }

        return true;
    }
}
