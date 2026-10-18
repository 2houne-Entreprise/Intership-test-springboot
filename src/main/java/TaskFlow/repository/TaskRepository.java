package TaskFlow.repository;

import TaskFlow.entity.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByProjectId(Long projectId);

  Optional<Task> findByIdAndProjectId(Long id, Long projectId);
}
