package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.GameState;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.commandInterfaces.GameCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;

public class ContinueCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        GameState game = session.getGame();

        if (game.getStatus() != GameState.Status.STOPPED) {
            sender.send("ERROR Game is not stopped");
            return false;
        }

        game.requestResume();
        game.setNextToMove(session.getPlayerColor(sender));
        session.sendToBoth("GAME_RESUMED " + session.getPlayerColor(sender));
        session.sendBoardToBoth();
        return true;
    }
}
