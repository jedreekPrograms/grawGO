package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.GameState;
import pl.edu.go.model.Point;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.commandInterfaces.GameCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;

public class DeadCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        GameState game = session.getGame();
        System.out.println("Odebrano dead");
        if (game.getStatus() != GameState.Status.STOPPED) {
            sender.send("ERROR Dead stones can be marked only after two passes");
            return false;
        }

        if (args.length != 2) {
            sender.send("ERROR Usage: DEAD x y");
            return false;
        }

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);

            boolean ok = game.toggleDeadStone(new Point(x, y));
            if (!ok) {
                sender.send("ERROR Cannot mark this point");
                return false;
            }

            session.sendToBoth("DEAD_MARKED " + x + " " + y);
            session.sendBoardToBoth();
            return true;

        } catch (NumberFormatException e) {
            sender.send("ERROR Coordinates must be numbers");
            return false;
        }
    }
}
