package pl.edu.go.client.networkInterfaces;

/**
 * Interfejs abstrakcyjny do komunikacji klienta z serwerem Go.
 *
 * <p>
 * Implementacje tego interfejsu odpowiadają za:
 * <ul>
 *     <li>wysyłanie wiadomości do serwera</li>
 *     <li>odbieranie wiadomości od serwera przez listener</li>
 *     <li>zamknięcie połączenia</li>
 * </ul>
 * </p>
 */
public interface ServerAPI {

    /**
     * Wysyła wiadomość do serwera.
     *
     * @param message tekst wiadomości
     */
    void send(String message);

    /**
     * Rejestruje listener odbierający wiadomości z serwera.
     *
     * @param listener obiekt implementujący {@link MessageListener}
     */
    void setMessageListener(MessageListener listener);

    /**
     * Zamyka połączenie z serwerem.
     */
    void close();
}
