package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.GameState;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.model.Move.Type;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
/**
 * Komenda rezygnacji (pass) w grze Go.
 * Gracz rezygnuje z wykonania ruchu w danej turze.
 */
public class PassCommand implements GameCommand {

    private final MoveFactory moveFactory = new MoveFactory();


    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {
        GameState game = session.getGame();
        Color color = session.getPlayerColor(sender);
        Move move = moveFactory.createPass(color);
        if (game.getLastMoveType() == Type.PASS) {
            System.out.println("Scoring pass");
            session.sendToBoth("STOPPED");
        }
        if(session.getPlayerColor(sender) == Color.BLACK){
            game.setNextToMove(Color.WHITE);
        }else{
            game.setNextToMove(Color.BLACK);
        }
        session.sendToBoth("PASS " + color);
        game.applyMove(move);
        
        //int licznikPass = session.getLicznikPass();

        //if (licznikPass < 2) {
            //session.sendToBoth("PASS " + color);
            //licznikPass++;
            //session.setLicznikPass(licznikPass);
        //} else {

        //}

        return true;
    }
}
