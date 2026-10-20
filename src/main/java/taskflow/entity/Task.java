package taskflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import java.time.LocalDate;

@Entity
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Column(nullable = false, length = 255)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TaskStatus status;

  private LocalDate deadline;

  @Column(length = 255)
  private String attachmentPath;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

  public Long getId() { return id; }
  public Project getProject() { return project; }
  public void setProject(Project project) { this.project = project; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public TaskStatus getStatus() { return status; }
  public void setStatus(TaskStatus status) { this.status = status; }
  public LocalDate getDeadline() { return deadline; }
  public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }

  public String getStatusLabel() {
    if (status == null) {
      return "";
    }
    return switch (status) {
      case PENDING -> "En attente";
      case IN_PROGRESS -> "En cours";
      case DONE -> "Terminée";
    };
  }

  public String getAttachmentPath() {
    return attachmentPath;
  }

  public void setAttachmentPath(String attachmentPath) {
    this.attachmentPath = attachmentPath;
  }
}
