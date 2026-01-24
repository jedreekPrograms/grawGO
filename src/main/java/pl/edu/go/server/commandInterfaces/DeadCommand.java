package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.GameState;
import pl.edu.go.model.Point;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
/**
 * Komenda serwera obsługująca oznaczanie kamieni jako martwe.
 * Wywoływana przez graczy w fazie wstrzymania gry (STOPPED), aby wspólnie 
 * ustalić, które grupy kamieni nie mają szans na przeżycie i powinny zostać 
 * usunięte przed ostatecznym podliczeniem punktów.
 */
public class DeadCommand implements GameCommand {
    /**
     * Wykonuje logikę przełączania statusu martwego kamienia na planszy.
     * Sprawdza, czy gra jest w odpowiedniej fazie, parsuje współrzędne 
     * i aktualizuje stan gry dla obu graczy.
     *
     * @param args Tablica argumentów, gdzie args[0] to współrzędna X, a args[1] to Y.
     * @param session Aktualna sesja gry.
     * @param sender Połączenie klienta, który wysłał żądanie oznaczenia kamienia.
     * @return true, jeśli operacja oznaczania zakończyła się sukcesem; false w przypadku błędnych danych lub fazy gry.
     */
    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        GameState game = session.getGame();
        // Oznaczanie martwych kamieni jest możliwe tylko po dwóch pasach (status STOPPED)
        if (game.getStatus() != GameState.Status.STOPPED) {
            sender.send("ERROR Dead stones can be marked only after two passes");
            return false;
        }
        // Walidacja liczby argumentów
        if (args.length != 2) {
            sender.send("ERROR Usage: DEAD x y");
            return false;
        }

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            // Przełączenie statusu martwego kamienia/grupy w modelu logicznym
            // Metoda toggleDeadStone zazwyczaj oznacza całą spójną grupę kamieni
            boolean ok = game.toggleDeadStone(new Point(x, y));
            if (!ok) {
                sender.send("ERROR Cannot mark this point");
                return false;
            }
            // Rozesłanie informacji o zmianie statusu punktu do obu graczy
            session.sendToBoth("DEAD_MARKED " + x + " " + y);
            // Odświeżenie wyglądu planszy u obu klientów
            session.sendBoardToBoth();
            return true;

        } catch (NumberFormatException e) {
            // Obsługa sytuacji, gdy współrzędne nie są poprawnymi liczbami całkowitymi
            sender.send("ERROR Coordinates must be numbers");
            return false;
        }
    }
}
