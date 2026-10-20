package TaskFlow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import TaskFlow.dto.TaskForm;
import TaskFlow.entity.Project;
import TaskFlow.entity.TaskStatus;
import TaskFlow.entity.User;
import TaskFlow.repository.ProjectRepository;
import TaskFlow.repository.TaskRepository;
import TaskFlow.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskValidationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private ProjectRepository projectRepository;
  @Autowired private TaskRepository taskRepository;

  @Test
  void emptyTitleViolatesValidationRules() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    TaskForm form = new TaskForm();
    form.setTitle("");
    form.setStatus(TaskStatus.PENDING);

    Set<ConstraintViolation<TaskForm>> violations = validator.validate(form);

    assertThat(violations)
        .anyMatch(violation -> violation.getPropertyPath().toString().equals("title"));
  }

  @Test
  void emptyTitleIsRejectedByTheControllerAndNoTaskIsSaved() throws Exception {
    User bob = new User();
    bob.setUsername("bob");
    bob.setPassword("not-used-in-this-test");
    bob = userRepository.save(bob);

    Project project = new Project();
    project.setUser(bob);
    project.setName("Bob's project");
    project = projectRepository.save(project);

    mockMvc
        .perform(
            post("/projects/" + project.getId() + "/tasks")
                .with(user("bob"))
                .with(csrf())
                .param("title", "")
                .param("status", "PENDING"))
        .andExpect(status().isOk())
        .andExpect(view().name("tasks/form"))
        .andExpect(model().attributeHasFieldErrors("taskForm", "title"));

    assertThat(taskRepository.findByProjectId(project.getId())).isEmpty();
  }
}
