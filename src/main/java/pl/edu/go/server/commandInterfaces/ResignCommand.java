package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

/**
 * Implementacja komendy rezygnacji (poddania się) w grze Go.
 * Wywołanie tej komendy przez któregokolwiek z graczy skutkuje natychmiastowym
 * przerwaniem rozgrywki i ogłoszeniem przeciwnika zwycięzcą.
 */
public class ResignCommand implements GameCommand {
    /** Fabryka służąca do tworzenia obiektów ruchu typu RESIGN. */
    private final MoveFactory moveFactory = new MoveFactory();
    /**
     * Wykonuje logikę poddania się gracza.
     * Identyfikuje gracza, który wysłał komendę, wyznacza zwycięzcę (przeciwnika),
     * aktualizuje stan logiczny gry w modelu i rozsyła komunikat o zakończeniu partii.
     *
     * @param args Tablica argumentów komendy (zazwyczaj pusta).
     * @param session Aktualna sesja gry, w której uczestniczy gracz.
     * @param sender Połączenie klienta (nadawcy), który zdecydował się poddać partię.
     * @return true, ponieważ komenda rezygnacji jest zawsze procesowana pomyślnie.
     */
    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        // Pobranie koloru gracza, który się poddaje
        Color loser = session.getPlayerColor(sender);
        // Wyznaczenie zwycięzcy (automatycznie przeciwnik osoby poddającej się)
        Color winner = loser.opponent();
        // Utworzenie obiektu ruchu typu RESIGN i zaaplikowanie go do modelu gry
        // Powoduje to ustawienie statusu gry na FINISHED w GameState
        Move move = moveFactory.createResign(loser);
        session.getGame().applyMove(move);

        // Poinformowanie obu graczy o rezygnacji i ogłoszenie koloru zwycięzcy
        session.sendToBoth("RESIGN " + winner);

        return true;
    }
}
