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
        
        // Upewnij się, że to tura tego gracza (zabezpieczenie)
        if (game.getNextToMove() != color) {
            return false;
        }

        Move move = moveFactory.createPass(color);

        // 1. Najpierw aplikujemy ruch w modelu
        // GameState ustawi status STOPPED jeśli to drugi pas, ale NIE zmieni nextToMove
        boolean success = game.applyMove(move);
        
        if (!success) {
            return false; 
        }

        // 2. Wysyłamy info o pasie
        session.sendToBoth("PASS " + color);

        // 3. Sprawdzamy, jaki jest status gry PO wykonaniu ruchu
        if (game.getStatus() == GameState.Status.STOPPED) {
            // Jeśli gra weszła w stan oznaczania kamieni -> wysyłamy STOPPED
            session.sendToBoth("STOPPED");
            // Nie zmieniamy nextToMove, bo gra jest zatrzymana
        } else {
            // 4. Jeśli gra nadal trwa (Status.PLAYING), to Komenda zmienia turę
            if (color == Color.BLACK) {
                game.setNextToMove(Color.WHITE);
            } else {
                game.setNextToMove(Color.BLACK);
            }
        }

        return true;
    }
}
