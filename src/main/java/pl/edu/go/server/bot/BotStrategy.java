package pl.edu.go.server.bot;

import pl.edu.go.server.GameSession;
import pl.edu.go.model.Color;

/**
 * Strategy – odpowiada WYŁĄCZNIE za decyzję bota.
 */
public interface BotStrategy {

    String decideMove(GameSession session, Color botColor);

    void onOpponentPass();

    String onGameResumed();
    String onAccept();
    String onTurn(GameSession session, Color botColor);
}
