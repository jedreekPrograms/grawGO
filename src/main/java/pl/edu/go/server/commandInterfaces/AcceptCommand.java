package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.GameState;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.commandInterfaces.GameCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.persistence.PersistenceApplication;
import pl.edu.go.server.persistence.entity.MoveType;
import pl.edu.go.server.persistence.service.GamePersistenceService;

public class AcceptCommand implements GameCommand {
    Color winner;
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
            if (result.blackScore() > result.whiteScore()) {
                winner = Color.BLACK;
            } else {
                winner = Color.WHITE;
            }
            GamePersistenceService ps = PersistenceApplication.getBean(GamePersistenceService.class);

//            ps.saveMove(
//                    session.getGameEntity(),
//                    session.nextMoveNumber(),
//                    null,
//                    null,
//                    playerColor,
//                    MoveType.ACCEPT
//            );

            ps.finishGame(
                    session.getGameEntity(),
                    winner.toString()
            );

            //session.endSession();
        } else {
            // Jeśli to dopiero pierwsza akceptacja, poinformuj o tym
            session.sendToBoth("PLAYER_ACCEPTED " + playerColor);
            // Można też wysłać wiadomość specyficzną dla klienta:
            // sender.send("INFO Waiting for the other player to accept");
        }
        return true;
    }
}
