package TaskFlow.Controller;

import TaskFlow.dto.TaskForm;
import TaskFlow.entity.Task;
import TaskFlow.entity.TaskStatus;
import TaskFlow.services.ProjectService;
import TaskFlow.services.TaskService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

  private final TaskService taskService;
  private final ProjectService projectService;

  public TaskController(TaskService taskService, ProjectService projectService) {
    this.taskService = taskService;
    this.projectService = projectService;
  }

  @GetMapping("/new")
  public String create(@PathVariable Long projectId, Principal principal, Model model) {
    projectService.findOwned(projectId, principal.getName());
    TaskForm form = new TaskForm();
    form.setStatus(TaskStatus.PENDING);
    prepareForm(model, form, projectId, "/projects/" + projectId + "/tasks");
    return "tasks/form";
  }

  @PostMapping
  public String store(
      @PathVariable Long projectId,
      @Valid @ModelAttribute("taskForm") TaskForm form,
      BindingResult result,
      Principal principal,
      Model model,
      RedirectAttributes redirectAttributes) {
    String action = "/projects/" + projectId + "/tasks";
    if (!result.hasErrors()) {
      try {
        taskService.create(projectId, form, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Task created.");
        return "redirect:/projects/" + projectId;
      } catch (IllegalArgumentException e) {
        result.rejectValue("attachment", "invalid", e.getMessage());
      }
    }
    prepareFormAttributes(model, projectId, action);
    return "tasks/form";
  }

  @GetMapping("/{id}/edit")
  public String edit(
      @PathVariable Long projectId, @PathVariable Long id, Principal principal, Model model) {
    Task task = taskService.findOwned(projectId, id, principal.getName());
    TaskForm form = new TaskForm();
    form.setTitle(task.getTitle());
    form.setStatus(task.getStatus());
    form.setDeadline(task.getDeadline());
    prepareForm(model, form, projectId, "/projects/" + projectId + "/tasks/" + id);
    return "tasks/form";
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long projectId,
      @PathVariable Long id,
      @Valid @ModelAttribute("taskForm") TaskForm form,
      BindingResult result,
      Principal principal,
      Model model,
      RedirectAttributes redirectAttributes) {
    String action = "/projects/" + projectId + "/tasks/" + id;
    if (!result.hasErrors()) {
      try {
        taskService.update(projectId, id, form, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Task updated.");
        return "redirect:/projects/" + projectId;
      } catch (IllegalArgumentException e) {
        result.rejectValue("attachment", "invalid", e.getMessage());
      }
    }
    prepareFormAttributes(model, projectId, action);
    return "tasks/form";
  }

  @PostMapping("/{id}/status")
  public String updateStatus(
      @PathVariable Long projectId,
      @PathVariable Long id,
      @RequestParam TaskStatus status,
      Principal principal,
      RedirectAttributes redirectAttributes) {
    taskService.updateStatus(projectId, id, status, principal.getName());
    redirectAttributes.addFlashAttribute("success", "Status updated.");
    return "redirect:/projects/" + projectId;
  }

  private void prepareForm(Model model, TaskForm form, Long projectId, String action) {
    model.addAttribute("taskForm", form);
    prepareFormAttributes(model, projectId, action);
  }

  private void prepareFormAttributes(Model model, Long projectId, String action) {
    model.addAttribute("statuses", TaskStatus.values());
    model.addAttribute("projectId", projectId);
    model.addAttribute("formAction", action);
  }
}
