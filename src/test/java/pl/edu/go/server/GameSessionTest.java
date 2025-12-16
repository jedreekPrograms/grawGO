package pl.edu.go.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.go.model.Color;
import pl.edu.go.server.commandInterfaces.CommandRegistry;
import pl.edu.go.server.commandInterfaces.MoveCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    private GameSession session;
    private MockClientConnection player1;
    private MockClientConnection player2;
    private CommandRegistry registry;

    @BeforeEach
    void setUp() {
        player1 = new MockClientConnection();
        player2 = new MockClientConnection();
        registry = new CommandRegistry();
        registry.register("MOVE", new MoveCommand());
        session = new GameSession(player1, player2, 19, registry);
        session.start();
        session.getClientByColor(Color.WHITE).setGameSession(session, Color.WHITE);
        session.getClientByColor(Color.BLACK).setGameSession(session, Color.BLACK);
    }

    @Test
    void testPlayerColors() {
        assertEquals(Color.WHITE, session.getPlayerColor(player1));
        assertEquals(Color.BLACK, session.getPlayerColor(player2));
    }

    @Test
    void testHandleCommandMove() {
        // gracz WHITE zaczyna
        boolean result = session.handleCommand("MOVE", new String[]{"0","0"}, player1);
        assertTrue(result);
        assertTrue(player1.messages.contains("VALID") || player1.messages.contains("START WHITE"));
        assertTrue(player2.messages.stream().anyMatch(m -> m.startsWith("OPPONENT_MOVED")));
    }

    @Test
    void testHandleCommandPass() {
        session.handleCommand("PASS", new String[]{}, player1);
        assertTrue(player1.messages.contains("VALID") || player1.messages.contains("START WHITE"));
    }
}
