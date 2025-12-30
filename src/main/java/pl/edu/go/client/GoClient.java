package pl.edu.go.client;

import javafx.application.Platform;
import pl.edu.go.client.networkInterfaces.ServerAPI;
import pl.edu.go.client.networkInterfaces.SocketServerAPI;

import java.io.IOException;

public class GoClient {

    private final ServerAPI api;
    private final GoBoardDemo ui;

    public GoClient(String host, int port, GoBoardDemo ui) throws IOException {
        this.api = new SocketServerAPI(host, port);
        this.ui = ui;
    }

    public void run() {
        api.setMessageListener(msg ->
                Platform.runLater(() -> ui.handleServerMessage(msg))
        );
    }

    public void sendMove(int x, int y) {
        api.send("MOVE " + x + " " + y);
    }

    public void sendPass() {
        api.send("PASS");
    }

    public void sendResign() {
        api.send("RESIGN");
    }
}
