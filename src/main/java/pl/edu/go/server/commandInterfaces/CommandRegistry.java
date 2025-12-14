package pl.edu.go.server.commandInterfaces;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, GameCommand> commands = new HashMap<>();

    public void register(String name, GameCommand command) {
        commands.put(name.toUpperCase(), command);
    }

    public GameCommand get(String name) {
        return commands.get(name.toUpperCase());
    }
}
