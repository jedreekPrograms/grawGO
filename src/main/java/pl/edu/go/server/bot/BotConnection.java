package pl.edu.go.server.bot;

import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.Color;

import java.net.Socket;

public class BotConnection implements ClientConnection {

    private GameSession session;
    private Color color;
    private final BotStrategy strategy;

    public BotConnection(BotStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void send(String message) {
        //System.out.println("[BOT] Received: " + message);

        if (message.equals("YOUR_TURN")) {
            makeMove();
            return;
        }

        if (message.startsWith("PASS "+ ((color == Color.BLACK) ? "WHITE" : "BLACK"))) {
            strategy.onOpponentPass();
            return;
        }
        if(message.startsWith("ILLEGAL MOVE")){
            makeMove();
        }
        if(message.startsWith("GAME RESUMED")){
            strategy.onGameResumed();
        }
        if(message.startsWith("PLAYER_ACCEPTED")){
            String cmd = strategy.onAccept();
            session.onMessage(this, cmd);
            return;
        }
    }


    private void makeMove() {
        for (int i = 0; i < 20; i++) { // limit prób
        String cmd = strategy.onTurn(session, color);
        //System.out.println("[BOT] Trying move: " + cmd);

        if (cmd == null || cmd.isBlank()) {
            continue;
        }
        session.onMessage(this, cmd);
        return;
        }

    // absolutny fallback
        session.onMessage(this, "PASS");
    }

    @Override
    public void setGameSession(GameSession session, Color color) {
        this.session = session;
        this.color = color;
    }

    @Override
    public void setMessageListener(pl.edu.go.server.networkInterfaces.MessageListener listener) {}

    @Override
    public Socket getSocket() {
        return null; // bot nie ma socketu
    }

    @Override
    public void close() {}

    @Override
    public void setPartner(ClientConnection partner) {}
}
