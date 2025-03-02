package com.saiTurf.API.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private final String DEFAULT_IMAGE = "na.jpg"; // Default fallback image

    @Async
    public CompletableFuture<byte[]> processImage(String folderPath, String imageName, int maxSize) {

        try {
            // 🔹 Find the image file (auto-detect extension)
            Path imagePath = findImagePath(imageName, folderPath);

            if (imagePath == null || !Files.exists(imagePath) || !Files.isReadable(imagePath)) {
                System.err.println("File not found: " + imageName + " → Returning " + DEFAULT_IMAGE);
                imagePath = Paths.get(folderPath, DEFAULT_IMAGE);
            }

            // 🔹 Read and resize the image
            BufferedImage originalImage = ImageIO.read(imagePath.toFile());
            BufferedImage resizedImage = resizeImageWithAspectRatio(originalImage, maxSize);

            // 🔹 Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos); // Convert to JPEG format
            byte[] imageBytes = baos.toByteArray();

            return CompletableFuture.completedFuture(imageBytes);

        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
            return CompletableFuture.completedFuture(new byte[0]); // Return empty byte array on failure
        }
    }

    /**
     * Finds the image file without requiring the user to specify an extension.
     */
    private Path findImagePath(String imageName, String folderPath) {
        Path basePath = Paths.get(folderPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(basePath, imageName + ".*")) {
            for (Path entry : stream) {
                return entry;
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
