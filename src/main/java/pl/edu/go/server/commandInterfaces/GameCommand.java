package pl.edu.go.server.commandInterfaces;


import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
/**
 * Interfejs dla wszystkich komend gry Go.
 * Każda komenda implementuje metodę execute, która wykonuje akcję
 * w kontekście sesji gry i klienta wysyłającego komendę.
 */
public interface GameCommand {
     /**
     * Wykonuje komendę.
     * @param args argumenty komendy
     * @param session sesja gry
     * @param sender klient, który wysłał komendę
     * @return true jeśli komenda wykonana poprawnie, false w przeciwnym wypadku
     */
    boolean execute(String[] args, GameSession session, ClientConnection sender);
}
