package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.Point;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.GameState;
/**
 * Implementacja komendy odpowiedzialnej za wykonanie ruchu postawienia kamienia na planszy.
 * Klasa zarządza weryfikacją kolejności graczy, poprawnością współrzędnych
 * oraz rozsyłaniem aktualizacji stanu planszy po udanym ruchu.
 */
public class MoveCommand implements GameCommand {

    /**
     * Wykonuje logikę ruchu postawienia kamienia.
     * Sprawdza, czy gracz wykonuje ruch w swojej turze, czy ruch jest legalny według zasad Go
     * (brak samobójstwa, zasada Ko) oraz aktualizuje liczbę zbitych kamieni.
     *
     * @param args Tablica argumentów, gdzie args[0] to współrzędna X, a args[1] to Y.
     * @param session Aktualna sesja gry, w której uczestniczy gracz.
     * @param sender Połączenie klienta wysyłającego żądanie ruchu.
     * @return true, jeśli ruch został pomyślnie wykonany; false w przypadku błędnych argumentów,
     * nie swojej tury lub nielegalnego ruchu.
     */
    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        GameState game = session.getGame();
        // Walidacja liczby argumentów (wymagane x i y)
        if (args.length != 2) {
            sender.send("ERROR MOVE x y");
            return false;
        }

        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);

        Color color = session.getPlayerColor(sender);
        if (color != session.getGame().getNextToMove()) {
            sender.send("ERROR Not your turn");
            return false;
        }
        // Utworzenie obiektu ruchu typu PLACE przy użyciu fabryki
        Move move = new MoveFactory().createPlace(new Point(x, y), color);
        // Próba zaaplikowania ruchu w modelu gry (sprawdzenie logiki Go)
        if (!session.getGame().applyMove(move)) {
            sender.send("ILLEGAL MOVE");
            return false;
        }
        // Zmiana tury na przeciwnika
        if(session.getPlayerColor(sender) == Color.BLACK){
            game.setNextToMove(Color.WHITE);
        }else{
            game.setNextToMove(Color.BLACK);
        }
        // Pobranie liczby kamieni zbitych w tym ruchu i powiadomienie graczy
        int captured = session.getGame().getBoard().getTotalCaptured();
        session.sendToBoth("MOVE " + color + " " + x + " " + y + " " + captured);

        // Przesłanie zaktualizowanej planszy w formacie tekstowym do obu klientów
        session.sendBoardToBoth();

        return true;
    }
}
