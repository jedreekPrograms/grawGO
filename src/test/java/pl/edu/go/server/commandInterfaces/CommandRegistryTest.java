package pl.edu.go.server.commandInterfaces;

import org.junit.Test;
import pl.edu.go.server.CommandRegistry;
import pl.edu.go.server.GameCommand;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class CommandRegistryTest {


    @Test
    public void testRegisterAndGet() {
        CommandRegistry registry = new CommandRegistry();
        GameCommand command = mock(GameCommand.class);


        registry.register("MOVE", command);
        assertSame(command, registry.get("MOVE"));
        assertSame(command, registry.get("move"));
    }
}