package pl.edu.go.server;


import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.networkInterfaces.MessageListener;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.Color;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class MatchmakingServer {

    private static final int PORT = 5000;

    private static final LinkedBlockingQueue<ClientConnection> waitingPlayers = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        System.out.println("Serwer matchmakingu uruchomiony na porcie " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(socket);
                handler.setMessageListener(msg -> System.out.println("Odebrano od " + handler.getSocket() + ": " + msg));
                Thread t = new Thread(handler);
                t.start();

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
                //patrzymy czy klienci istnieja
                if (player1 == null || player2 == null) {
                    if (player2 != null) {
                        player2.send("WAITING_FOR_OPPONENT");
                    }
                    return;
                }

                System.out.println("Parowanie graczy: " + player1.getSocket() + " <-> " + player2.getSocket());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
