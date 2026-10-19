package TaskFlow.config;

import TaskFlow.services.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class ProjectOwnershipInterceptor implements HandlerInterceptor {

  private final ProjectService projectService;

  public ProjectOwnershipInterceptor(ProjectService projectService) {
    this.projectService = projectService;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler)
      throws IOException {
    Map<String, String> pathVariables =
        (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (pathVariables == null || !pathVariables.containsKey("projectId")) {
      return true; // route without a project id (list, new, store): nothing to check
    }

    Principal principal = request.getUserPrincipal();
    if (principal == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }

    long projectId;
    try {
      projectId = Long.parseLong(pathVariables.get("projectId"));
    } catch (NumberFormatException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return false;
    }

    if (!projectService.isOwner(projectId, principal.getName())) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return false;
    }
    return true;
  }
}
