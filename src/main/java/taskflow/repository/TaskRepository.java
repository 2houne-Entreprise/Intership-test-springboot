package taskflow.repository;

import taskflow.entity.Task;
import taskflow.entity.TaskStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByProjectId(Long projectId);

  Optional<Task> findByIdAndProjectId(Long id, Long projectId);

  @EntityGraph(attributePaths = "project")
  List<Task> findByProjectUserUsernameAndDeadlineBeforeAndStatusNot(
      String username, LocalDate date, TaskStatus status);
}
