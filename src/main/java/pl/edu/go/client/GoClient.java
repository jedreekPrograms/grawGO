package pl.edu.go.client;

import pl.edu.go.client.networkInterfaces.MessageListener;
import pl.edu.go.client.networkInterfaces.ServerAPI;
import pl.edu.go.client.networkInterfaces.SocketServerAPI;
import pl.edu.go.model.Board;
import pl.edu.go.model.Color;
import pl.edu.go.model.Point;

import java.io.IOException;
import java.util.Scanner;

public class GoClient {
    private final ServerAPI api;
    private final ConsoleUI ui;
    private Board localBoard;
    private Color myColor = null;
    private boolean running = true;




    public GoClient(String host, int port) throws IOException {
        this.api = new SocketServerAPI(host, port);
        this.ui = new ConsoleUI(new Scanner(System.in));
    }

    // konstruktor testowy
    GoClient(ServerAPI api, ConsoleUI ui) {
        this.api = api;
        this.ui = ui;
    }


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

        // zamknięcie dopiero po zakończeniu gry
        api.close();
        scanner.close();
    }

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
