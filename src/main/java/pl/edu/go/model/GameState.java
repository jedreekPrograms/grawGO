package pl.edu.go.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Klasa przechowująca aktualny stan gry.
 * Odpowiada za planszę, tury graczy,
 * liczbę zbitych kamieni oraz zakończenie gry.
 */
public class GameState {

    public enum Status {
        PLAYING,    // Gra trwa
        STOPPED,    // Gra zatrzymana (dwa pasy), czas na oznaczanie martwych kamieni
        FINISHED    // Gra zakończona (rezygnacja lub podliczenie punktów)
    }

    /** Aktualna plansza gry. */
    private Board board;

    /** Kolor gracza, który wykonuje następny ruch. */
    private Color nextToMove;

    /** Liczba kamieni zbitych przez czarnego gracza. */
    private int blackCaptures;

    /** Liczba kamieni zbitych przez białego gracza. */
    private int whiteCaptures;

    private Status status;
    private final Set<Integer> previousBoardStates;
    private Move.Type lastMoveType;
    /** Historia hashy planszy używana do wykrywania powtórzeń pozycji. */
    //private final Deque<Integer> historyHashes;

    /** Informacja, czy gra została zakończona. */
    //private boolean gameOver;

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
        this.status = Status.PLAYING;
        //this.historyHashes = new ArrayDeque<>();
        //this.historyHashes.add(board.computeHash());
        this.previousBoardStates = new HashSet<>();
        this.previousBoardStates.add(board.computeHash());
        //this.gameOver = false;
        this.lastMoveType = null;
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
    public Status getStatus() { 
        return status; 
    }

    /**
     * Próbuje zastosować ruch do aktualnego stanu gry.
     *
     * @param move ruch do wykonania
     * @return true jeśli ruch został wykonany poprawnie,
     *         false jeśli ruch jest nielegalny lub gra jest zakończona
     */
    public boolean applyMove(Move move) {
        //if (gameOver) return false;
        if (status != Status.PLAYING) return false;
        if (move.getColor() != nextToMove) return false;

        if (move.getType() == Move.Type.RESIGN) {
            status = Status.FINISHED;
            return true;
        }

        if (move.getType() == Move.Type.PASS) {
            // Zasada 8: Dwa pasy z rzędu zatrzymują grę
            if (lastMoveType == Move.Type.PASS) {
                status = Status.STOPPED;
            }
            switchTurn(Move.Type.PASS);
            return true;
        }

        if (move.getType() == Move.Type.PLACE) {
            // Symulacja ruchu na kopii planszy, aby sprawdzić legalność (Ko i Samobójstwo)
            Board simulation = new Board(this.board);
            int captured = simulation.placeStone(move.getColor(), move.getPos().x, move.getPos().y);

            // Ruch nielegalny mechanicznie (zajęte pole, samobójstwo)
            if (captured < 0) {
                return false;
            }

            // Zasada 6 (Ko / Superko): Sprawdź czy stan planszy się nie powtarza
            int newHash = simulation.computeHash();
            if (previousBoardStates.contains(newHash)) {
                return false; // Naruszenie zasady Ko
            }

            // Jeśli wszystko OK, aplikujemy ruch na prawdziwej planszy
            board = simulation; // Podmieniamy planszę na tę z symulacji
            if (move.getColor() == Color.BLACK) {
                blackCaptures += captured;
            } else {
                whiteCaptures += captured;
            }

            previousBoardStates.add(newHash);
            switchTurn(Move.Type.PLACE);
            return true;
        }
        return false;
        /*switch (move.getType()) {
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
        }*/




    }
    private void switchTurn(Move.Type type) {
        this.lastMoveType = type;
        this.nextToMove = nextToMove.opponent();
    }
    public void resumeGame() {
        if (status == Status.STOPPED) {
            status = Status.PLAYING;
            lastMoveType = null; // Reset licznika pasów
            // nextToMove pozostaje bez zmian (czyli ten, kto miałby ruch po dwóch pasach)
            // lub można wymusić logikę z zasady 8 ("przeciwnik nie może odmówić i gra jako pierwszy").
            // W obecnym modelu nextToMove wskazuje na osobę, która miałaby ruch po pasie,
            // co spełnia ten warunek naturalnie.
        }
    }
    public boolean isGameOver() {
    return status == Status.FINISHED;
    }
}
