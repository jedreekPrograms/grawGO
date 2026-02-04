package pl.edu.go.server.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.go.server.persistence.entity.GameEntity;

/**
 * Repozytorium JPA odpowiedzialne za dostęp do danych encji {@link GameEntity}.
 * <p>
 * Udostępnia standardowe operacje CRUD oraz metody zapytań
 * generowane automatycznie przez Spring Data JPA.
 * </p>
 */
public interface GameRepository extends JpaRepository<GameEntity, Long> {

    /**
     * Zwraca ostatnio zakończoną grę.
     * <p>
     * Wyszukuje grę, która posiada ustawioną datę zakończenia
     * i wybiera tę z najpóźniejszą wartością pola {@code finishedAt}.
     * </p>
     *
     * <pre>
     * Odpowiada zapytaniu SQL:
     * SELECT *
     * FROM game_entity
     * WHERE finished_at IS NOT NULL
     * ORDER BY finished_at DESC
     * LIMIT 1
     * </pre>
     *
     * @return ostatnio zakończona gra lub {@code null}, jeśli brak zakończonych gier
     */
    GameEntity findTopByFinishedAtIsNotNullOrderByFinishedAtDesc();
}
