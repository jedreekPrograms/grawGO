package pl.edu.go.server;

import pl.edu.go.server.commandInterfaces.CommandRegistry;
import pl.edu.go.server.commandInterfaces.GameCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.GameState;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.model.Point;
import pl.edu.go.model.GameState.Status;
import pl.edu.go.model.Color;
import pl.edu.go.model.Board;
import pl.edu.go.server.persistence.entity.GameEntity;

import java.util.Objects;
/**
 * Klasa reprezentująca sesję gry Go między dwoma graczami.
 * Odpowiada za stan gry, kolejność ruchów, obsługę komend i komunikację z klientami.
 */
public class GameSession {

    private final ClientConnection whitePlayer;
    private final ClientConnection blackPlayer;
    private final GameState game;
    private boolean sessionEnded = false;
    private final CommandRegistry registry;
    private final MoveFactory moveFactory = new MoveFactory();

    private GameEntity gameEntity;
    private int moveCounter = 0;

    public GameSession(ClientConnection whitePlayer, ClientConnection blackPlayer, int boardSize, CommandRegistry registry) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.game = new GameState(boardSize);
        this.registry = registry;
    }
    /**
     * Rozpoczęcie gry – wysyła planszę i informuje gracza, który ma pierwszy ruch.
     */
    public void start() {
        sendBoardToBoth();
        ClientConnection first = getClientByColor(game.getNextToMove());
        if (first != null) first.send("YOUR_TURN");
    }


    /**
     * Główna metoda obsługi komunikatów od graczy.
     * @param sender klient wysyłający wiadomość
     * @param message treść wiadomości
     */
    public synchronized void onMessage(ClientConnection sender, String message) {
        
        if (sessionEnded) {
            sender.send("ERROR Session ended");
            return;
        }

        message = message.trim();
        if (message.isEmpty()) return;

        String[] parts = message.split("\\s+");
        String cmd = parts[0].toUpperCase();

        Color senderColor = getPlayerColor(sender);
        if (senderColor == null) {
            sender.send("ERROR Unknown player");
            return;
        }

        // sprawdzenie tury
        // w fazie STOPPED wszystkie komendy DEAD / ACCEPT / CONTINUE są dozwolone bez tury
        boolean requiresTurn = cmd.equals("MOVE") || cmd.equals("PASS");
        boolean allowDuringStop = game.getStatus() == GameState.Status.STOPPED &&
                (cmd.equals("DEAD") || cmd.equals("ACCEPT") || cmd.equals("CONTINUE"));

        if (requiresTurn && game.getNextToMove() != senderColor && !allowDuringStop) {
            sender.send("ERROR Not your turn");
            return;
        }


        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        if (!handleCommand(cmd, args, sender)) {
            return;
        }

        // po poprawnej komendzie — następna tura
        if (game.getStatus() == Status.PLAYING) {
    ClientConnection next = getClientByColor(game.getNextToMove());
    if (next != null) next.send("YOUR_TURN");
}

        System.out.println(game.getStatus());
    }

    private Color oppestsColor(ClientConnection c) {
        Color col = getPlayerColor(c);
        return col == Color.BLACK ? Color.WHITE : Color.BLACK;
    }

    public void sendBoardToBoth() {
    StringBuilder sb = new StringBuilder();

    sb.append("BOARD ")
      .append(game.getBoard().getSize())
      .append(" ")
      .append(game.getBoard().toSingleLineString());

    if (!game.getMarkedDead().isEmpty()) {
        sb.append(" DEAD");
        for (Point p : game.getMarkedDead()) {
            sb.append(" ").append(p.x).append(",").append(p.y);
        }
    }

    sendToBoth(sb.toString());
}






    public GameState getGame() { return game; }

    public Color getPlayerColor(ClientConnection c) {
        if (c == whitePlayer) return Color.WHITE;
        if (c == blackPlayer) return Color.BLACK;
        return null;
    }

    protected ClientConnection getOpponent(ClientConnection c) {
        return c == whitePlayer ? blackPlayer : whitePlayer;
    }

    private ClientConnection getClientByColor(Color color) {
        return color == Color.WHITE ? whitePlayer : blackPlayer;
    }

    public void sendToBoth(String msg) {
        if (whitePlayer != null) whitePlayer.send(msg);
        if (blackPlayer != null) blackPlayer.send(msg);
    }

    public void endSession() {
        sessionEnded = true;
        if (whitePlayer != null) whitePlayer.close();
        if (blackPlayer != null) blackPlayer.close();
    }

    public boolean handleCommand(String cmd, String[] args, ClientConnection sender) {
        GameCommand command = registry.get(cmd);
        if (command == null) {
            sender.send("ERROR Unknown command: " + cmd);
            return false;
        }
        return command.execute(args, this, sender);
    }

    public void setGameEntity(GameEntity g) {
        this.gameEntity = g;
    }

    public GameEntity getGameEntity() {
        return gameEntity;
    }

    public int nextMoveNumber() {
        return ++moveCounter;
    }
}