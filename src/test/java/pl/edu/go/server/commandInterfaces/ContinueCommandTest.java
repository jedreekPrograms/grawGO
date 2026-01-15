package pl.edu.go.server.commandInterfaces;

import org.junit.Before;
import org.junit.Test;
import pl.edu.go.model.Color;
import pl.edu.go.model.GameState;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ContinueCommandTest {

    private ContinueCommand command;
    private GameSession session;
    private GameState game;
    private ClientConnection sender;

    @Before
    public void setUp() {
        command = new ContinueCommand();
        session = mock(GameSession.class);
        game = mock(GameState.class);
        sender = mock(ClientConnection.class);

        when(session.getGame()).thenReturn(game);
    }

    @Test
    public void execute_failsIfGameNotStopped() {
        when(game.getStatus()).thenReturn(GameState.Status.PLAYING);

        boolean result = command.execute(new String[]{}, session, sender);

        assertFalse(result);
        verify(sender).send("ERROR Game is not stopped");
    }

    @Test
    public void execute_blackRequestsContinue_whiteMovesNext() {
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);
        when(session.getPlayerColor(sender)).thenReturn(Color.BLACK);

        boolean result = command.execute(new String[]{}, session, sender);

        assertTrue(result);
        verify(game).requestResume();
        verify(game).setNextToMove(Color.WHITE);
        verify(session).sendToBoth("GAME_RESUMED WHITE");
        verify(session).sendBoardToBoth();
    }
}
