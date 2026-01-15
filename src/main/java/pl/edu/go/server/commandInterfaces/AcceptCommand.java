package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.GameState;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
/**
 * Komenda obsługująca żądanie akceptacji aktualnego stanu punktacji przez gracza.
 * Wywoływana w fazie STOPPED, gdy obaj gracze muszą zgodzić się co do martwych kamieni
 * przed ostatecznym zakończeniem partii.
 */
public class AcceptCommand implements GameCommand {
    /**
     * Wykonuje logikę akceptacji punktacji. 
     * Sprawdza, czy gra jest w odpowiedniej fazie, rejestruje decyzję gracza 
     * i jeśli obaj uczestnicy wyrazili zgodę – kończy sesję gry, wysyłając ostateczne wyniki.
     *
     * @param args Tablica argumentów komendy (w tym przypadku zazwyczaj pusta).
     * @param session Aktualna sesja gry, w ramach której komenda jest wykonywana.
     * @param sender Połączenie klienta, który wysłał żądanie akceptacji.
     * @return true, jeśli komenda została przetworzona poprawnie; false w przypadku błędu fazy gry.
     */
    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        GameState game = session.getGame();

        // Akceptacja jest możliwa tylko wtedy, gdy gra została wstrzymana po dwóch pasach
        if (game.getStatus() != GameState.Status.STOPPED) {
            sender.send("ERROR Game is not in scoring phase");
            return false;
        }

        // Pobranie koloru gracza wysyłającego komendę
        Color playerColor = session.getPlayerColor(sender);

        // Rejestracja akceptacji w stanie gry i sprawdzenie, czy to już komplet zgód
        boolean bothAccepted = game.accept(playerColor);

        if (bothAccepted) {
            game.confirmEndGame();
            var result = game.getFinalResult();
            // Rozesłanie komunikatu o definitywnym końcu gry wraz z punktacją i zwycięzcą
            session.sendToBoth(
                    "GAME_END " +
                    result.blackScore() + " " +
                    result.whiteScore() + " " +
                    result.winner()
            );
            // Zamknięcie sesji gry i ewentualne rozłączenie klientów
            session.endSession();
        } else {
            // Tylko jeden gracz zaakceptował – informujemy o tym drugą stronę
            session.sendToBoth("PLAYER_ACCEPTED " + playerColor);
        }
        return true;
    }
}
