//package pl.edu.go.model;
//
//import java.util.*;
//
///**
 //* Klasa obliczająca punkty po zakończeniu gry.
 //*/
//public class TerritoryCalculator {
//
    ///**
     //* Wynik gry.
     //*/
    //public record GameResult(double blackScore, double whiteScore, double komi, String winner) {}
//
    ///**
     //* Oblicza wynik gry.
     //*
     //* @param gameState Stan gry.
     //* @param deadStones Zbiór punktów wskazanych jako martwe kamienie.
     //* @param komi Punkty rekompensaty dla białego.
     //* @return Wynik gry.
     //*/
    //public GameResult calculateScore(GameState gameState, Set<Point> deadStones, double komi) {
        //Board originalBoard = gameState.getBoard();
        //int size = originalBoard.getSize();
//
        //// 1. Stwórz kopię planszy do obliczeń
        //Board scoringBoard = new Board(originalBoard);
//
        //// 2. Usuń martwe kamienie (i policz je jako jeńców)
        //int extraBlackCaptures = 0;
        //int extraWhiteCaptures = 0;
//
        //for (Point p : deadStones) {
            //Color stone = scoringBoard.get(p.x, p.y);
            //if (stone == Color.WHITE) {
                //extraBlackCaptures++;
            //} else if (stone == Color.BLACK) {
                //extraWhiteCaptures++;
            //}
            //// Usuwamy kamień manualnie na kopii planszy
            //// (Board.placeStone ma logikę gry, tutaj robimy czystkę)
        //}
        //scoringBoard.removeGroup(deadStones);
//
        //// 3. Policz terytorium
        //// ZMIANA: Używamy tablic jednoelementowych jako "mutable wrappers",
        //// aby można było je modyfikować wewnątrz lambdy.
        //final int[] blackTerritory = {0};
        //final int[] whiteTerritory = {0};
//        
        //boolean[][] visited = new boolean[size][size];
//
        //for (int x = 0; x < size; x++) {
            //for (int y = 0; y < size; y++) {
                //if (scoringBoard.isEmpty(x, y) && !visited[x][y]) {
                    //analyzeTerritory(scoringBoard, x, y, visited,
                            //res -> {
                                //if (res == Color.BLACK) blackTerritory[0]++;
                                //else if (res == Color.WHITE) whiteTerritory[0]++;
                            //});
                //}
            //}
        //}
//
        //// 4. Sumowanie punktów
        //int totalBlackCaptures = gameState.getBlackCaptures() + extraBlackCaptures;
        //int totalWhiteCaptures = gameState.getWhiteCaptures() + extraWhiteCaptures;
//
        //// Odczytujemy wartości z tablic
        //double finalBlackScore = blackTerritory[0] + totalBlackCaptures;
        //double finalWhiteScore = whiteTerritory[0] + totalWhiteCaptures + komi;
//
        //String winner = finalBlackScore > finalWhiteScore ? "BLACK" : "WHITE";
//
        //return new GameResult(finalBlackScore, finalWhiteScore, komi, winner);
    //}
//
    ///**
     //* Pomocnicza metoda analizująca obszar pustych pól.
     //*/
    //private void analyzeTerritory(Board board, int startX, int startY, boolean[][] visited, java.util.function.Consumer<Color> callback) {
        //List<Point> region = new ArrayList<>();
        //Queue<Point> queue = new LinkedList<>();
        //Point start = new Point(startX, startY);
//        
        //queue.add(start);
        //visited[startX][startY] = true;
        //region.add(start);
//
        //boolean touchesBlack = false;
        //boolean touchesWhite = false;
//
        //while (!queue.isEmpty()) {
            //Point p = queue.poll();
//
            //for (Point n : board.getAdjacentPoints(p.x, p.y)) {
                //Color c = board.get(n.x, n.y);
                //if (c == Color.EMPTY) {
                    //if (!visited[n.x][n.y]) {
                        //visited[n.x][n.y] = true;
                        //region.add(n);
                        //queue.add(n);
                    //}
                //} else if (c == Color.BLACK) {
                    //touchesBlack = true;
                //} else if (c == Color.WHITE) {
                    //touchesWhite = true;
                //}
            //}
        //}
//
        //Color owner = Color.EMPTY;
        //if (touchesBlack && !touchesWhite) owner = Color.BLACK;
        //if (!touchesBlack && touchesWhite) owner = Color.WHITE;
//        
        //// Wywołujemy callback dla każdego punktu w znalezionym regionie
        //for (int i = 0; i < region.size(); i++) {
             //callback.accept(owner);
        //}
    //}
//}