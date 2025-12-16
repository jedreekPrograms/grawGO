package pl.edu.go.server.commandInterfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.MockClientConnection;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResignCommandTest {

    private ResignCommand resignCommand;
    private GameSession session;
    private MockClientConnection player1;
    private MockClientConnection player2;

    @BeforeEach
    void setUp() {
        resignCommand = new ResignCommand();
        player1 = new MockClientConnection();
        player2 = new MockClientConnection();
        session = new GameSession(player1, player2, 19, new CommandRegistry());
        session.start();
        player1.setGameSession(session, player1.assignedColor = pl.edu.go.model.Color.WHITE);
        player2.setGameSession(session, player2.assignedColor = pl.edu.go.model.Color.BLACK);
    }

    @Test
    void testExecuteResign() {
        resignCommand.execute(new String[]{}, session, player1);
        assertTrue(player1.messages.contains("RESIGN WHITE"));
        assertTrue(player2.messages.contains("WINNER BLACK"));
    }
}
