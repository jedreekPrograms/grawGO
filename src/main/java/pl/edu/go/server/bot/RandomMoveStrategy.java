package pl.edu.go.server.bot;

import pl.edu.go.server.GameSession;
import pl.edu.go.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomMoveStrategy implements BotStrategy {

    private static final int MAX_ATTEMPTS = 50;
    boolean shouldPass = false;
    @Override
    public String decideMove(GameSession session, Color botColor) {
        GameState game = session.getGame();
        Board board = game.getBoard();

        List<Point> allPoints = new ArrayList<>();

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                allPoints.add(new Point(x, y));
            }
        }

        // losowa kolejność prób
        Collections.shuffle(allPoints);

        int attempts = 0;

        for (Point p : allPoints) {
            if (attempts++ >= MAX_ATTEMPTS) {
                break;
            }

            // Zwracamy komendę – legalność sprawdzi MoveCommand
            return "MOVE " + p.x + " " + p.y;
        }

        // jeśli nie udało się znaleźć sensownego ruchu
        return "PASS";
    }

    @Override
    public String onAccept() {
        return "ACCEPT";
    }

    @Override
    public void onOpponentPass() {
        shouldPass = true;
    }

    @Override
    public String onGameResumed() {
        return "PASS";
    }

    @Override
    public String onTurn(GameSession session, Color botColor) {
        if(shouldPass){
            shouldPass = false;
            return "Pass";
        }
        return decideMove(session, botColor);
    }
}
