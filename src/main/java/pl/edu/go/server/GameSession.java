package pl.edu.go.server;

import pl.edu.go.server.commandInterfaces.CommandRegistry;
import pl.edu.go.server.commandInterfaces.GameCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.GameState;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.model.Point;
import pl.edu.go.model.Color;
import pl.edu.go.model.Board;

import java.util.Objects;

public class GameSession {

    private final ClientConnection whitePlayer;
    private final ClientConnection blackPlayer;
    private final GameState game;
    private boolean sessionEnded = false;
    private final CommandRegistry registry;

    // NOWE:
    private final MoveFactory moveFactory = new MoveFactory();

    public GameSession(ClientConnection whitePlayer, ClientConnection blackPlayer, int boardSize, CommandRegistry registry) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.game = new GameState(boardSize);
        this.registry = registry;
    }

    public void start() {
        sendBoardToBoth();
        ClientConnection black = getClientByColor(Color.BLACK);
        if (black != null) black.send("YOUR_TURN");
    }

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

        boolean requiresTurn = cmd.equals("MOVE") || cmd.equals("PASS");
        if (requiresTurn && !Objects.equals(game.getNextToMove(), senderColor)) {
            sender.send("ERROR Not your turn");
            return;
        }

        switch (cmd) {
            case "MOVE":
                handleMove(sender, parts, senderColor);
                break;

            case "PASS":
                handlePass(sender, senderColor);
                break;

            case "RESIGN":
                handleResign(sender, senderColor);
                break;

            default:
                sender.send("ERROR Unknown command: " + cmd);
        }
    }

    private void handleMove(ClientConnection sender, String[] parts, Color color) {
        if (parts.length < 3) {
            sender.send("ERROR MOVE requires x y");
            return;
        }

        int x, y;
        try {
            x = Integer.parseInt(parts[1]);
            y = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            sender.send("ERROR Invalid coordinates");
            return;
        }

        Point pos = new Point(x, y);

        // ❗ FABRYKA
        Move m = moveFactory.createPlace(pos, color);

        synchronized (game) {
            boolean ok = game.applyMove(m);
            if (!ok) {
                sender.send("INVALID");
                return;
            }

            sender.send("VALID");
            ClientConnection opp = getOpponent(sender);
            if (opp != null) {
                opp.send("OPPONENT_MOVED " + x + " " + y);
            }
            sendBoardToBoth();

            ClientConnection nextPlayer = getClientByColor(game.getNextToMove());
            if (nextPlayer != null) nextPlayer.send("YOUR_TURN");
        }
    }

    private void handlePass(ClientConnection sender, Color color) {

        // ❗ FABRYKA
        Move m = moveFactory.createPass(color);

        synchronized (game) {
            boolean ok = game.applyMove(m);
            if (!ok) {
                sender.send("INVALID");
                return;
            }
            sender.send("VALID");

            ClientConnection opp = getOpponent(sender);
            if (opp != null) {
                opp.send("OPPONENT_PASSED " + color.name());
            }

            sendBoardToBoth();

            ClientConnection nextPlayer = getClientByColor(game.getNextToMove());
            if (nextPlayer != null) nextPlayer.send("YOUR_TURN");
        }
    }

    private void handleResign(ClientConnection sender, Color color) {
        ClientConnection opp = getOpponent(sender);

        sender.send("RESIGN " + color.name());
        if (opp != null) opp.send("WINNER " + oppestsColor(opp).name());

        endSession();
    }

    private Color oppestsColor(ClientConnection c) {
        Color col = getPlayerColor(c);
        return col == Color.BLACK ? Color.WHITE : Color.BLACK;
    }

    private void sendBoardToBoth() {
        String serialized = serializeBoard(game.getBoard());
        sendToBoth("BOARD\n" + serialized);
    }

    private String serializeBoard(Board b) {
        StringBuilder sb = new StringBuilder();
        int size = b.getSize();
        sb.append(size).append("\n");
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Color c = b.get(x, y);
                char ch;
                if (c == null || c == Color.EMPTY) ch = '.';
                else if (c == Color.BLACK) ch = 'B';
                else ch = 'W';
                sb.append(ch);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public GameState getGame() { return game; }

    public Color getPlayerColor(ClientConnection c) {
        if (c == whitePlayer) return Color.WHITE;
        if (c == blackPlayer) return Color.BLACK;
        return null;
    }

    private ClientConnection getOpponent(ClientConnection c) {
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
}
