package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.GameState;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.commandInterfaces.GameCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;

public class AcceptCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        GameState game = session.getGame();

        if (game.getStatus() != GameState.Status.STOPPED) {
            sender.send("ERROR Game is not in scoring phase");
            return false;
        }

        game.confirmEndGame();

        var result = game.getFinalResult();

        session.sendToBoth(
                "GAME_END " +
                result.blackScore() + " " +
                result.whiteScore() + " " +
                result.winner()
        );

        session.endSession();
        return true;
    }
}
