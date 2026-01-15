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

import java.util.Objects;
/**
 * Klasa reprezentująca aktywną sesję gry Go między dwoma graczami.
 * Pełni rolę kontrolera (mediatora), który zarządza stanem gry, egzekwuje kolejność ruchów,
 * dystrybuuje komunikaty do odpowiednich klientów oraz deleguje wykonanie komend do rejestru komend.
 */
public class GameSession {
    /** Połączenie sieciowe gracza grającego białymi kamieniami. */
    private final ClientConnection whitePlayer;
    /** Połączenie sieciowe gracza grającego czarnymi kamieniami. */
    private final ClientConnection blackPlayer;
    private final GameState game;
    /** Flaga określająca, czy sesja została już zakończona. */
    private boolean sessionEnded = false;
    /** Rejestr dostępnych komend (np. MOVE, PASS, DEAD). */
    private final CommandRegistry registry;

    /** Fabryka do tworzenia obiektów ruchów. */
    private final MoveFactory moveFactory = new MoveFactory();

    /**
     * Tworzy nową sesję gry dla dwóch graczy.
     * @param whitePlayer połączenie gracza białego.
     * @param blackPlayer połączenie gracza czarnego.
     * @param boardSize rozmiar planszy (np. 9, 13, 19).
     * @param registry rejestr obiektów komend serwera.
     */
    public GameSession(ClientConnection whitePlayer, ClientConnection blackPlayer, int boardSize, CommandRegistry registry) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.game = new GameState(boardSize);
        this.registry = registry;
    }

    /**
     * Rozpoczyna sesję gry, przesyłając początkowy stan planszy do obu graczy
     * oraz informując gracza czarnego, że to jego tura.
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

        boolean requiresTurn = cmd.equals("MOVE") || cmd.equals("PASS");
        boolean allowDuringStop = game.getStatus() == GameState.Status.STOPPED && (cmd.equals("DEAD") || cmd.equals("ACCEPT") || cmd.equals("CONTINUE"));

        if (requiresTurn && game.getNextToMove() != senderColor && !allowDuringStop) {
            sender.send("ERROR Not your turn");
            return;
        }


        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        if (!handleCommand(cmd, args, sender)) {
            return;
        }

        if (game.getStatus() == Status.PLAYING) {
            ClientConnection next = getClientByColor(game.getNextToMove());
            if (next != null) next.send("YOUR_TURN");
        }

        System.out.println(game.getStatus());
    }
    /**
     * Zwraca kolor przeciwnika gracza podanego jako argument.
     * @param c połączenie gracza.
     * @return kolor przeciwnika.
     */
    private Color oppestsColor(ClientConnection c) {
        Color col = getPlayerColor(c);
        return col == Color.BLACK ? Color.WHITE : Color.BLACK;
    }
    /**
     * Konstruuje i wysyła do obu graczy aktualny stan planszy.
     * Format komunikatu zawiera rozmiar, układ pól oraz listę martwych kamieni (jeśli są).
     */
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





    /** @return Obiekt stanu gry. */
    public GameState getGame() { return game; }

    /**
     * Rozpoznaje kolor gracza na podstawie obiektu połączenia.
     * @param c połączenie klienta.
     * @return Kolor gracza lub null, jeśli nie należy do tej sesji.
     */
    public Color getPlayerColor(ClientConnection c) {
        if (c == whitePlayer) return Color.WHITE;
        if (c == blackPlayer) return Color.BLACK;
        return null;
    }

    /** Zwraca połączenie przeciwnika. */
    protected ClientConnection getOpponent(ClientConnection c) {
        return c == whitePlayer ? blackPlayer : whitePlayer;
    }

    /** Pobiera połączenie klienta na podstawie koloru. */
    private ClientConnection getClientByColor(Color color) {
        return color == Color.WHITE ? whitePlayer : blackPlayer;
    }

    /** Wysyła wiadomość tekstową do obu uczestników sesji. */
    public void sendToBoth(String msg) {
        if (whitePlayer != null) whitePlayer.send(msg);
        if (blackPlayer != null) blackPlayer.send(msg);
    }

    /**
     * Kończy sesję gry, blokując przyjmowanie nowych wiadomości i zamykając gniazda obu graczy.
     */
    public void endSession() {
        sessionEnded = true;
        if (whitePlayer != null) whitePlayer.close();
        if (blackPlayer != null) blackPlayer.close();
    }

    /**
     * Deleguje wykonanie konkretnej komendy do odpowiedniego obiektu w rejestrze.
     * @param cmd nazwa komendy (np. "MOVE").
     * @param args parametry komendy.
     * @param sender nadawca żądania.
     * @return true, jeśli komenda została znaleziona i wykonana pomyślnie.
     */
    public boolean handleCommand(String cmd, String[] args, ClientConnection sender) {
        GameCommand command = registry.get(cmd);
        if (command == null) {
            sender.send("ERROR Unknown command: " + cmd);
            return false;
        }
        return command.execute(args, this, sender);
    }
}