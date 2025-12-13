package pl.edu.go.model;



import java.util.ArrayDeque;
import java.util.Deque;



public class GameState {
    private final Board board;
    private Color nextToMove;
    private int blackCaptures;
    private int whiteCaptures;
    private final Deque<Integer> historyHashes;
    private boolean gameOver;

    public GameState(int size) {
        this.board = new Board(size);
        this.nextToMove = Color.BLACK;
        this.blackCaptures = 0;
        this.whiteCaptures = 0;
        this.historyHashes = new ArrayDeque<>();
        this.historyHashes.add(board.computeHash());
        this.gameOver = false;
    }

    public Board getBoard() { return board; }
    public Color getNextToMove() { return nextToMove; }
    public int getBlackCaptures() { return blackCaptures; }
    public int getWhiteCaptures() { return whiteCaptures; }
    public boolean isGameOver() { return gameOver; }

    public boolean applyMove(Move move) {
        if (gameOver) return false;

        switch (move.getType()) {
            case PLACE:
                if (move.getColor() != nextToMove) {
                    return false;
                }

                int captured = board.placeStone(move.getColor(), move.getPos().x, move.getPos().y);
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
