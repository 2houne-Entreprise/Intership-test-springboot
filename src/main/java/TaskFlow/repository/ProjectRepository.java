package TaskFlow.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import TaskFlow.entity.Project;
import java.util.Optional;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  List<Project> findByUserId(Long userId);

  Optional<Project> findByIdAndUserId(Long id, Long userId);
}