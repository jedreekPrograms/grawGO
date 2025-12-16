package pl.edu.go.server.commandInterfaces;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandRegistryTest {

    @Test
    void testRegisterAndGetCommand() {
        CommandRegistry registry = new CommandRegistry();
        GameCommand dummyCommand = (args, session, sender) -> true;

        registry.register("MOVE", dummyCommand);

        assertEquals(dummyCommand, registry.get("MOVE"));
        assertEquals(dummyCommand, registry.get("move")); // case insensitive
        assertNull(registry.get("UNKNOWN"));
    }
}
