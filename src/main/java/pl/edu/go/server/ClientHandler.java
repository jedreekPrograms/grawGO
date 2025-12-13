package pl.edu.go.server;

import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.networkInterfaces.MessageListener;
import pl.edu.go.model.Color;
import pl.edu.go.model.Move;
import pl.edu.go.model.Point;

import java.io.*;
import java.net.Socket;

//Domyslna implementacja komunikacji z klientem i obslugi klientow
public class ClientHandler implements Runnable, ClientConnection {

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageListener listener;
    private ClientConnection partner;
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
    public void run() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                // Przekaż komunikat do sesji, jeśli jest przypisana
                // Można logować lub reagować wstępnie
                System.out.println("Wiadomość przed przypisaniem do sesji: " + msg);
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
