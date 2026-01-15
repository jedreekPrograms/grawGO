package pl.edu.go.server.commandInterfaces;

import org.junit.Before;
import org.junit.Test;
import pl.edu.go.model.Color;
import pl.edu.go.model.GameState;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AcceptCommandTest {

    private AcceptCommand command;
    private GameSession session;
    private GameState game;
    private ClientConnection sender;

    @Before
    public void setUp() {
        command = new AcceptCommand();
        session = mock(GameSession.class);
        game = mock(GameState.class);
        sender = mock(ClientConnection.class);

        when(session.getGame()).thenReturn(game);
        when(session.getPlayerColor(sender)).thenReturn(Color.BLACK);
    }

    @Test
    public void execute_failsIfGameNotStopped() {
        when(game.getStatus()).thenReturn(GameState.Status.PLAYING);

        boolean result = command.execute(new String[]{}, session, sender);

        assertFalse(result);
        verify(sender).send("ERROR Game is not in scoring phase");
    }

    @Test
    public void execute_firstAccept_sendsPlayerAccepted() {
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);
        when(game.accept(Color.BLACK)).thenReturn(false);

        boolean result = command.execute(new String[]{}, session, sender);

        assertTrue(result);
        verify(session).sendToBoth("PLAYER_ACCEPTED BLACK");
        verify(session, never()).endSession();
    }


}
