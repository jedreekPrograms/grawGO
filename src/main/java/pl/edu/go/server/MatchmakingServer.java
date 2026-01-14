package pl.edu.go.server;

import pl.edu.go.server.commandInterfaces.AcceptCommand;
import pl.edu.go.server.commandInterfaces.CommandRegistry;
import pl.edu.go.server.commandInterfaces.ContinueCommand;
import pl.edu.go.server.commandInterfaces.DeadCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.networkInterfaces.MessageListener;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.Color;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class MatchmakingServer {

    private static final int PORT = 5000;

    // Kolejka oczekujących graczy
    private static final LinkedBlockingQueue<ClientConnection> waitingPlayers = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        System.out.println("Serwer matchmakingu uruchomiony na porcie " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();
                //System.out.println("Nowy klient połączony: " + socket);

                ClientHandler handler = new ClientHandler(socket);
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

    // Próba sparowania graczy
    private static synchronized void matchmaking(ClientConnection newPlayer) {
        try {
            // jeśli już jest ktoś w kolejce – parujemy
            if (!waitingPlayers.isEmpty()) {
                ClientConnection player1 = waitingPlayers.poll();
                ClientConnection player2 = newPlayer;
                //patrzymy czy klienci istnieja !!!!!!!!!
                if (player1 == null || player2 == null) {
                    if (player2 != null) {
                        waitingPlayers.add(player2);
                        player2.send("WAITING_FOR_OPPONENT");
                    }
                    return;
                }
                CommandRegistry registry = new CommandRegistry();
                registry.register("MOVE", new pl.edu.go.server.commandInterfaces.MoveCommand());
                registry.register("PASS", new pl.edu.go.server.commandInterfaces.PassCommand());
                registry.register("RESIGN", new pl.edu.go.server.commandInterfaces.ResignCommand());
                registry.register("FINAL", new pl.edu.go.server.commandInterfaces.FinalCommand());
                registry.register("DEAD", new DeadCommand());
                registry.register("CONTINUE", new ContinueCommand());
                registry.register("ACCEPT", new AcceptCommand());

                //tu rozmiar dodalem
                GameSession session = new GameSession(player1, player2, 9, registry);

                player1.setGameSession(session, Color.WHITE);
                player2.setGameSession(session, Color.BLACK);
                //parujemy graczy !!!!!!!!!!!!!!!!!
                System.out.println("Parowanie graczy: " + player1.getSocket() + " <-> " + player2.getSocket());

                session.start();
                player1.send("INFO You are WHITE");
                player2.send("INFO You are BLACK");

            } else {
                // nikt nie czeka – ustawiamy klienta jako oczekującego
                waitingPlayers.add(newPlayer);
                newPlayer.send("WAITING_FOR_OPPONENT");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
