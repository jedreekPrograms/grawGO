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

/**
 * Serwis odpowiedzialny za odtwarzanie (replay) zapisanej gry Go.
 * <p>
 * Na podstawie danych zapisanych w bazie (gra + ruchy)
 * buduje sekwencję komunikatów tekstowych reprezentujących
 * kolejne stany planszy.
 * </p>
 */
@Service
public class GameReplayService {

    /**
     * Repozytorium gier.
     */
    private final GameRepository gameRepo;

    /**
     * Repozytorium ruchów.
     */
    private final MoveRepository moveRepo;

    /**
     * Tworzy serwis replay gry.
     *
     * @param g repozytorium gier
     * @param m repozytorium ruchów
     */
    public GameReplayService(GameRepository g, MoveRepository m) {
        this.gameRepo = g;
        this.moveRepo = m;
    }

    /**
     * Buduje zapis replay dla wskazanej gry.
     * <p>
     * Replay składa się z:
     * <ul>
     *   <li>nagłówka {@code REPLAY_START}</li>
     *   <li>kolejnych stanów planszy po każdym ruchu</li>
     *   <li>opcjonalnej informacji o zwycięzcy</li>
     *   <li>znacznika {@code REPLAY_END}</li>
     * </ul>
     * </p>
     * <p>
     * Ruchy typu {@link MoveType#PASS}, {@link MoveType#RESIGN}
     * oraz {@link MoveType#ACCEPT} nie zmieniają planszy,
     * ale nadal powodują zapis aktualnego stanu planszy.
     * </p>
     *
     * @param gameId identyfikator gry
     * @return lista komunikatów replay lub {@code null},
     * jeśli gra o podanym ID nie istnieje
     */
    public List<String> buildReplay(long gameId) {
        GameEntity game = gameRepo.findById(gameId).orElse(null);
        if (game == null) return null;

        List<MoveEntity> moves = moveRepo.findByGameOrderByMoveNumber(game);

        List<String> replay = new ArrayList<>();

        replay.add("REPLAY_START " + gameId);

        // Początkowy stan – pusta plansza
        GameState state = new GameState(game.getBoardSize());
        replay.add(boardMessage(state));

        for (MoveEntity m : moves) {

            if (m.getType() == MoveType.MOVE) {
                // Ustaw gracza, który wykonuje ruch
                state.setNextToMove(m.getColor());

                // Zastosuj ruch (zakładamy, że zawsze jest poprawny)
                state.applyMove(
                        new MoveFactory().createPlace(
                                new Point(m.getX(), m.getY()),
                                m.getColor()
                        )
                );
            }

            // PASS / ACCEPT / RESIGN nie zmieniają planszy,
            // ale zapisujemy jej aktualny stan
            replay.add(boardMessage(state));
        }

        // Informacja o zwycięzcy (jeśli dostępna)
        if (game.getWinner() != null) {
            replay.add("REPLAY_WINNER " + game.getWinner());
        }

        replay.add("REPLAY_END");
        return replay;
    }

    /**
     * Tworzy komunikat tekstowy opisujący aktualny stan planszy.
     *
     * @param state aktualny stan gry
     * @return komunikat w formacie:
     * {@code BOARD <rozmiar> <plansza_jednoliniowa>}
     */
    private String boardMessage(GameState state) {
        return "BOARD " +
                state.getBoard().getSize() + " " +
                state.getBoard().toSingleLineString();
    }
}
