package pl.edu.go.server.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.go.server.persistence.entity.GameEntity;
import pl.edu.go.server.persistence.entity.MoveEntity;

import java.util.List;

public interface MoveRepository extends JpaRepository<MoveEntity, Long> {
    List<MoveEntity> findByGameOrderByMoveNumber(GameEntity game);
}
