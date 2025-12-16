package pl.edu.go.server.commandInterfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.go.model.Color;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.MockClientConnection;

import static org.junit.jupiter.api.Assertions.*;

class PassCommandTest {

    private PassCommand passCommand;
    private GameSession session;
    private MockClientConnection player1;
    private MockClientConnection player2;

    @BeforeEach
    void setUp() {
        passCommand = new PassCommand();
        player1 = new MockClientConnection();
        player2 = new MockClientConnection();
        CommandRegistry registry = new CommandRegistry();
        registry.register("PASS", passCommand);
        session = new GameSession(player1, player2, 9, registry);

        // poprawiona kolejność
        player1.setGameSession(session, Color.WHITE);
        player2.setGameSession(session, Color.BLACK);
    }

    @Test
    void testExecutePass() {
        boolean result = passCommand.execute(new String[]{}, session, player1);
        assertTrue(result);
        assertTrue(player1.lastMessage.startsWith("PASS"));
    }
}
