package pl.edu.go.server.commandInterfaces;

import org.junit.Test;
import pl.edu.go.model.Color;
import pl.edu.go.server.GameSession;

import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PassCommandTest {

    /*
    @Test
    public void testPassCommand() {
        PassCommand cmd = new PassCommand();
        GameSession session = mock(GameSession.class);
        ClientConnection sender = mock(ClientConnection.class);


        when(session.getPlayerColor(sender)).thenReturn(Color.BLACK);
        when(session.getGame()).thenReturn(new pl.edu.go.model.GameState(9));


        boolean result = cmd.execute(new String[]{}, session, sender);
        assertTrue(result);
        verify(session).sendToBoth("PASS BLACK");
    }*/
}