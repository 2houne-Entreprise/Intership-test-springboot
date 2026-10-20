package taskflow.controller;

import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

  @ModelAttribute("currentUser")
  public String currentUser(Principal principal) {
    return principal == null ? null : principal.getName();
  }
}
