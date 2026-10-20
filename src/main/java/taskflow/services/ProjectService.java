package taskflow.services;

import taskflow.dto.ProjectForm;
import taskflow.entity.Project;
import taskflow.entity.User;
import taskflow.repository.ProjectRepository;
import taskflow.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public boolean isOwner(Long projectId, String username) {
    return projectRepository.existsByIdAndUserUsername(projectId, username);
  }

  @Transactional(readOnly = true)
  public List<Project> findAllFor(String username) {
    return projectRepository.findByUserId(currentUser(username).getId());
  }

  @Transactional(readOnly = true)
  public Project findOwned(Long id, String username) {
    return projectRepository
        .findByIdAndUserId(id, currentUser(username).getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Transactional
  public Project create(ProjectForm form, String username) {
    Project project = new Project();
    project.setUser(currentUser(username));
    project.setName(form.getName().trim());
    project.setDescription(form.getDescription());
    return projectRepository.save(project);
  }

  @Transactional
  public Project update(Long id, ProjectForm form, String username) {
    Project project = findOwned(id, username);
    project.setName(form.getName().trim());
    project.setDescription(form.getDescription());
    return project;
  }

  @Transactional
  public void delete(Long id, String username) {
    projectRepository.delete(findOwned(id, username));
  }

  private User currentUser(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }
}
