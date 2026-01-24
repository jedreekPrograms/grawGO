package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.persistence.PersistenceApplication;
import pl.edu.go.server.persistence.entity.GameEntity;
import pl.edu.go.server.persistence.entity.MoveType;
import pl.edu.go.server.persistence.service.GamePersistenceService;

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


        session.sendToBoth("RESIGN " + winner);
        GamePersistenceService ps = PersistenceApplication.getBean(GamePersistenceService.class);
        ps.saveMove(
                session.getGameEntity(),
                session.nextMoveNumber(),
                null,
                null,
                loser,
                MoveType.RESIGN
        );

        ps.finishGame(
                session.getGameEntity(),
                winner.toString()
        );

        // Zakończenie sesji
        //session.endSession();
        return true;
    }
}
