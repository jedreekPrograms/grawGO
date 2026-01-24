package pl.edu.go.model;

import java.util.*;
/**
 * Klasa odpowiedzialna za obliczanie końcowego wyniku gry.
 * Wykorzystuje algorytm zaliczania terytorium, uwzględniając zbite kamienie,
 * martwe grupy oraz komi (punktową rekompensatę dla gracza białego).
 */
public class TerritoryCalculator {
    /**
     * Rekord przechowujący szczegółowe wyniki partii.
     * @param blackScore całkowita liczba punktów gracza czarnego.
     * @param whiteScore całkowita liczba punktów gracza białego (z komi).
     * @param komi wartość rekompensaty punktowej dla białych.
     * @param winner nazwa koloru zwycięzcy ("BLACK" lub "WHITE").
     */
    public record GameResult(double blackScore, double whiteScore, double komi, String winner) {}
    /**
     * Oblicza finalny wynik gry na podstawie aktualnego stanu planszy.
     * Proces obejmuje usunięcie martwych kamieni, identyfikację zamkniętych terytoriów
     * oraz zsumowanie punktów terytorialnych ze zbitymi kamieniami.
     *
     * @param gameState aktualny stan gry.
     * @param deadStones zbiór punktów z kamieniami uznanymi przez graczy za martwe.
     * @param komi wartość punktowa dodawana białym za grę jako drugi.
     * @return obiekt GameResult z podsumowaniem punktacji.
     */
    public GameResult calculateScore(GameState gameState, Set<Point> deadStones, double komi) {
        // Tworzymy kopię planszy, aby nie modyfikować oryginalnego stanu gry podczas obliczeń
        Board board = new Board(gameState.getBoard());
        int size = board.getSize();

        int extraBlackCaptures = 0;
        int extraWhiteCaptures = 0;
        // Usuwamy martwe kamienie i dodajemy je do puli zbitych kamieni przeciwnika
        for (Point p : deadStones) {
            Color c = board.get(p.x, p.y);
            if (c == Color.WHITE) extraBlackCaptures++;
            else if (c == Color.BLACK) extraWhiteCaptures++;

            board.removeGroup(Set.of(p));
        }
        boolean[][] visited = new boolean[size][size];

        int blackTerritory = 0;
        int whiteTerritory = 0;
        // Przeszukujemy planszę w poszukiwaniu pustych obszarów (terytoriów)
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (board.isEmpty(x, y) && !visited[x][y]) {
                    TerritoryResult tr = analyzeTerritory(board, x, y, visited);

                    if (tr.owner == Color.BLACK) blackTerritory += tr.size;
                    else if (tr.owner == Color.WHITE) whiteTerritory += tr.size;
                }
            }
        }
        // Sumujemy kamienie zbite w trakcie gry oraz te usunięte jako martwe
        int totalBlackCaptures = gameState.getBlackCaptures() + extraBlackCaptures;
        int totalWhiteCaptures = gameState.getWhiteCaptures() + extraWhiteCaptures;
        // Finalne obliczenie: Terytorium + Jeńcy (+ Komi dla białych)
        double finalBlack = blackTerritory + totalBlackCaptures;
        double finalWhite = whiteTerritory + totalWhiteCaptures + komi;

        String winner = finalBlack > finalWhite ? "BLACK" : "WHITE";

        return new GameResult(finalBlack, finalWhite, komi, winner);
    }
    /**
     * Pomocniczy rekord przechowujący wynik analizy konkretnego spójnego obszaru.
     */
    private record TerritoryResult(Color owner, int size) {}

    /**
     * Analizuje spójny obszar pustych pól przy użyciu algorytmu przeszukiwania wszerz (BFS).
     * Obszar zostaje uznany za terytorium danego gracza tylko wtedy, gdy styka się 
     * wyłącznie z kamieniami jednego koloru. Jeśli styka się z oboma kolorami (dame) 
     * lub żadnym, nie przynosi punktów.
     *
     * @param board kopia planszy do analizy.
     * @param sx współrzędna początkowa X.
     * @param sy współrzędna początkowa Y.
     * @param visited tablica odwiedzonych pól dla algorytmu BFS.
     * @return wynik analizy zawierający kolor właściciela obszaru oraz jego wielkość.
     */
    private TerritoryResult analyzeTerritory(Board board, int sx, int sy, boolean[][] visited) {
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(sx, sy));
        visited[sx][sy] = true;

        int size = 0;

        boolean touchesBlack = false;
        boolean touchesWhite = false;

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            size++;
            // Sprawdzamy sąsiadów pola
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
        // Wyznaczanie właściciela: obszar musi być otoczony tylko przez jeden kolor
        Color owner = Color.EMPTY;
        if (touchesBlack && !touchesWhite) owner = Color.BLACK;
        else if (!touchesBlack && touchesWhite) owner = Color.WHITE;

        return new TerritoryResult(owner, size);
    }
}
