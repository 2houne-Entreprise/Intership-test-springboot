package TaskFlow.repository;

import TaskFlow.entity.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  List<Project> findByUserId(Long userId);

  Optional<Project> findByIdAndUserId(Long id, Long userId);

  boolean existsByIdAndUserUsername(Long id, String username);
}
