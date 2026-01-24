package pl.edu.go.server.persistence.service;

import org.springframework.stereotype.Service;
import pl.edu.go.model.Color;
import pl.edu.go.server.persistence.entity.GameEntity;
import pl.edu.go.server.persistence.entity.MoveEntity;
import pl.edu.go.server.persistence.entity.MoveType;
import pl.edu.go.server.persistence.repository.GameRepository;
import pl.edu.go.server.persistence.repository.MoveRepository;

import java.time.LocalDateTime;

@Service
public class GamePersistenceService {
    private final GameRepository gameRepo;
    private final MoveRepository moveRepo;

    public GamePersistenceService(GameRepository g, MoveRepository m) {
        this.gameRepo = g;
        this.moveRepo = m;
    }

    public GameEntity startGame(int boardSize) {
        GameEntity g = new GameEntity();
        g.setBoardSize(boardSize);
        g.setStartedAt(LocalDateTime.now());
        return gameRepo.save(g);
    }

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

    public void finishGame(GameEntity g, String winner) {
        g.setFinishedAt(LocalDateTime.now());
        g.setWinner(winner);
        gameRepo.save(g);
    }
}
