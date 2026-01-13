package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.model.TerritoryCalculator;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import java.util.Set;

/**
 * Komenda rezygnacji (pass) w grze Go.
 * Gracz rezygnuje z wykonania ruchu w danej turze.
 */
public class PassCommand implements GameCommand {

    private final MoveFactory moveFactory = new MoveFactory();


    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        Color color = session.getPlayerColor(sender);
        Move move = moveFactory.createPass(color);
        session.getGame().applyMove(move);

        int licznikPass = session.getLicznikPass() + 1;
        session.setLicznikPass(licznikPass);

        session.sendToBoth("PASS " + color);

        if (licznikPass == 2) {
            // 🔴 KONIEC GRY → LICZYMY PUNKTY
            TerritoryCalculator calc = new TerritoryCalculator();

            // na razie: brak martwych kamieni
            var result = calc.calculateScore(
                    session.getGame(),
                    Set.of(),
                    6.5 // komi
            );

            session.sendToBoth(
                    "SCORE " +
                            result.blackScore() + " " +
                            result.whiteScore() + " " +
                            result.winner()
            );
        }

        return true;
    }

}
