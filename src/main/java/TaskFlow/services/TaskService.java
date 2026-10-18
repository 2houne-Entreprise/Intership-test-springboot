package TaskFlow.services;

import TaskFlow.dto.TaskForm;
import TaskFlow.entity.Project;
import TaskFlow.entity.Task;
import TaskFlow.repository.TaskRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final ProjectService projectService;

  public TaskService(TaskRepository taskRepository, ProjectService projectService) {
    this.taskRepository = taskRepository;
    this.projectService = projectService;
  }

  @Transactional(readOnly = true)
  public List<Task> findAll(Long projectId, String username) {
    projectService.findOwned(projectId, username);
    return taskRepository.findByProjectId(projectId);
  }

  @Transactional(readOnly = true)
  public Task findOwned(Long projectId, Long id, String username) {
    projectService.findOwned(projectId, username);
    return taskRepository
        .findByIdAndProjectId(id, projectId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Transactional
  public Task create(Long projectId, TaskForm form, String username) {
    Project project = projectService.findOwned(projectId, username);
    Task task = new Task();
    task.setProject(project);
    apply(task, form);
    return taskRepository.save(task);
  }

  @Transactional
  public Task update(Long projectId, Long id, TaskForm form, String username) {
    Task task = findOwned(projectId, id, username);
    apply(task, form);
    return task;
  }

  private void apply(Task task, TaskForm form) {
    task.setTitle(form.getTitle().trim());
    task.setStatus(form.getStatus());
    task.setDeadline(form.getDeadline());
  }
}
