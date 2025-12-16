package pl.edu.go.server;

import pl.edu.go.model.Color;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import java.util.ArrayList;
import java.util.List;

public class MockClientConnection implements ClientConnection {

    public List<String> messages = new ArrayList<>();
    public Color assignedColor;
    public GameSession session;

    @Override
    public void send(String message) {
        messages.add(message);
    }

    @Override
    public void setGameSession(GameSession session, Color color) {
        this.session = session;
        this.assignedColor = color;
        send("START " + color.name());
    }
}
