package pl.edu.go.server.commandInterfaces;

import org.junit.Before;
import org.junit.Test;
import pl.edu.go.model.*;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PassCommandTest {

    private PassCommand command;
    private GameSession session;
    private GameState game;
    private ClientConnection sender;

    @Before
    public void setUp() {
        command = new PassCommand();
        session = mock(GameSession.class);
        game = mock(GameState.class);
        sender = mock(ClientConnection.class);

        when(session.getGame()).thenReturn(game);
        when(session.getPlayerColor(sender)).thenReturn(Color.BLACK);
        when(game.getNextToMove()).thenReturn(Color.BLACK);
    }

    @Test
    public void execute_failsIfNotPlayersTurn() {
        when(game.getNextToMove()).thenReturn(Color.WHITE);

        boolean result = command.execute(new String[]{}, session, sender);

        assertFalse(result);
    }

    @Test
    public void execute_firstPass_gameContinues() {
        when(game.applyMove(any())).thenReturn(true);
        when(game.getStatus()).thenReturn(GameState.Status.PLAYING);

        boolean result = command.execute(new String[]{}, session, sender);

        assertTrue(result);
        verify(session).sendToBoth("PASS BLACK");
        verify(game).setNextToMove(Color.WHITE);
    }

    @Test
    public void execute_secondPass_gameStops() {
        when(game.applyMove(any())).thenReturn(true);
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);

        boolean result = command.execute(new String[]{}, session, sender);

        assertTrue(result);
        verify(session).sendToBoth("PASS BLACK");
        verify(session).sendToBoth("STOPPED");
        verify(game, never()).setNextToMove(any());
    }
}
