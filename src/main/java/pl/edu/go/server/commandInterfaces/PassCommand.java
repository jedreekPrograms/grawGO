package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

public class PassCommand implements GameCommand {

    private final MoveFactory moveFactory = new MoveFactory();

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        Color color = session.getPlayerColor(sender);
        Move move = moveFactory.createPass(color);

        session.getGame().applyMove(move);

        session.sendToBoth("PASS " + color);
        return true;
    }
}
