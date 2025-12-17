package pl.edu.go.client;

import pl.edu.go.client.networkInterfaces.MessageListener;
import pl.edu.go.client.networkInterfaces.ServerAPI;
import pl.edu.go.client.networkInterfaces.SocketServerAPI;
import pl.edu.go.model.Board;
import pl.edu.go.model.Color;
import pl.edu.go.model.Point;

import java.io.IOException;
import java.util.Scanner;

/**
 * Klasa reprezentująca klienta gry Go działającego w konsoli.
 *
 * <p>
 * GoClient łączy się z serwerem, odbiera komunikaty dotyczące stanu gry,
 * wyświetla planszę i komunikaty graczowi oraz wysyła jego ruchy do serwera.
 * </p>
 *
 * <p>
 * Klient utrzymuje lokalną kopię planszy (localBoard) w celu wyświetlania w konsoli.
 * Logika gry, weryfikacja ruchów i aktualizacja stanu są wykonywane po stronie serwera.
 * </p>
 */
public class GoClient {

    /** Abstrakcja połączenia z serwerem. */
    private final ServerAPI api;

    /** Konsolowy interfejs użytkownika do wyświetlania planszy i pobierania ruchów. */
    private final ConsoleUI ui;

    /** Lokalna kopia planszy dla wyświetlania w UI. */
    private Board localBoard;

    /** Kolor gracza przypisany przez serwer. */
    private Color myColor = null;

    /** Flaga sterująca główną pętlą klienta. */
    private boolean running = true;

    /**
     * Tworzy klienta Go i łączy go z serwerem pod podanym hostem i portem.
     *
     * @param host adres serwera
     * @param port port serwera
     * @throws IOException jeśli połączenie z serwerem nie powiedzie się
     */
    public GoClient(String host, int port) throws IOException {
        this.api = new SocketServerAPI(host, port);
        this.ui = new ConsoleUI(new Scanner(System.in));
    }

    /**
     * Konstruktor testowy pozwalający wstrzyknąć własne API serwera i UI.
     *
     * @param api interfejs serwera
     * @param ui interfejs użytkownika
     */
    GoClient(ServerAPI api, ConsoleUI ui) {
        this.api = api;
        this.ui = ui;
    }

    /**
     * Uruchamia klienta w trybie konsoli.
     *
     * <p>
     * Pętla główna klienta pobiera ruchy od gracza za pomocą {@link ConsoleUI} i wysyła je do serwera.
     * Odbiera też wiadomości z serwera i aktualizuje lokalną planszę.
     * </p>
     */
    public void run() {
        api.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(String message) {
                handleServerMessage(message);
            }
        });

        Scanner scanner = new Scanner(System.in);

        while (running) {
            try {
                Color promptColor = myColor == null ? Color.BLACK : myColor;
                Board boardForInput = localBoard != null ? localBoard : new Board(19);

                String command = ui.getMoveCommand(promptColor, boardForInput);
                api.send(command);

                if (command.equalsIgnoreCase("RESIGN")) {
                    System.out.println("You resigned. Exiting");
                    running = false;
                }
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage());
                running = false;
            }
        }

        api.close();
        scanner.close();
    }

    /**
     * Obsługuje wiadomość otrzymaną z serwera.
     *
     * <p>
     * W zależności od typu wiadomości aktualizuje lokalną planszę, wyświetla komunikaty
     * graczowi lub kończy grę.
     * </p>
     *
     * @param message wiadomość otrzymana od serwera
     */
    private void handleServerMessage(String message) {
        if (message == null) return;
        message = message.trim();
        if (message.isEmpty()) return;

        if (message.startsWith("START")) {
            String[] parts = message.split("\\s+");
            if (parts.length >= 2) {
                myColor = parts[1].equalsIgnoreCase("WHITE") ? Color.WHITE : Color.BLACK;
                System.out.println("Assigned color: " + myColor);
            }
            return;
        }

        if (message.startsWith("WAITING_FOR_OPPONENT")) {
            System.out.println("Waiting for opponent...");
            return;
        }

        if (message.startsWith("BOARD")) {
            String payload = message.substring("BOARD".length()).trim();
            String[] lines = payload.split("\n");
            try {
                int size = Integer.parseInt(lines[0].trim());
                Board b = new Board(size);
                for (int y = 0; y < size && y + 1 < lines.length; y++) {
                    String row = lines[y + 1];
                    for (int x = 0; x < size && x < row.length(); x++) {
                        char ch = row.charAt(x);
                        if (ch == 'B') b.placeStone(Color.BLACK, x, y);
                        else if (ch == 'W') b.placeStone(Color.WHITE, x, y);
                    }
                }
                localBoard = b;
                ui.displayBoard(localBoard);
            } catch (NumberFormatException e) {
                System.out.println("Invalid BOARD payload:\n" + payload);
            }
            return;
        }

        if (message.startsWith("VALID")) {
            System.out.println("Move accepted.");
            return;
        }

        if (message.startsWith("INVALID")) {
            System.out.println("Invalid move.");
            return;
        }

        if (message.startsWith("OPPONENT_MOVED")) {
            System.out.println("Opponent moved: " + message.substring("OPPONENT_MOVED".length()).trim());
            return;
        }

        if (message.startsWith("OPPONENT_PASSED")) {
            System.out.println("Opponent passed.");
            return;
        }

        if (message.startsWith("RESIGN")) {
            System.out.println("RESIGN: " + message.substring("RESIGN".length()).trim());
            running = false;
            return;
        }

        if (message.startsWith("WINNER")) {
            System.out.println("Game winner: " + message.substring("WINNER".length()).trim());
            running = false;
            return;
        }

        if (message.startsWith("ERROR")) {
            System.out.println("Server error: " + message.substring("ERROR".length()).trim());
            return;
        }

        System.out.println("Server: " + message);
    }

    /**
     * Uruchamiany programowo punkt wejścia dla klienta.
     *
     * @param args argumenty: [host] [port]
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);

        try {
            GoClient client = new GoClient(host, port);
            client.run();
        } catch (IOException e) {
            System.err.println("Nie udało się połączyć z serwerem: " + e.getMessage());
        }
    }
}
