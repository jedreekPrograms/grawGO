package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
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

        // 1. Pobierz kolor gracza wysyłającego komendę
        Color playerColor = session.getPlayerColor(sender);

        // 2. Spróbuj zaakceptować wynik dla tego gracza
        boolean bothAccepted = game.accept(playerColor);

        if (bothAccepted) {
            // Jeśli obaj zaakceptowali, kończymy grę
            game.confirmEndGame();
            var result = game.getFinalResult();

            session.sendToBoth(
                    "GAME_END " +
                    result.blackScore() + " " +
                    result.whiteScore() + " " +
                    result.winner()
            );

            session.endSession();
        } else {
            // Jeśli to dopiero pierwsza akceptacja, poinformuj o tym
            session.sendToBoth("PLAYER_ACCEPTED " + playerColor);
            // Można też wysłać wiadomość specyficzną dla klienta:
            // sender.send("INFO Waiting for the other player to accept");
        }
        return true;
    }
}
