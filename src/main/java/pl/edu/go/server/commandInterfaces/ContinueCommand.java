package pl.edu.go.server.commandInterfaces;

import pl.edu.go.model.Color;
import pl.edu.go.model.GameState;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;

/**
 * Komenda serwera obsługująca żądanie wznowienia gry.
 * Jest używana w fazie STOPPED (podczas oznaczania martwych kamieni), gdy gracz
 * zdecyduje, że chce kontynuować kładzenie kamieni zamiast kończyć partię.
 */
public class ContinueCommand implements GameCommand {

    /**
     * Wykonuje logikę wznowienia gry.
     * Zmienia status gry z powrotem na PLAYING, ustala, czyja jest tura (przeciwnika osoby
     * wznawiającej) i informuje obu graczy o aktualnym stanie planszy.
     *
     * @param args Tablica argumentów komendy (zazwyczaj pusta).
     * @param session Aktualna sesja gry, w której uczestniczy gracz.
     * @param sender Połączenie klienta wysyłającego komendę żądania wznowienia.
     * @return true, jeśli gra została pomyślnie wznowiona; 
     * false, jeśli gra nie znajdowała się w stanie wstrzymania.
     */
    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        GameState game = session.getGame();

        // Wznowienie jest możliwe tylko wtedy, gdy gra jest w fazie oznaczania martwych kamieni
        if (game.getStatus() != GameState.Status.STOPPED) {
            sender.send("ERROR Game is not stopped");
            return false;
        }

        //Wyczyszczenie martwych kamieni i powrót do statusu PLAYING
        game.requestResume();
        Color c;
        // Ustalenie, że tura przypada graczowi, który NIE wysłał komendy CONTINUE
        if(session.getPlayerColor(sender) == Color.BLACK){
            game.setNextToMove(Color.WHITE);
            c = Color.WHITE;
        }else{
            game.setNextToMove(Color.BLACK);
            c = Color.BLACK;
        }
        // Powiadomienie obu graczy o wznowieniu gry i przesłanie aktualnego wyglądu planszy
        session.sendToBoth("GAME_RESUMED " + c);
        session.sendBoardToBoth();
        return true;
    }
}
