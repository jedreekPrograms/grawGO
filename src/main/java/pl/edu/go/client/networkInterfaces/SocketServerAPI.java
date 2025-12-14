package pl.edu.go.client.networkInterfaces;
import pl.edu.go.client.networkInterfaces.ServerAPI;

import java.io.*;
import java.net.*;
//Domyślna implementacja interfejsu ServerAPI
public class SocketServerAPI implements ServerAPI {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    private MessageListener listener;
    private Thread readerThread;

    public SocketServerAPI(String host, int port) throws IOException {
        this.socket = new Socket(host, port);

        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Wątek odbioru danych
        readerThread= new Thread(() -> {
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

    @Override
    public void send(String message) {
        out.println(message);
    }

    @Override
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
