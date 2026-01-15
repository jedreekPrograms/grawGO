package pl.edu.go.server.commandInterfaces;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CommandRegistryTest {

    @Test
    public void registerAndGetCommand_caseInsensitive() {
        CommandRegistry registry = new CommandRegistry();
        GameCommand cmd = mock(GameCommand.class);

        registry.register("accept", cmd);

        assertSame(cmd, registry.get("ACCEPT"));
        assertSame(cmd, registry.get("accept"));
        assertSame(cmd, registry.get("AcCePt"));
    }

    @Test
    public void get_returnsNullForUnknownCommand() {
        CommandRegistry registry = new CommandRegistry();
        assertNull(registry.get("UNKNOWN"));
    }
}
