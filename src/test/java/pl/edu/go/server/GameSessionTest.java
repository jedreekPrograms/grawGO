package pl.edu.go.server;


import org.junit.*;
import pl.edu.go.model.Color;

import pl.edu.go.server.commandInterfaces.*;
import pl.edu.go.server.networkInterfaces.ClientConnection;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameSessionTest {


    private ClientConnection white;
    private ClientConnection black;
    private CommandRegistry registry;
    private GameSession session;


    @Before
    public void setup() {
        white = mock(ClientConnection.class);
        black = mock(ClientConnection.class);
        registry = new CommandRegistry();
        registry.register("MOVE", new MoveCommand());
        registry.register("PASS", new PassCommand());
        registry.register("RESIGN", new ResignCommand());
        session = new GameSession(white, black, 9, registry);
    }


    @Test
    public void testPlayerColorsAssigned() {
        assertEquals(Color.WHITE, session.getPlayerColor(white));
        assertEquals(Color.BLACK, session.getPlayerColor(black));
    }


    @Test
    public void testMoveOutOfTurnRejected() {
        session.onMessage(white, "MOVE 1 1");
        verify(white).send(startsWith("ERROR"));
    }


    @Test
    public void testValidMoveNotifiesOpponent() {
        session.start(); // BLACK starts
        session.onMessage(black, "MOVE 2 2");
        verify(black).send("VALID");
        verify(white).send("OPPONENT_MOVED 2 2");
    }


    @Test
    public void testResignEndsSession() {
        session.onMessage(black, "RESIGN");
        verify(black).send(contains("RESIGN"));
        verify(white).send(contains("WINNER"));
        verify(white).close();
        verify(black).close();
    }
}
