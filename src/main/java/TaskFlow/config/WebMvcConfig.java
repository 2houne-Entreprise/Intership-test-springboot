package TaskFlow.config;

import TaskFlow.services.StorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final ProjectOwnershipInterceptor projectOwnershipInterceptor;
  private final StorageService storageService;

  public WebMvcConfig(
      ProjectOwnershipInterceptor projectOwnershipInterceptor, StorageService storageService) {
    this.projectOwnershipInterceptor = projectOwnershipInterceptor;
    this.storageService = storageService;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(projectOwnershipInterceptor)
        .addPathPatterns("/projects/*", "/projects/*/**");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/attachments/**")
        .addResourceLocations(storageService.location().toUri().toString());
  }
}
