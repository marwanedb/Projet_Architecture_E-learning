package e_learning.catalog_service.controllers;

import e_learning.catalog_service.services.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
@Tag(name = "File Upload", description = "File upload APIs")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/thumbnail")
    @Operation(summary = "Upload a course thumbnail image")
    public ResponseEntity<Map<String, String>> uploadThumbnail(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadThumbnail(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            response.put("message", "Thumbnail uploaded successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
