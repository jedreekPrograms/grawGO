package pl.edu.go.server.networkInterfaces;

import java.net.Socket;
import pl.edu.go.model.Color;
import pl.edu.go.server.GameSession;


/**
 * Interfejs do komunikacji serwera z klientem.
 * Abstrahuje implementacje sieciowe (np. TCP).
 */
public interface ClientConnection {

    /**
     * Wysyła wiadomość do klienta.
     */
    void send(String message);
    /**
     * Rejestruje listener odbierający wiadomości od klienta.
     */
    void setMessageListener(MessageListener listener);


    /**
     * Zwraca gniazdo sieciowe klienta.
     */
    Socket getSocket();

    /**
     * Zamknięcie połączenia klienta.
     */
    void close();
    /**
     * Ustawia partnera klienta
     */
    void setPartner(ClientConnection partner);

    /**
     * Przypisuje klienta do sesji gry oraz koloru.
     */
    void setGameSession(GameSession session, Color color);
}
