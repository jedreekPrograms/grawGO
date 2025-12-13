package pl.edu.go.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkHandler {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public NetworkHandler(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = out;
        this.in = in;
    }
}
