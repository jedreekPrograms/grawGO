package pl.edu.go.model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Klasa przechowująca aktualny stan gry.
 * Odpowiada za planszę, tury graczy,
 * liczbę zbitych kamieni oraz zakończenie gry.
 */
public class GameState {

    /** Aktualna plansza gry. */
    private final Board board;

    /** Kolor gracza, który wykonuje następny ruch. */
    private Color nextToMove;

    /** Liczba kamieni zbitych przez czarnego gracza. */
    private int blackCaptures;

    /** Liczba kamieni zbitych przez białego gracza. */
    private int whiteCaptures;

    /** Historia hashy planszy używana do wykrywania powtórzeń pozycji. */
    private final Deque<Integer> historyHashes;

    /** Informacja, czy gra została zakończona. */
    private boolean gameOver;

    /**
     * Tworzy nowy stan gry dla planszy o podanym rozmiarze.
     * Czarny gracz rozpoczyna grę.
     *
     * @param size rozmiar planszy
     */
    public GameState(int size) {
        this.board = new Board(size);
        this.nextToMove = Color.BLACK;
        this.blackCaptures = 0;
        this.whiteCaptures = 0;
        this.historyHashes = new ArrayDeque<>();
        this.historyHashes.add(board.computeHash());
        this.gameOver = false;
    }

    /** @return aktualna plansza */
    public Board getBoard() {
        return board;
    }

    /** @return kolor gracza, który ma wykonać ruch */
    public Color getNextToMove() {
        return nextToMove;
    }

    /** @return liczba kamieni zbitych przez czarnego */
    public int getBlackCaptures() {
        return blackCaptures;
    }

    /** @return liczba kamieni zbitych przez białego */
    public int getWhiteCaptures() {
        return whiteCaptures;
    }

    /** @return true jeśli gra jest zakończona */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Próbuje zastosować ruch do aktualnego stanu gry.
     *
     * @param move ruch do wykonania
     * @return true jeśli ruch został wykonany poprawnie,
     *         false jeśli ruch jest nielegalny lub gra jest zakończona
     */
    public boolean applyMove(Move move) {
        if (gameOver) return false;

        switch (move.getType()) {
            case PLACE:
                if (move.getColor() != nextToMove) {
                    return false;
                }

                int captured = board.placeStone(
                        move.getColor(),
                        move.getPos().x,
                        move.getPos().y
                );

                if (captured < 0) return false;

                if (move.getColor() == Color.BLACK) {
                    blackCaptures += captured;
                } else {
                    whiteCaptures += captured;
                }

                historyHashes.add(board.computeHash());
                nextToMove = nextToMove.opponent();
                return true;

            case PASS:
                if (move.getColor() != nextToMove) return false;
                nextToMove = nextToMove.opponent();
                return true;

            case RESIGN:
                gameOver = true;
                return true;

            default:
                return false;
        }
    }
}
