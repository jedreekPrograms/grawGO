package pl.edu.go.server.bot;

import pl.edu.go.server.GameSession;
import pl.edu.go.model.Color;

public class PassStrategy implements BotStrategy {

    @Override
    public String decideMove(GameSession session, Color botColor) {
        return "PASS";
    }

    @Override
    public String onAccept() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onAccept'");
    }

    @Override
    public void onOpponentPass() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onOpponentPass'");
    }

    @Override
    public String onGameResumed() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onGameResumed'");
    }

    @Override
    public String onTurn(GameSession session, Color botColor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onTurn'");
    }
}
