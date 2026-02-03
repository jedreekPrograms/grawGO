package pl.edu.go.server.bot;

import pl.edu.go.server.GameSession;
import pl.edu.go.model.*;

import java.util.*;

public class HeuristicBot implements BotStrategy {

    private final Random random = new Random();
    boolean shouldPass = false;
    @Override
    public String decideMove(GameSession session, Color botColor) {

        Board board = session.getGame().getBoard();
        int size = board.getSize();
        
        List<Point> candidates = new ArrayList<>();

        // Prosta heurystyka:
        // 1. Najpierw środek
        Point center = new Point(size / 2, size / 2);
        candidates.add(center);

        // 2. Potem sąsiedztwo istniejących kamieni
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (board.get(x, y) != Color.EMPTY) {
                    addNeighbors(board, x, y, candidates);
                }
            }
        }

        // 3. Fallback – losowe pole
        if (candidates.isEmpty()) {
            return randomMove(board);
        }

        Point chosen = candidates.get(random.nextInt(candidates.size()));
        return "MOVE " + chosen.x + " " + chosen.y;
    }

    private void addNeighbors(Board board, int x, int y, List<Point> list) {
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        int size = board.getSize();

        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                if (board.get(nx, ny) == Color.EMPTY) {
                    list.add(new Point(nx, ny));
                }
            }
        }
    }

    private String randomMove(Board board) {
        int size = board.getSize();
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            if (board.get(x, y) == Color.EMPTY) {
                return "MOVE " + x + " " + y;
            }
        }
        return "PASS";
    }

    // ==== REAKCJE NA PROTOKÓŁ ====

    @Override
    public void onOpponentPass() {
        shouldPass = true;
    }

    @Override
    public String onAccept() {
        return "ACCEPT";
    }
    public String onGameResumed(){
        return "PASS";
    }
    @Override
    public String onTurn(GameSession session, Color botColor){
        if(shouldPass){
            shouldPass = false;
            return "Pass";
        }
        return decideMove(session, botColor);
    }
}
