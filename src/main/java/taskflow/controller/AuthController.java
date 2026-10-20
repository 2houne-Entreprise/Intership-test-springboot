package taskflow.controller;

import taskflow.dto.RegisterForm;
import taskflow.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/register")
  public String registerForm(Model model) {
    model.addAttribute("registerForm", new RegisterForm());
    return "register";
  }

  @PostMapping("/register")
  public String register(
      @Valid @ModelAttribute("registerForm") RegisterForm form,
      BindingResult result,
      RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return "register";
    }
    try {
      userService.registerUser(form.getUsername().trim(), form.getPassword());
    } catch (IllegalArgumentException e) {
      result.rejectValue("username", "duplicate", e.getMessage());
      return "register";
    }
    redirectAttributes.addFlashAttribute("success", "Account created. Please log in.");
    return "redirect:/login";
  }
}