package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.Point;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

/**
 * Implementacja komendy wykonania ruchu w grze Go.
 * Odpowiada za przesłanie ruchu do sesji, walidację oraz powiadomienie graczy.
 */
public class MoveCommand implements GameCommand {
    // Fabryka do tworzenia obiektów ruchów
    private final MoveFactory moveFactory = new MoveFactory();

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        // Walidacja liczby argumentów
        if (args.length != 2) {
            sender.send("ERROR MOVE requires x y");
            return false;
        }
        // Parsowanie współrzędnych
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        // Pobranie koloru gracza wysyłającego ruch
        Color color = session.getPlayerColor(sender);

        Point pos = new Point(x, y);
        Move move = moveFactory.createPlace(pos, color);

        boolean ok = session.getGame().applyMove(move);

        if (!ok) {
            sender.send("ILLEGAL MOVE");
            return false;
        }

        // Powiadomienie obu graczy o wykonanym ruchu
        session.sendToBoth("MOVE " + color + " " + x + " " + y);
        session.sendToBoth("BOARD\n" + session.getGame().getBoard().toString());

        return true;
    }
}
