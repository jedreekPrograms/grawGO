package pl.edu.go.server.persistence.service;

import org.springframework.stereotype.Service;
import pl.edu.go.model.Color;
import pl.edu.go.server.persistence.entity.GameEntity;
import pl.edu.go.server.persistence.entity.MoveEntity;
import pl.edu.go.server.persistence.entity.MoveType;
import pl.edu.go.server.persistence.repository.GameRepository;
import pl.edu.go.server.persistence.repository.MoveRepository;

import java.time.LocalDateTime;

/**
 * Serwis odpowiedzialny za logikę zapisu i odczytu danych
 * związanych z przebiegiem gry Go.
 * <p>
 * Stanowi warstwę pośrednią pomiędzy logiką biznesową
 * a warstwą persystencji (repozytoria JPA).
 * </p>
 */
@Service
public class GamePersistenceService {

    /**
     * Repozytorium gier.
     */
    private final GameRepository gameRepo;

    /**
     * Repozytorium ruchów.
     */
    private final MoveRepository moveRepo;

    /**
     * Tworzy serwis persystencji gry.
     *
     * @param g repozytorium gier
     * @param m repozytorium ruchów
     */
    public GamePersistenceService(GameRepository g, MoveRepository m) {
        this.gameRepo = g;
        this.moveRepo = m;
    }

    /**
     * Rozpoczyna nową grę i zapisuje ją w bazie danych.
     *
     * @param boardSize rozmiar planszy (np. 9, 13, 19)
     * @return zapisana encja nowej gry
     */
    public GameEntity startGame(int boardSize) {
        GameEntity g = new GameEntity();
        g.setBoardSize(boardSize);
        g.setStartedAt(LocalDateTime.now());
        return gameRepo.save(g);
    }

    /**
     * Zapisuje pojedynczy ruch w danej grze.
     *
     * @param g    gra, do której należy ruch
     * @param nr   numer ruchu
     * @param x    współrzędna X (może być {@code null} dla ruchów specjalnych)
     * @param y    współrzędna Y (może być {@code null} dla ruchów specjalnych)
     * @param c    kolor gracza wykonującego ruch
     * @param type typ ruchu
     */
    public void saveMove(GameEntity g, int nr, Integer x, Integer y, Color c, MoveType type) {
        MoveEntity m = new MoveEntity();
        m.setGame(g);
        m.setMoveNumber(nr);
        m.setX(x);
        m.setY(y);
        m.setColor(c);
        m.setType(type);
        moveRepo.save(m);
    }

    /**
     * Zakańcza grę, ustawiając czas zakończenia oraz zwycięzcę,
     * a następnie zapisuje zmiany w bazie danych.
     *
     * @param g      encja gry
     * @param winner identyfikator zwycięzcy
     */
    public void finishGame(GameEntity g, String winner) {
        g.setFinishedAt(LocalDateTime.now());
        g.setWinner(winner);
        gameRepo.save(g);
    }
}
