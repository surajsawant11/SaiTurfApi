package com.saiTurf.API.controller;

import com.saiTurf.API.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ResponseController {

    private final ImageService imageService;

    @Value("${image.upload.directory}")
    private String uploadDirectory;

    public ResponseController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/images/{folderName}/{imagesize}/{imageName}")
    public CompletableFuture<ResponseEntity<byte[]>> getImage(
            @PathVariable String folderName,
            @PathVariable int imagesize,
            @PathVariable String imageName) {

        String folderPath = uploadDirectory + "\\turf";  // Path where images are stored

        return imageService.processImage(folderPath, imageName, imagesize)
                .thenApply(imageBytes -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + ".jpg\"")
                        .body(imageBytes));
    }
}
