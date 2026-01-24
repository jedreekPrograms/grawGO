package pl.edu.go.server.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.go.server.persistence.entity.GameEntity;

public interface GameRepository extends JpaRepository<GameEntity, Long> {
    // SELECT * FROM game_entity WHERE finished_at
    // IS NOT NULL ORDER BY finished_at DESC LIMIT 1
    GameEntity findTopByFinishedAtIsNotNullOrderByFinishedAtDesc();
}
