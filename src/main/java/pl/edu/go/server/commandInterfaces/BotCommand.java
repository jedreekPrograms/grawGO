package pl.edu.go.server.commandInterfaces;

import pl.edu.go.server.GameSession;
import pl.edu.go.server.MatchmakingServer;
import pl.edu.go.server.networkInterfaces.ClientConnection;

public class BotCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        // jeśli gracz już gra – odrzuć
        sender.send("ERROR Already in game");
        return false;
    }
}
