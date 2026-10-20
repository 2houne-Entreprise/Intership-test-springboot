package TaskFlow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import TaskFlow.entity.Project;
import TaskFlow.entity.User;
import TaskFlow.repository.ProjectRepository;
import TaskFlow.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProjectCreationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private ProjectRepository projectRepository;

  @Test
  void authenticatedUserCreatesProjectInDatabase() throws Exception {
    User alice = new User();
    alice.setUsername("alice");
    alice.setPassword("not-used-in-this-test");
    alice = userRepository.save(alice);

    mockMvc
        .perform(
            post("/projects")
                .with(user("alice"))
                .with(csrf())
                .param("name", "Website redesign")
                .param("description", "New landing page"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/projects"));

    List<Project> projects = projectRepository.findByUserId(alice.getId());
    assertThat(projects).hasSize(1);
    assertThat(projects.get(0).getName()).isEqualTo("Website redesign");
    assertThat(projects.get(0).getDescription()).isEqualTo("New landing page");
  }
}
