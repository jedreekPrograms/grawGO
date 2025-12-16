package pl.edu.go.server.networkInterfaces;
/**
 * Listener do odbierania wiadomości od klienta.
 */
public interface MessageListener {

    /**
     * Wywoływana przy nadejściu wiadomości od klienta.
     * @param message treść wiadomości
     */
    void onMessage(String message);
}
