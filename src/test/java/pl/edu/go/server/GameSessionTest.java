package pl.edu.go.server;

import org.junit.Before;
import org.junit.Test;
import pl.edu.go.model.Color;
import pl.edu.go.model.GameState;
import pl.edu.go.server.commandInterfaces.CommandRegistry;
import pl.edu.go.server.commandInterfaces.GameCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameSessionTest {

    private GameSession session;
    private ClientConnection white;
    private ClientConnection black;
    private GameCommand command;
    private CommandRegistry registry;

    @Before
    public void setUp() {
        white = mock(ClientConnection.class);
        black = mock(ClientConnection.class);
        command = mock(GameCommand.class);
        registry = new CommandRegistry();

        registry.register("TEST", command);

        session = new GameSession(white, black, 9, registry);
    }

    @Test
    public void getPlayerColor_returnsCorrectColor() {
        assertEquals(Color.WHITE, session.getPlayerColor(white));
        assertEquals(Color.BLACK, session.getPlayerColor(black));
        assertNull(session.getPlayerColor(mock(ClientConnection.class)));
    }

    @Test
    public void handleCommand_executesRegisteredCommand() {
        when(command.execute(any(), eq(session), eq(white))).thenReturn(true);

        boolean result = session.handleCommand("TEST", new String[]{"1"}, white);

        assertTrue(result);
        verify(command).execute(any(), eq(session), eq(white));
    }

    @Test
    public void handleCommand_unknownCommand_returnsError() {
        boolean result = session.handleCommand("UNKNOWN", new String[]{}, white);

        assertFalse(result);
        verify(white).send("ERROR Unknown command: UNKNOWN");
    }

    @Test
    public void sendToBoth_sendsMessageToBothPlayers() {
        session.sendToBoth("HELLO");

        verify(white).send("HELLO");
        verify(black).send("HELLO");
    }

    @Test
    public void endSession_closesBothConnections() {
        session.endSession();

        verify(white).close();
        verify(black).close();
    }
}
