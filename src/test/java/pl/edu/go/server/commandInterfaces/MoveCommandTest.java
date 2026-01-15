package pl.edu.go.server.commandInterfaces;

import org.junit.Before;
import org.junit.Test;
import pl.edu.go.model.*;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MoveCommandTest {

    private MoveCommand command;
    private GameSession session;
    private GameState game;
    private Board board;
    private ClientConnection sender;

    @Before
    public void setUp() {
        command = new MoveCommand();
        session = mock(GameSession.class);
        game = mock(GameState.class);
        board = mock(Board.class);
        sender = mock(ClientConnection.class);

        when(session.getGame()).thenReturn(game);
        when(game.getBoard()).thenReturn(board);
        when(session.getPlayerColor(sender)).thenReturn(Color.BLACK);
        when(game.getNextToMove()).thenReturn(Color.BLACK);
    }

    @Test
    public void execute_failsOnWrongArgumentCount() {
        boolean result = command.execute(new String[]{"1"}, session, sender);

        assertFalse(result);
        verify(sender).send("ERROR MOVE x y");
    }

    @Test
    public void execute_failsIfNotPlayersTurn() {
        when(game.getNextToMove()).thenReturn(Color.WHITE);

        boolean result = command.execute(new String[]{"1", "2"}, session, sender);

        assertFalse(result);
        verify(sender).send("ERROR Not your turn");
    }

    @Test
    public void execute_failsOnIllegalMove() {
        when(game.applyMove(any())).thenReturn(false);

        boolean result = command.execute(new String[]{"1", "2"}, session, sender);

        assertFalse(result);
        verify(sender).send("ILLEGAL MOVE");
    }

    @Test
    public void execute_successfulMove_blackMoves_whiteNext() {
        when(game.applyMove(any())).thenReturn(true);
        when(board.getTotalCaptured()).thenReturn(2);

        boolean result = command.execute(new String[]{"3", "4"}, session, sender);

        assertTrue(result);
        verify(game).setNextToMove(Color.WHITE);
        verify(session).sendToBoth("MOVE BLACK 3 4 2");
        verify(session).sendBoardToBoth();
    }
}
