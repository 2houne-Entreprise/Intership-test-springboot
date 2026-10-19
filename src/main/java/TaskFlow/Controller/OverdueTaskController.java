package TaskFlow.Controller;

import TaskFlow.services.TaskService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OverdueTaskController {

  private final TaskService taskService;

  public OverdueTaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping("/tasks/overdue")
  public String overdue(Principal principal, Model model) {
    model.addAttribute("tasks", taskService.findOverdue(principal.getName()));
    return "tasks/overdue";
  }
}
