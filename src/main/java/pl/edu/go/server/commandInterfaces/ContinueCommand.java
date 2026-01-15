package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
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
        Color c;
        if(session.getPlayerColor(sender) == Color.BLACK){
            game.setNextToMove(Color.WHITE);
            c = Color.WHITE;
        }else{
            game.setNextToMove(Color.BLACK);
            c = Color.BLACK;
        }
        
        session.sendToBoth("GAME_RESUMED " + c);
        session.sendBoardToBoth();
        return true;
    }
}
