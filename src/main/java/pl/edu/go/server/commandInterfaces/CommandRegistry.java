package pl.edu.go.server.commandInterfaces;

import java.util.HashMap;
import java.util.Map;


/**
 * Rejestr komend gry. Pozwala zarejestrować implementacje komend
 * i pobierać je po nazwie
 */
public class CommandRegistry {

    private final Map<String, GameCommand> commands = new HashMap<>();

     /**
     * Rejestruje komendę pod daną nazwą.
     * @param name nazwa komendy
     * @param command implementacja komendy
     */
    public void register(String name, GameCommand command) {
        commands.put(name.toUpperCase(), command);
    }

     /**
     * Pobiera zarejestrowaną komendę po nazwie.
     * @param name nazwa komendy
     * @return implementacja komendy lub null, jeśli nie istnieje
     */
    public GameCommand get(String name) {
        return commands.get(name.toUpperCase());
    }
}
