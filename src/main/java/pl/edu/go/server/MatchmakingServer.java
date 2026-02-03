package pl.edu.go.server;

import pl.edu.go.server.commandInterfaces.*;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.networkInterfaces.MessageListener;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.Color;
import pl.edu.go.server.persistence.PersistenceApplication;
import pl.edu.go.server.persistence.entity.GameEntity;
import pl.edu.go.server.persistence.service.GamePersistenceService;
import pl.edu.go.server.bot.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Główna klasa serwera odpowiedzialna za przyjmowanie nowych połączeń
 * oraz kojarzenie graczy w pary (matchmaking).
 * Serwer nasłuchuje na określonym porcie i po zebraniu dwóch osób tworzy nową sesję gry.
 */
public class MatchmakingServer {

    /** Port, na którym serwer akceptuje połączenia przychodzące. */
    private static final int PORT = 5000;

    /** Bezpieczna wątkowo kolejka przechowująca graczy oczekujących na przeciwnika. */
    private static final LinkedBlockingQueue<ClientConnection> waitingPlayers = new LinkedBlockingQueue<>();

    /**
     * Punkt wejścia serwera. Uruchamia gniazdo serwerowe i w nieskończonej pętli
     * akceptuje nowych klientów, przypisując każdego do osobnego wątku obsługi.
     *
     * @param args parametry linii komend (nieużywane).
     */
    public static void main(String[] args) {
        PersistenceApplication.start();
        System.out.println("Serwer matchmakingu uruchomiony na porcie " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                // Oczekiwanie na połączenie nowego klienta
                Socket socket = serverSocket.accept();
                
                // Utworzenie handlera dla klienta i uruchomienie go w osobnym wątku
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

    /**
     * Próbuje sparować nowo połączonego gracza z kimś z kolejki oczekujących.
     * Metoda jest synchronizowana, aby zapobiec problemom przy jednoczesnym łączeniu wielu graczy.
     * Jeśli para zostanie znaleziona, inicjalizuje rejestr komend i tworzy sesję gry.
     *
     * @param newPlayer połączenie nowego gracza.
     */
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
                // Konfiguracja rejestru komend dostępnych w tej sesji gry
                CommandRegistry registry = new CommandRegistry();
                registry.register("MOVE", new pl.edu.go.server.commandInterfaces.MoveCommand());
                registry.register("PASS", new pl.edu.go.server.commandInterfaces.PassCommand());
                registry.register("RESIGN", new pl.edu.go.server.commandInterfaces.ResignCommand());
                registry.register("FINAL", new pl.edu.go.server.commandInterfaces.FinalCommand());
                registry.register("DEAD", new DeadCommand());
                registry.register("CONTINUE", new ContinueCommand());
                registry.register("ACCEPT", new AcceptCommand());
                registry.register("PLAYSTORE", new PlayStoreCommand());

                GamePersistenceService ps = PersistenceApplication.getBean(GamePersistenceService.class);

                GameEntity gameEntity = ps.startGame(9);

                //tu rozmiar dodalem
                GameSession session = new GameSession(player1, player2, 9, registry);

                session.setGameEntity(gameEntity);

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

    public static synchronized void startBotGame(ClientConnection player) {
        waitingPlayers.remove(player);
    
        CommandRegistry registry = new CommandRegistry();
        registry.register("MOVE", new pl.edu.go.server.commandInterfaces.MoveCommand());
        registry.register("PASS", new pl.edu.go.server.commandInterfaces.PassCommand());
        registry.register("RESIGN", new pl.edu.go.server.commandInterfaces.ResignCommand());
        registry.register("FINAL", new pl.edu.go.server.commandInterfaces.FinalCommand());
        registry.register("DEAD", new DeadCommand());
        registry.register("CONTINUE", new ContinueCommand());
        registry.register("ACCEPT", new AcceptCommand());
        registry.register("PLAYSTORE", new PlayStoreCommand());
        BotStrategy strategy = new HeuristicBot();
        BotConnection bot = new BotConnection(strategy);
        GamePersistenceService ps = PersistenceApplication.getBean(GamePersistenceService.class);

        GameEntity gameEntity = ps.startGame(9);
        GameSession session = new GameSession(player, bot, 9, registry);
        session.setGameEntity(gameEntity);
        player.setGameSession(session, Color.WHITE);
        bot.setGameSession(session, Color.BLACK);
    
        session.start();
    }
}
