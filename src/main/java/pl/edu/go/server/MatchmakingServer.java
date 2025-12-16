package pl.edu.go.server;

import pl.edu.go.server.commandInterfaces.CommandRegistry;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.networkInterfaces.MessageListener;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.Color;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * Serwer matchmakingu gry Go.
 * Odpowiada za przyjmowanie połączeń, parowanie graczy i inicjalizację sesji gry.
 */
public class MatchmakingServer {

    private static final int PORT = 5000;

    // Kolejka oczekujących graczy
    private static final LinkedBlockingQueue<ClientConnection> waitingPlayers = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        System.out.println("Serwer matchmakingu uruchomiony na porcie " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(socket);
                // Listener loguje odebrane wiadomości
                handler.setMessageListener(msg -> System.out.println("Odebrano od " + handler.getSocket() + ": " + msg));
                Thread t = new Thread(handler);
                t.start();

                // Dodajemy klienta do kolejki oczekujących
                matchmaking(handler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Próba sparowania graczy w kolejce.
     * @param newPlayer nowo połączony klient
     */
    private static synchronized void matchmaking(ClientConnection newPlayer) {
        try {
            // parowanie jeśli już jest ktoś w kolejce
            if (!waitingPlayers.isEmpty()) {
                ClientConnection player1 = waitingPlayers.poll();
                ClientConnection player2 = newPlayer;
                //patrzymy czy klienci istnieja
                if (player1 == null || player2 == null) {
                    if (player2 != null) {
                        waitingPlayers.add(player2);
                        player2.send("WAITING_FOR_OPPONENT");
                    }
                    return;
                }
                // Tworzenie rejestru komend dla sesji
                CommandRegistry registry = new CommandRegistry();
                registry.register("MOVE", new pl.edu.go.server.commandInterfaces.MoveCommand());
                registry.register("PASS", new pl.edu.go.server.commandInterfaces.PassCommand());
                registry.register("RESIGN", new pl.edu.go.server.commandInterfaces.ResignCommand());

                GameSession session = new GameSession(player1, player2, 19, registry);

                player1.setGameSession(session, Color.WHITE);
                player2.setGameSession(session, Color.BLACK);
                System.out.println("Parowanie graczy: " + player1.getSocket() + " <-> " + player2.getSocket());


                player1.send("INFO You are WHITE");
                player2.send("INFO You are BLACK");

            } else {
                waitingPlayers.add(newPlayer);
                newPlayer.send("WAITING_FOR_OPPONENT");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
