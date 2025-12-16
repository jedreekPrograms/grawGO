package pl.edu.go.server.commandInterfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.MockClientConnection;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveCommandTest {

    private MoveCommand moveCommand;
    private GameSession session;
    private MockClientConnection player1;
    private MockClientConnection player2;

    @BeforeEach
    void setUp() {
        moveCommand = new MoveCommand();
        player1 = new MockClientConnection();
        player2 = new MockClientConnection();
        session = new GameSession(player1, player2, 19, new CommandRegistry());
        session.start();
        player1.setGameSession(session, player1.assignedColor = pl.edu.go.model.Color.WHITE);
        player2.setGameSession(session, player2.assignedColor = pl.edu.go.model.Color.BLACK);
    }

    @Test
    void testExecuteValidMove() {
        boolean result = moveCommand.execute(new String[]{"0","0"}, session, player1);
        assertTrue(result);
    }
}
