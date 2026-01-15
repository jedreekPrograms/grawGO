package pl.edu.go.server.commandInterfaces;

import org.junit.Before;
import org.junit.Test;
import pl.edu.go.model.GameState;
import pl.edu.go.model.Point;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DeadCommandTest {

    private DeadCommand command;
    private GameSession session;
    private GameState game;
    private ClientConnection sender;

    @Before
    public void setUp() {
        command = new DeadCommand();
        session = mock(GameSession.class);
        game = mock(GameState.class);
        sender = mock(ClientConnection.class);

        when(session.getGame()).thenReturn(game);
    }



    // =========================
    // ❌ ZŁA LICZBA ARGUMENTÓW
    // =========================
    @Test
    public void execute_failsIfWrongArgumentCount() {
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);

        boolean result = command.execute(new String[]{"1"}, session, sender);

        assertFalse(result);
        verify(sender).send("ERROR Usage: DEAD x y");
    }

    // =========================
    // ❌ NIEPOPRAWNE LICZBY
    // =========================
    @Test
    public void execute_failsIfArgumentsAreNotNumbers() {
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);

        boolean result = command.execute(new String[]{"x", "y"}, session, sender);

        assertFalse(result);
        verify(sender).send("ERROR Coordinates must be numbers");
    }

    // =========================
    // ❌ NIE MOŻNA OZNACZYĆ PUNKTU
    // =========================
    @Test
    public void execute_failsIfGameRejectsDeadStone() {
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);
        when(game.toggleDeadStone(new Point(3, 4))).thenReturn(false);

        boolean result = command.execute(new String[]{"3", "4"}, session, sender);

        assertFalse(result);
        verify(sender).send("ERROR Cannot mark this point");
    }

    // =========================
    // ✅ POPRAWNE OZNACZENIE
    // =========================
    @Test
    public void execute_marksDeadStoneSuccessfully() {
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);
        when(game.toggleDeadStone(new Point(2, 5))).thenReturn(true);

        boolean result = command.execute(new String[]{"2", "5"}, session, sender);

        assertTrue(result);
        verify(session).sendToBoth("DEAD_MARKED 2 5");
        verify(session).sendBoardToBoth();
    }
}
