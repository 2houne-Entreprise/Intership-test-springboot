package taskflow.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

  private static final Set<String> ALLOWED_EXTENSIONS =
      Set.of("pdf", "png", "jpg", "jpeg", "gif", "txt", "csv", "doc", "docx", "xls", "xlsx", "zip");

  private final Path root;

  public StorageService(@Value("${app.storage.attachments-dir}") String directory)
      throws IOException {
    this.root = Path.of(directory).toAbsolutePath().normalize();
    Files.createDirectories(root);
  }

  public Path location() {
    return root;
  }

  public String store(MultipartFile file) {
    String extension = extensionOf(file.getOriginalFilename());
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new IllegalArgumentException("File type not allowed");
    }
    String name = UUID.randomUUID() + "." + extension;
    try (InputStream in = file.getInputStream()) {
      Files.copy(in, root.resolve(name));
    } catch (IOException e) {
      throw new UncheckedIOException("Could not store the file", e);
    }
    return name;
  }

  public void delete(String name) {
    if (name == null) {
      return;
    }
    Path target = root.resolve(name).normalize();
    if (!target.startsWith(root)) {
      return;
    }
    try {
      Files.deleteIfExists(target);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not delete the file", e);
    }
  }

  private String extensionOf(String originalName) {
    String clean = StringUtils.cleanPath(originalName == null ? "" : originalName);
    String extension = StringUtils.getFilenameExtension(clean);
    return extension == null ? "" : extension.toLowerCase();
  }
}
