package pl.edu.go.server.commandInterfaces;


import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

public interface GameCommand {
    boolean execute(String[] args, GameSession session, ClientConnection sender);
}
