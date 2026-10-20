package taskflow.services;

import taskflow.dto.TaskForm;
import taskflow.entity.Project;
import taskflow.entity.Task;
import taskflow.entity.TaskStatus;
import taskflow.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final ProjectService projectService;
  private final StorageService storageService;

  public TaskService(
      TaskRepository taskRepository,
      ProjectService projectService,
      StorageService storageService) {
    this.taskRepository = taskRepository;
    this.projectService = projectService;
    this.storageService = storageService;
  }

  @Transactional(readOnly = true)
  public List<Task> findAll(Long projectId, String username) {
    projectService.findOwned(projectId, username);
    return taskRepository.findByProjectId(projectId);
  }

  @Transactional(readOnly = true)
  public List<Task> findOverdue(String username) {
    return taskRepository.findByProjectUserUsernameAndDeadlineBeforeAndStatusNot(
        username, LocalDate.now(), TaskStatus.DONE);
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
    String attachmentPath = storeIfPresent(form.getAttachment());
    Task task = new Task();
    task.setProject(project);
    apply(task, form);
    task.setAttachmentPath(attachmentPath);
    return taskRepository.save(task);
  }

  @Transactional
  public Task update(Long projectId, Long id, TaskForm form, String username) {
    Task task = findOwned(projectId, id, username);
    String attachmentPath = storeIfPresent(form.getAttachment());
    if (attachmentPath != null) {
      storageService.delete(task.getAttachmentPath());
      task.setAttachmentPath(attachmentPath);
    }
    apply(task, form);
    return task;
  }

  @Transactional
  public Task updateStatus(Long projectId, Long id, TaskStatus status, String username) {
    Task task = findOwned(projectId, id, username);
    task.setStatus(status);
    return task;
  }

  private String storeIfPresent(MultipartFile attachment) {
    if (attachment == null || attachment.isEmpty()) {
      return null;
    }
    return storageService.store(attachment);
  }

  private void apply(Task task, TaskForm form) {
    task.setTitle(form.getTitle().trim());
    task.setStatus(form.getStatus());
    task.setDeadline(form.getDeadline());
  }
}
