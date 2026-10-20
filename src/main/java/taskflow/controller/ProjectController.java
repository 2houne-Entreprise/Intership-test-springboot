package taskflow.controller;

import taskflow.dto.ProjectForm;
import taskflow.entity.Project;
import taskflow.entity.TaskStatus;
import taskflow.services.ProjectService;
import taskflow.services.TaskService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
public class ProjectController {

  private final ProjectService projectService;
  private final TaskService taskService;

  public ProjectController(ProjectService projectService, TaskService taskService) {
    this.projectService = projectService;
    this.taskService = taskService;
  }

  @GetMapping
  public String index(Principal principal, Model model) {
    model.addAttribute("projects", projectService.findAllFor(principal.getName()));
    return "projects/index";
  }

  @GetMapping("/new")
  public String create(Model model) {
    model.addAttribute("projectForm", new ProjectForm());
    model.addAttribute("formAction", "/projects");
    return "projects/form";
  }

  @PostMapping
  public String store(
      @Valid @ModelAttribute("projectForm") ProjectForm form,
      BindingResult result,
      Principal principal,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      model.addAttribute("formAction", "/projects");
      return "projects/form";
    }
    projectService.create(form, principal.getName());
    redirectAttributes.addFlashAttribute("success", "Project created.");
    return "redirect:/projects";
  }

  @GetMapping("/{projectId}")
  public String show(@PathVariable("projectId") Long id, Principal principal, Model model) {
    model.addAttribute("project", projectService.findOwned(id, principal.getName()));
    model.addAttribute("tasks", taskService.findAll(id, principal.getName()));
    model.addAttribute("statuses", TaskStatus.values());
    return "projects/show";
  }

  @GetMapping("/{projectId}/edit")
  public String edit(@PathVariable("projectId") Long id, Principal principal, Model model) {
    Project project = projectService.findOwned(id, principal.getName());
    ProjectForm form = new ProjectForm();
    form.setName(project.getName());
    form.setDescription(project.getDescription());
    model.addAttribute("projectForm", form);
    model.addAttribute("formAction", "/projects/" + id);
    return "projects/form";
  }

  @PostMapping("/{projectId}")
  public String update(
      @PathVariable("projectId") Long id,
      @Valid @ModelAttribute("projectForm") ProjectForm form,
      BindingResult result,
      Principal principal,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      model.addAttribute("formAction", "/projects/" + id);
      return "projects/form";
    }
    projectService.update(id, form, principal.getName());
    redirectAttributes.addFlashAttribute("success", "Project updated.");
    return "redirect:/projects";
  }

  @PostMapping("/{projectId}/delete")
  public String destroy(
      @PathVariable("projectId") Long id, Principal principal, RedirectAttributes redirectAttributes) {
    projectService.delete(id, principal.getName());
    redirectAttributes.addFlashAttribute("success", "Project deleted.");
    return "redirect:/projects";
  }
}
