package pl.edu.go.server.commandInterfaces;

import org.junit.Before;
import org.junit.Test;
import pl.edu.go.model.Color;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FinalCommandTest {

    private FinalCommand command;
    private GameSession session;
    private ClientConnection sender;

    @Before
    public void setUp() {
        command = new FinalCommand();
        session = mock(GameSession.class);
        sender = mock(ClientConnection.class);
    }

    @Test
    public void execute_sendsFinalWithPlayerColor() {
        when(session.getPlayerColor(sender)).thenReturn(Color.WHITE);

        boolean result = command.execute(new String[]{}, session, sender);

        assertTrue(result);
        verify(session).sendToBoth("FINAL WHITE");
    }
}
