package pl.edu.go.server.replay;

import org.springframework.stereotype.Service;
import pl.edu.go.model.Color;
import pl.edu.go.model.GameState;
import pl.edu.go.model.Move;
import pl.edu.go.model.MoveFactory;
import pl.edu.go.model.Point;
import pl.edu.go.server.persistence.entity.GameEntity;
import pl.edu.go.server.persistence.entity.MoveEntity;
import pl.edu.go.server.persistence.entity.MoveType;
import pl.edu.go.server.persistence.repository.GameRepository;
import pl.edu.go.server.persistence.repository.MoveRepository;

import java.util.ArrayList;
import java.util.List;

import static pl.edu.go.server.persistence.entity.MoveType.*;

@Service
public class GameReplayService {

    private final GameRepository gameRepo;
    private final MoveRepository moveRepo;

    public GameReplayService(GameRepository g, MoveRepository m) {
        this.gameRepo = g;
        this.moveRepo = m;
    }

    public List<String> buildReplay(long gameId) {
        GameEntity game = gameRepo.findById(gameId).orElse(null);
        if (game == null) return null;

        List<MoveEntity> moves = moveRepo.findByGameOrderByMoveNumber(game);

        List<String> replay = new ArrayList<>();

        replay.add("REPLAY_START " + gameId);

// pusta plansza
        GameState state = new GameState(game.getBoardSize());
        replay.add(boardMessage(state));

        for (MoveEntity m : moves) {

            if (m.getType() == MoveType.MOVE) {
                // USTAW KOLEJ
                state.setNextToMove(m.getColor());

// TERAZ RUCH ZAWSZE WEJDZIE
                state.applyMove(
                        new MoveFactory().createPlace(
                                new Point(m.getX(), m.getY()),
                                m.getColor()
                        )
                );

            }

            // PASS / ACCEPT / RESIGN pomijamy w replay
            replay.add(boardMessage(state));
        }

        // --- informacja o zwycięzcy ---
        if (game.getWinner() != null) {
            replay.add("REPLAY_WINNER " + game.getWinner());
        }

        replay.add("REPLAY_END");
        return replay;

    }

    private String boardMessage(GameState state) {
        return "BOARD " +
                state.getBoard().getSize() + " " +
                state.getBoard().toSingleLineString();
    }
}
