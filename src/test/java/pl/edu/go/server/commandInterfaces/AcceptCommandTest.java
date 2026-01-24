package pl.edu.go.server.commandInterfaces;

import org.junit.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import pl.edu.go.server.commandInterfaces.AcceptCommand;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.*;
import pl.edu.go.model.TerritoryCalculator.GameResult;

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
    public void shouldRejectWhenGameNotStopped() {
        when(game.getStatus()).thenReturn(GameState.Status.PLAYING);

        boolean result = command.execute(new String[]{}, session, sender);

        assertFalse(result);
        verify(sender).send("ERROR Game is not in scoring phase");
    }

    @Test
    public void shouldAcceptFirstPlayerOnly() {
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);
        when(game.accept(Color.BLACK)).thenReturn(false);

        boolean result = command.execute(new String[]{}, session, sender);

        assertTrue(result);
        verify(session).sendToBoth("PLAYER_ACCEPTED BLACK");
        verify(game, never()).confirmEndGame();
    }


   @Test
public void shouldEndGameWhenBothAccepted() {
    when(game.getStatus()).thenReturn(GameState.Status.STOPPED);
    when(game.accept(Color.BLACK)).thenReturn(true);

    GameResult resultMock = mock(GameResult.class);
    when(resultMock.blackScore()).thenReturn(10.0);
    when(resultMock.whiteScore()).thenReturn(8.5);
    when(resultMock.winner()).thenReturn("BLACK");   // ✅ POPRAWKA
    when(game.getFinalResult()).thenReturn(resultMock);

    boolean result = command.execute(new String[]{}, session, sender);

    assertTrue(result);
    verify(game).confirmEndGame();
    verify(session).sendToBoth("GAME_END 10.0 8.5 BLACK");
    verify(session).endSession();
}

}
