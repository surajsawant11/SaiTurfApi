package com.saiTurf.API.service;

import com.saiTurf.API.model.TurfDetailModel;
import com.saiTurf.API.repository.TurfRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TurfService {
	@Value("${image.upload.directory}")  // 🔹 Defined in `application.properties`
    private String uploadDirectory;

    private final TurfRepository turfRepository;

    public TurfService(TurfRepository turfRepository) {
        this.turfRepository = turfRepository;
    }

    public TurfDetailModel registerTurf(TurfDetailModel turf) {
        if (turf.getId() ==null && turfRepository.existsByName(turf.getName())) {
            throw new RuntimeException("Turf with this name already exists.");
        }
        return turfRepository.save(turf);
    }

    public List<TurfDetailModel> getAllTurfs() {
        return turfRepository.findAll();
    }
    
    public String saveImage(MultipartFile imageFile, Long turfId) throws IOException {
        String extension = StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
        String fileName = turfId + (extension != null ? "." + extension : ""); // ID as filename
        Path uploadPath = Paths.get(uploadDirectory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // 🔹 Create directory if not exists
        }

        Path filePath = uploadPath.resolve("turf-"+fileName);
        imageFile.transferTo(filePath.toFile());

        return fileName; // 🔹 Return the accessible image path
    }
}
