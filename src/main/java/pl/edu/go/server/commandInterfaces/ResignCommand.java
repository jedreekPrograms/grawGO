package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

/**
 * Komenda poddania się (resign) w grze Go.
 * Gracz rezygnuje, przeciwnik zostaje zwycięzcą.
 */
public class ResignCommand implements GameCommand {

    private final MoveFactory moveFactory = new MoveFactory();

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        Color loser = session.getPlayerColor(sender);
        Color winner = loser.opponent();

        Move move = moveFactory.createResign(loser);
        session.getGame().applyMove(move);

        // Powiadomienie obu graczy o wyniku
        session.sendToBoth("RESIGN " + loser);
        session.sendToBoth("WINNER " + winner);
        // Zakończenie sesji
        session.endSession();
        return true;
    }
}
