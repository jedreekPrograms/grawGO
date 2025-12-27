package pl.edu.go.server;

import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.networkInterfaces.MessageListener;
import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.Point;
import pl.edu.go.server.GameSession;

import java.io.*;
import java.net.Socket;

/**
 * Domyślna implementacja ClientConnection.
 * Obsługuje klienta w osobnym wątku i komunikację TCP.
 */
public class ClientHandler implements Runnable, ClientConnection {

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageListener listener;
    private ClientConnection partner;
    private GameSession session;
    private Color assignedColor;


    public ClientHandler(Socket socket) {
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Błąd inicjalizacji klienta" + e.getMessage());
        }
    }

    @Override
    public void setGameSession(GameSession session, Color color) {
        this.session = session;
        this.assignedColor = color;
        // poinformuj klienta jaki kolor dostał i że sesja jest gotowa
        send("START " + color.name());
    }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                if (session != null) {

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
    
    @Override
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public void setPartner(ClientConnection partner) {
        this.partner = partner;
    }

    @Override
    public void send(String msg) {
        out.println(msg);
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() {
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    public PrintWriter getOut() {
        return out;
    }

    public Color getAssignedColor() {
        return assignedColor;
    }
}
