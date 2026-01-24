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
    /**
     * Reprezentuje możliwe stany, w jakich może znajdować się gra.
     */
    public enum Status {
        PLAYING,    // Gra trwa
        STOPPED,    // Gra zatrzymana (dwa pasy), czas na oznaczanie martwych kamieni
        FINISHED    // Gra zakończona (rezygnacja lub podliczenie punktów)
    }
    /** Zbiór punktów na planszy oznaczonych jako martwe w fazie STOPPED. */
    private Set<Point> markedDead = new HashSet<>();
    /** Zbiór kolorów graczy, którzy zaakceptowali aktualny stan martwych kamieni. */
    private final Set<Color> acceptedPlayers = new HashSet<>();
    /** Aktualna plansza gry. */
    private Board board;

    /** Kolor gracza, który wykonuje następny ruch. */
    private Color nextToMove;
    private TerritoryCalculator.GameResult finalResult;
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
        if (status == Status.FINISHED) return false;
        if (move.getColor() != nextToMove && status == Status.PLAYING) return false;

        if (move.getType() == Move.Type.RESIGN) {
            status = Status.FINISHED;
            // W przypadku rezygnacji zwycięzcą jest przeciwnik
            String winner = move.getColor() == Color.BLACK ? "WHITE" : "BLACK";
            this.finalResult = new TerritoryCalculator.GameResult(0, 0, 6.5, winner);
            return true;
        }

        if (move.getType() == Move.Type.PASS) {
            if (lastMoveType == Move.Type.PASS) {
                // Drugi pas -> faza oznaczania martwych kamieni
                System.out.println("Entering scoring");
                this.status = Status.STOPPED;
                lastMoveType = null; // resetujemy, żeby DEAD / ACCEPT / CONTINUE działały
                // **Nie obliczamy wyniku od razu!**
                return true;
            } else {
                lastMoveType = Move.Type.PASS;
                //switchTurn(Move.Type.PASS);
                return true;
            }
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
            lastMoveType = Move.Type.PLACE;
            return true;
        }
        return false;
    }
    /**
     * Uruchamia kalkulator terytorium i ustawia ostateczny wynik gry.
     */
    private void calculateAndSetScore() {
        TerritoryCalculator calculator = new TerritoryCalculator();
        this.finalResult = calculator.calculateScore(this, new HashSet<>(), 6.5);
    }
    /** @return Wynik gry po jej zakończeniu. */
    public TerritoryCalculator.GameResult getFinalResult() {
        return finalResult;
    }
    /**
     * Wznawia grę z fazy STOPPED do PLAYING, czyszcząc oznaczenia martwych kamieni.
     */   
    public void resumeGame() {
        if (status == Status.STOPPED) {
            markedDead.clear();
            status = Status.PLAYING;
            
            lastMoveType = null;

        }
    }
    /** @return true, jeśli gra została definitywnie zakończona. */
    public boolean isGameOver() {
        return status == Status.FINISHED;
    }
    /**
     * Przełącza status grupy kamieni (żywa/martwa) na danej pozycji w fazie punktacji.
     * Oznaczenie martwej grupy powoduje automatyczne zaznaczenie wszystkich połączonych kamieni tego samego koloru.
     *
     * @param p Punkt na planszy wskazujący grupę.
     * @return true, jeśli operacja się powiodła (gra jest w fazie STOPPED).
     */
    public boolean toggleDeadStone(Point p) {
        if (status != Status.STOPPED) return false;

        Color c = board.get(p.x, p.y);
        if (c == null || c == Color.EMPTY) return false;

        Set<Point> group = board.getGroup(p.x, p.y);

        if (markedDead.contains(p)) {
            markedDead.removeAll(group);
        } else {
            markedDead.addAll(group);
        }
        // Każda zmiana oznaczeń resetuje akceptację graczy
        acceptedPlayers.clear();
        return true;
    }
    /** Żąda wznowienia gry (wyjścia z fazy punktacji). */
    public void requestResume() {
        if (status == Status.STOPPED) {
            resumeGame();
        }
    }
    /**
     * Definitywnie kończy grę. 
     * Usuwa z planszy grupy oznaczone jako martwe, dolicza je do punktów graczy i oblicza wynik terytorialny.
     */
    public void confirmEndGame() {
        if (status != Status.STOPPED) return;

        for (Point p : markedDead) {
            Color c = board.get(p.x, p.y);
            if (c == Color.BLACK) whiteCaptures++;
            if (c == Color.WHITE) blackCaptures++;
        }
        board.removeGroup(markedDead);

        calculateAndSetScore();

        status = Status.FINISHED;
    }
    /** @return Kopia zbioru punktów oznaczonych jako martwe. */
    public Set<Point> getMarkedDead() {
        return new HashSet<>(markedDead);
    }
    /**
     * Rejestruje akceptację stanu planszy przez danego gracza.
     *
     * @param player Kolor gracza akceptującego.
     * @return true, jeśli obaj gracze zaakceptowali stan (oznacza to koniec gry).
     */
    public boolean accept(Color player) {
        if (status != Status.STOPPED) return false;

        acceptedPlayers.add(player);
        return acceptedPlayers.size() == 2;
    }
    /** @param s Nowy status gry. */
    public void setStatus(Status s){
        this.status = s;
    }
    /** @param n Kolor gracza, który ma teraz wykonać ruch. */
    public void setNextToMove(Color n){
        this.nextToMove = n;
    }
    /** @return Typ ostatnio wykonanego ruchu. */
    public Move.Type getLastMoveType(){
        return lastMoveType;
    }
}
