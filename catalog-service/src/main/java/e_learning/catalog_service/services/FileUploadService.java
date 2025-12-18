package e_learning.catalog_service.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${upload.path:./uploads/}")
    private String uploadPath;

    @Value("${upload.base-url:http://localhost:8085/uploads/}")
    private String baseUrl;

    public String uploadThumbnail(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Create thumbnails directory if it doesn't exist
        Path thumbnailDir = Paths.get(uploadPath, "thumbnails");
        if (!Files.exists(thumbnailDir)) {
            Files.createDirectories(thumbnailDir);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        // Save file
        Path targetPath = thumbnailDir.resolve(newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Return accessible URL
        return baseUrl + "thumbnails/" + newFilename;
    }

    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || !fileUrl.startsWith(baseUrl)) {
            return;
        }

        String relativePath = fileUrl.substring(baseUrl.length());
        Path filePath = Paths.get(uploadPath, relativePath);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
