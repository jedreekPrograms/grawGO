package pl.edu.go.server.commandInterfaces;

import org.junit.Test;
import pl.edu.go.model.Color;
import pl.edu.go.server.GameSession;

import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ResignCommandTest {


    @Test
    public void testResignCommand() {
        ResignCommand cmd = new ResignCommand();
        GameSession session = mock(GameSession.class);
        ClientConnection sender = mock(ClientConnection.class);


        when(session.getPlayerColor(sender)).thenReturn(Color.WHITE);
        when(session.getGame()).thenReturn(new pl.edu.go.model.GameState(9));


        boolean result = cmd.execute(new String[]{}, session, sender);
        assertTrue(result);
        verify(session).sendToBoth("RESIGN WHITE");
        verify(session).sendToBoth("WINNER BLACK");
        verify(session).endSession();
    }
}