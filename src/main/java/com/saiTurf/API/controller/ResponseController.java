package com.saiTurf.API.controller;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResponseController {

    private static final String IMAGE_DIRECTORY = "C:/Users/Suraj/Desktop/saiTurfImgFolder/"; // ✅ Use forward slashes

    @GetMapping("/images/{imagesize}/{imageName}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable int imagesize, // 🔹 Max width/height (e.g., 200, 300, 400)
            @PathVariable String imageName) {

        try {
            // 🔹 Find the image file (auto-detect extension)
            Path imagePath = findImagePath(imageName);

            if (imagePath == null || !Files.exists(imagePath) || !Files.isReadable(imagePath)) {
                System.err.println("File not found: " + imageName + " → Returning na.jpg");
                imagePath = Paths.get(IMAGE_DIRECTORY, "na.jpg"); // Fallback to default image
            }

            // 🔹 Read and resize the image (keep aspect ratio)
            BufferedImage originalImage = ImageIO.read(imagePath.toFile());
            BufferedImage resizedImage = resizeImageWithAspectRatio(originalImage, imagesize);

            // 🔹 Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos); // Convert to JPEG format
            byte[] imageBytes = baos.toByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + ".jpg\"")
                    .body(imageBytes);

        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Finds the image file without requiring the user to specify an extension.
     */
    private Path findImagePath(String imageName) {
        Path basePath = Paths.get(IMAGE_DIRECTORY);
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(basePath, imageName + ".*")) {
            for (Path entry : stream) {
                return entry; // Return the first matched file
            }
        } catch (IOException e) {
            System.err.println("Error searching for image: " + e.getMessage());
        }
        
        return null; // No match found
    }

    /**
     * Resizes an image while keeping the original aspect ratio.
     */
    private BufferedImage resizeImageWithAspectRatio(BufferedImage originalImage, int maxSize) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 🔹 Calculate new dimensions while keeping aspect ratio
        double aspectRatio = (double) originalWidth / originalHeight;
        int newWidth, newHeight;

        if (originalWidth > originalHeight) {
            newWidth = maxSize;
            newHeight = (int) (maxSize / aspectRatio);
        } else {
            newHeight = maxSize;
            newWidth = (int) (maxSize * aspectRatio);
        }

        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }


    }
