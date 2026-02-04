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
        return "ACCEPT";
    }

    @Override
    public void onOpponentPass() {
    }

    @Override
    public String onGameResumed() {
        return "PASS";
    }

    @Override
    public String onTurn(GameSession session, Color botColor) {
        return decideMove(session, botColor);
    }
}
