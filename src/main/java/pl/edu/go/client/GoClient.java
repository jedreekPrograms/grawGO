package pl.edu.go.client;

import javafx.application.Platform;
import pl.edu.go.client.networkInterfaces.ServerAPI;
import pl.edu.go.client.networkInterfaces.SocketServerAPI;

import java.io.IOException;
/**
 * Klasa klienta odpowiedzialna za logikę komunikacji z serwerem gry Go.
 * Pełni rolę pośrednika (wrappera) nad interfejsem ServerAPI, tłumacząc
 * akcje użytkownika na komunikaty sieciowe oraz przekazując odpowiedzi serwera do UI.
 */
public class GoClient {
    /** Interfejs komunikacyjny do wysyłania i odbierania danych. */
    private final ServerAPI api;
    /** Referencja do kontrolera interfejsu graficznego. */
    private final GoBoardDemo ui;
    /**
     * Tworzy nową instancję klienta i inicjalizuje połączenie z serwerem.
     * @param host Adres IP lub nazwa hosta serwera.
     * @param port Numer portu, na którym nasłuchuje serwer.
     * @param ui Referencja do obiektu klasy GoBoardDemo, który ma być aktualizowany.
     * @throws IOException W przypadku błędu podczas nawiązywania połączenia gniazda.
     */
    public GoClient(String host, int port, GoBoardDemo ui) throws IOException {
        this.api = new SocketServerAPI(host, port);
        this.ui = ui;
    }
    /**
     * Uruchamia nasłuchiwanie komunikatów z serwera.
     * Otrzymane wiadomości są przekazywane do interfejsu użytkownika wewnątrz 
     * bloku Platform.runLater, aby zapewnić bezpieczeństwo wątkowe JavaFX.
     */
    public void run() {
        api.setMessageListener(msg ->
                Platform.runLater(() -> ui.handleServerMessage(msg))
        );
    }
    /**
     * Wysyła żądanie wykonania ruchu na określonych współrzędnych.
     * @param x Współrzędna X na planszy.
     * @param y Współrzędna Y na planszy.
     */
    public void sendMove(int x, int y) {
        api.send("MOVE " + x + " " + y);
    }
    /**
     * Wysyła informację o spasowaniu tury przez gracza.
     */
    public void sendPass() {
        api.send("PASS");
    }
    /**
     * Wysyła informację o poddaniu partii przez gracza.
     */
    public void sendResign() {
        api.send("RESIGN");
    }
    /**
     * Wysyła współrzędne kamienia, który ma zostać oznaczony jako martwy w fazie punktacji.
     * @param x Współrzędna X kamienia.
     * @param y Współrzędna Y kamienia.
     */
    public void sendDead(int x, int y) {
        api.send("DEAD " + x + " " + y);
    }
    /**
     * Wysyła potwierdzenie (akceptację) aktualnego stanu planszy i punktacji.
     */
    public void sendAccept() {
        api.send("ACCEPT");
    }
    /**
     * Wysyła żądanie kontynuowania gry i wyjścia z fazy oznaczania martwych kamieni.
     */
    public void sendContinue() {
        api.send("CONTINUE");
    }

}
