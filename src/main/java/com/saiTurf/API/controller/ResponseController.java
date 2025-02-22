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
@RequestMapping("/api")
public class ResponseController {

    private static final String IMAGE_DIRECTORY = "C:/Users/Suraj/Desktop/turfImages/"; // ✅ Use forward slashes

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        System.out.println("Image request received: " + imageName);
        
        try {
            // Ensure case-insensitive search for JPG images
            Path imagePath = findImagePath(imageName);

            if (imagePath == null || !Files.exists(imagePath) || !Files.isReadable(imagePath)) {
                System.err.println("File not found or unreadable: " + imageName + " → Returning na.jpg");
                imagePath = Paths.get(IMAGE_DIRECTORY).resolve("na.jpg"); // Fallback to default image
            }

            Resource resource = new UrlResource(imagePath.toUri());
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Default content type
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            System.err.println("Error serving file: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Finds the image path, allowing for case-insensitive ".jpg" and ".JPG" extensions.
     */
    private Path findImagePath(String imageName) {
        Path exactPath = Paths.get(IMAGE_DIRECTORY).resolve(imageName).normalize();

        if (Files.exists(exactPath) && Files.isReadable(exactPath)) {
            return exactPath;
        }

        // Try with lowercase ".jpg"
        Path lowerJpg = Paths.get(IMAGE_DIRECTORY).resolve(imageName + ".jpg").normalize();
        if (Files.exists(lowerJpg) && Files.isReadable(lowerJpg)) {
            return lowerJpg;
        }

        // Try with uppercase ".JPG"
        Path upperJpg = Paths.get(IMAGE_DIRECTORY).resolve(imageName + ".JPG").normalize();
        if (Files.exists(upperJpg) && Files.isReadable(upperJpg)) {
            return upperJpg;
        }

        return null; // If no match found
    }

    }
