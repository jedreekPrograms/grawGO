package pl.edu.go.server;

import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.networkInterfaces.MessageListener;
import pl.edu.go.model.Color;


import java.io.*;
import java.net.Socket;

/**
 * Klasa odpowiedzialna za obsługę pojedynczego połączenia klienta w systemie serwera Go.
 * Implementuje interfejs Runnable, co pozwala na uruchomienie obsługi każdego klienta
 * w osobnym wątku, umożliwiając równoczesną komunikację z wieloma graczami.
 */
public class ClientHandler implements Runnable, ClientConnection {
    /** Gniazdo (socket) utrzymujące połączenie z konkretnym klientem. */
    private final Socket socket;

    /** Strumień wejściowy do odczytywania komunikatów tekstowych od klienta. */
    private BufferedReader in;

    /** Strumień wyjściowy do wysyłania komunikatów tekstowych do klienta. */
    private PrintWriter out;
    private MessageListener listener;
    private ClientConnection partner;
    private GameSession session;
    private Color assignedColor;

    /**
     * Inicjalizuje obsługę klienta, tworząc strumienie wejścia i wyjścia na podstawie gniazda.
     * * @param socket Otwarte gniazdo TCP połączone z klientem.
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Błąd inicjalizacji klienta" + e.getMessage());
        }
    }
    /**
     * Przypisuje klienta do konkretnej sesji gry i nadaje mu kolor.
     * Natychmiast wysyła do klienta komunikat START z informacją o jego kolorze.
     * * @param session Obiekt sesji gry zarządzający logiką partii.
     * @param color Kolor przydzielony temu graczowi.
     */
    @Override
    public void setGameSession(GameSession session, Color color) {
        this.session = session;
        this.assignedColor = color;
        // poinformuj klienta jaki kolor dostał i że sesja jest gotowa
        send("START " + color.name());
    }

    /**
     * Główna pętla wątku obsługującego klienta.
     * Nasłuchuje na przychodzące wiadomości w sposób blokujący i przekazuje je 
     * do aktywnej sesji gry w celu ich przetworzenia.
     */
    @Override
    public void run() {
        try {
            String msg;
            // Odczyt linii tekstu do momentu rozłączenia lub błędu
            while ((msg = in.readLine()) != null) {
                if (session != null) {
                    // Przekazanie surowego komunikatu do logiki sesji
                    session.onMessage(this, msg);
                } else {
                    System.out.println("Wiadomość przed przypisaniem do sesji: " + msg);
                }
            }
        } catch (IOException e) {
            System.out.println("Klient rozłączony: " + socket);
        } finally {
            close();
        }
    }
    /**
     * Ustawia słuchacza komunikatów dla tego połączenia.
     * * @param listener Obiekt implementujący logikę reakcji na wiadomość.
     */
    @Override
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }
    /**
     * Ustawia połączenie z drugim graczem (partnerem) w grze.
     * * @param partner Obiekt połączenia przeciwnika.
     */
    public void setPartner(ClientConnection partner) {
        this.partner = partner;
    }
    /**
     * Wysyła komunikat tekstowy bezpośrednio do klienta przez gniazdo TCP.
     * * @param msg Treść komunikatu do wysłania.
     */
    @Override
    public void send(String msg) {
        out.println(msg);
    }
    /** @return Gniazdo sieciowe skojarzone z tym klientem. */
    public Socket getSocket() {
        return socket;
    }
    /**
     * Bezpiecznie zamyka połączenie z klientem i zwalnia zasoby gniazda.
     */
    public void close() {
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
    /** @return Obiekt PrintWriter umożliwiający zapis do strumienia wyjściowego. */
    public PrintWriter getOut() {
        return out;
    }
    /** @return Kolor przypisany temu graczowi (BLACK/WHITE). */
    public Color getAssignedColor() {
        return assignedColor;
    }
}
