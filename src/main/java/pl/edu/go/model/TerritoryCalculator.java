package pl.edu.go.model;

import java.util.*;

public class TerritoryCalculator {

    public record GameResult(double blackScore, double whiteScore, double komi, String winner) {}

    public GameResult calculateScore(GameState gameState,
                                     Set<Point> deadStones,
                                     double komi) {

        Board board = new Board(gameState.getBoard());
        int size = board.getSize();

        // ==================================================
        // 1. Usuń martwe kamienie i policz jeńców
        // ==================================================
        int extraBlackCaptures = 0;
        int extraWhiteCaptures = 0;

        for (Point p : deadStones) {
            Color c = board.get(p.x, p.y);
            if (c == Color.WHITE) extraBlackCaptures++;
            else if (c == Color.BLACK) extraWhiteCaptures++;

            board.removeGroup(Set.of(p));   // bezpieczne – usuwa 1 kamień
        }

        // ==================================================
        // 2. Liczenie terytorium
        // ==================================================
        boolean[][] visited = new boolean[size][size];

        int blackTerritory = 0;
        int whiteTerritory = 0;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (board.isEmpty(x, y) && !visited[x][y]) {
                    TerritoryResult tr = analyzeTerritory(board, x, y, visited);

                    if (tr.owner == Color.BLACK) blackTerritory += tr.size;
                    else if (tr.owner == Color.WHITE) whiteTerritory += tr.size;
                }
            }
        }

        // ==================================================
        // 3. Wynik końcowy (japońska punktacja)
        // ==================================================
        int totalBlackCaptures =
                gameState.getBlackCaptures() + extraBlackCaptures;
        int totalWhiteCaptures =
                gameState.getWhiteCaptures() + extraWhiteCaptures;

        double finalBlack = blackTerritory + totalBlackCaptures;
        double finalWhite = whiteTerritory + totalWhiteCaptures + komi;

        String winner = finalBlack > finalWhite ? "BLACK" : "WHITE";

        return new GameResult(finalBlack, finalWhite, komi, winner);
    }

    // ==================================================
    // POMOCNICZE
    // ==================================================

    private record TerritoryResult(Color owner, int size) {}

    /**
     * Analizuje obszar pustych pól.
     * Właściciel = kolor ŻYWYCH kamieni granicznych.
     */
    private TerritoryResult analyzeTerritory(Board board,
                                             int sx, int sy,
                                             boolean[][] visited) {

        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(sx, sy));
        visited[sx][sy] = true;

        int size = 0;

        boolean touchesBlack = false;
        boolean touchesWhite = false;

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            size++;

            for (Point n : board.getAdjacentPoints(p.x, p.y)) {
                Color c = board.get(n.x, n.y);

                if (c == Color.EMPTY) {
                    if (!visited[n.x][n.y]) {
                        visited[n.x][n.y] = true;
                        queue.add(n);
                    }
                }
                else if (c == Color.BLACK) {
                    touchesBlack = true;
                }
                else if (c == Color.WHITE) {
                    touchesWhite = true;
                }
            }
        }

        Color owner = Color.EMPTY;
        if (touchesBlack && !touchesWhite) owner = Color.BLACK;
        else if (!touchesBlack && touchesWhite) owner = Color.WHITE;
        // jeśli oba → punkt niczyj / seki

        return new TerritoryResult(owner, size);
    }
}
