package pl.edu.go.server.commandInterfaces;

import org.junit.Test;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.MoveCommand;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MoveCommandTest {


    @Test
    public void testInvalidArgs() {
        MoveCommand cmd = new MoveCommand();
        ClientConnection sender = mock(ClientConnection.class);
        GameSession session = mock(GameSession.class);


        boolean result = cmd.execute(new String[]{"1"}, session, sender);
        assertFalse(result);
        verify(sender).send(startsWith("ERROR"));
    }
}