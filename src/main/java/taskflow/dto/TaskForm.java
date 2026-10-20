package taskflow.dto;

import taskflow.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.multipart.MultipartFile;

public class TaskForm {

  @NotBlank(message = "Title is required")
  @Size(max = 255, message = "Title must be at most 255 characters")
  private String title;

  @NotNull(message = "Status is required")
  private TaskStatus status;

  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate deadline;

  private MultipartFile attachment;

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public TaskStatus getStatus() { return status; }
  public void setStatus(TaskStatus status) { this.status = status; }
  public LocalDate getDeadline() { return deadline; }
  public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
  public MultipartFile getAttachment() { return attachment; }
  public void setAttachment(MultipartFile attachment) { this.attachment = attachment; }
}
