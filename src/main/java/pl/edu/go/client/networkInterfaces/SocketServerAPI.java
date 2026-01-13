package pl.edu.go.client.networkInterfaces;

import java.io.*;
import java.net.*;

/**
 * Domyślna implementacja interfejsu {@link ServerAPI} wykorzystująca gniazda TCP/IP.
 *
 * <p>
 * SocketServerAPI odpowiada za:
 * <ul>
 *     <li>połączenie z serwerem pod wskazanym hostem i portem</li>s
 *     <li>wysyłanie wiadomości do serwera</li>
 *     <li>odbieranie wiadomości od serwera w osobnym wątku i przekazywanie ich do {@link MessageListener}</li>
 * </ul>
 * </p>
 */
public class SocketServerAPI implements ServerAPI {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    /** Listener do obsługi wiadomości od serwera. */
    private MessageListener listener;

    /** Wątek odbierający wiadomości od serwera. */
    private Thread readerThread;

    /**
     * Tworzy połączenie z serwerem pod wskazanym hostem i portem.
     * Uruchamia wątek odbioru wiadomości.
     *
     * @param host adres serwera
     * @param port port serwera
     * @throws IOException jeśli połączenie nie powiedzie się
     */
    public SocketServerAPI(String host, int port) throws IOException {
        this.socket = new Socket(host, port);

        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Wątek odbioru danych
        readerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (listener != null) {
                        listener.onMessage(line);
                    }
                }
            } catch (IOException ignored) {}
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    /**
     * Wysyła wiadomość do serwera.
     *
     * @param message tekst wiadomości
     */
    @Override
    public void send(String message) {
        out.println(message);
    }

    /**
     * Rejestruje listener do odbioru wiadomości od serwera.
     *
     * @param listener obiekt implementujący {@link MessageListener}
     */
    @Override
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    /**
     * Zamyka połączenie z serwerem.
     */
    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
