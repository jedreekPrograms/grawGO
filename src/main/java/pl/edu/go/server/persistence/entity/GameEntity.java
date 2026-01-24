package pl.edu.go.server.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class GameEntity {
    @Id
    @GeneratedValue
    private Long id;

    private int boardSize;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String winner;


    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public void setStartedAt(LocalDateTime now) {
        this.startedAt = now;
    }

    public void setFinishedAt(LocalDateTime now) {
        this.finishedAt = now;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public String getWinner() {
        return winner;
    }

}
