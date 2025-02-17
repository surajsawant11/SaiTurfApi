package com.saiTurf.API.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
public class ResponseController {

    private static final String IMAGE_DIRECTORY = "C:/Users/Suraj/Desktop/turfImages/"; // ✅ Use forward slashes

    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        System.out.println("Image request received: " + imageName);
        try {
            Path imagePath = Paths.get(IMAGE_DIRECTORY).resolve(imageName).normalize();
            System.out.println("Resolved path: " + imagePath.toString()); // ✅ Debugging output

            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("File not found or unreadable: " + imagePath);
                return ResponseEntity.notFound().build();
            }

            // Dynamically detect content type
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Default if type not found
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            System.err.println("Error serving file: " + e.getMessage()); // ✅ Debugging output
            return ResponseEntity.internalServerError().build();
        }
    }
}
