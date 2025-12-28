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

    private final MoveFactory moveFactory = new MoveFactory();

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

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

        Move move = new MoveFactory().createPlace(new Point(x, y), color);

        if (!session.getGame().applyMove(move)) {
            sender.send("ILLEGAL MOVE");
            return false;
        }

        int captured = session.getGame().getBoard().getTotalCaptured();
        session.sendToBoth("MOVE " + color + " " + x + " " + y + " " + captured);
        session.sendBoardToBoth();
        return true;
    }
}
