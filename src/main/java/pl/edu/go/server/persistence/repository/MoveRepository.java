package pl.edu.go.server.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.go.server.persistence.entity.GameEntity;
import pl.edu.go.server.persistence.entity.MoveEntity;

import java.util.List;

/**
 * Repozytorium JPA odpowiedzialne za dostęp do danych encji {@link MoveEntity}.
 * <p>
 * Umożliwia wykonywanie standardowych operacji CRUD na ruchach
 * oraz pobieranie listy ruchów powiązanych z konkretną grą.
 * </p>
 */
public interface MoveRepository extends JpaRepository<MoveEntity, Long> {

    /**
     * Zwraca wszystkie ruchy przypisane do danej gry,
     * posortowane rosnąco według numeru ruchu.
     *
     * <pre>
     * Odpowiada zapytaniu SQL:
     * SELECT *
     * FROM move_entity
     * WHERE game_id = ?
     * ORDER BY move_number ASC
     * </pre>
     *
     * @param game encja gry
     * @return lista ruchów w kolejności ich wykonania
     */
    List<MoveEntity> findByGameOrderByMoveNumber(GameEntity game);
}
