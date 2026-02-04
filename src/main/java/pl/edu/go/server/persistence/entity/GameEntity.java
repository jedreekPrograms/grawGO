package pl.edu.go.server.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Encja JPA reprezentująca pojedynczą rozgrywkę gry Go.
 * <p>
 * Przechowuje podstawowe informacje o grze, takie jak:
 * rozmiar planszy, czas rozpoczęcia i zakończenia rozgrywki
 * oraz zwycięzcę.
 * </p>
 */
@Entity
public class GameEntity {

    /**
     * Unikalny identyfikator gry.
     * Generowany automatycznie przez mechanizm JPA.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Rozmiar planszy (np. 9, 13, 19).
     */
    private int boardSize;

    /**
     * Data i czas rozpoczęcia gry.
     */
    private LocalDateTime startedAt;

    /**
     * Data i czas zakończenia gry.
     */
    private LocalDateTime finishedAt;

    /**
     * Zwycięzca gry (np. "BLACK", "WHITE", nazwa gracza).
     */
    private String winner;

    /**
     * Ustawia rozmiar planszy.
     *
     * @param boardSize rozmiar planszy gry
     */
    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    /**
     * Ustawia czas rozpoczęcia gry.
     *
     * @param now data i czas rozpoczęcia
     */
    public void setStartedAt(LocalDateTime now) {
        this.startedAt = now;
    }

    /**
     * Ustawia czas zakończenia gry.
     *
     * @param now data i czas zakończenia
     */
    public void setFinishedAt(LocalDateTime now) {
        this.finishedAt = now;
    }

    /**
     * Ustawia zwycięzcę gry.
     *
     * @param winner identyfikator zwycięzcy
     */
    public void setWinner(String winner) {
        this.winner = winner;
    }

    /**
     * Zwraca rozmiar planszy.
     *
     * @return rozmiar planszy
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Zwraca zwycięzcę gry.
     *
     * @return zwycięzca gry
     */
    public String getWinner() {
        return winner;
    }
}
