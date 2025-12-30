package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

public class FinalCommand implements GameCommand{
    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        Color color = session.getPlayerColor(sender);
        session.sendToBoth("FINAL " + color);
        return true;
    }
}
