package com.saiTurf.API.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.saiTurf.API.model.TurfDetailModel;
import com.saiTurf.API.repository.TurfRepository;

@Service
@Transactional
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
        return turfRepository.findAll(); // ✅ Ensure this fetches bookings too
    }
    
    public String saveImage(MultipartFile imageFile, Long turfId) throws IOException {
        String extension = StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
        String fileName = "turf-"+turfId + (extension != null ? "." + extension : ""); // ID as filename
        Path uploadPath = Paths.get(uploadDirectory+"\\turf");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // 🔹 Create directory if not exists
        }

        Path filePath = uploadPath.resolve(fileName);
        imageFile.transferTo(filePath.toFile());

        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex); // ✅ Remove extension
        }
        return fileName;
    }
    
    public TurfDetailModel findById(Long id) {
        return turfRepository.findById(id).orElse(null);
    }

    public boolean softDeleteTurf(Long turfId) {
        Optional<TurfDetailModel> optionalTurf = turfRepository.findById(turfId);
        if (optionalTurf.isPresent()) {
            TurfDetailModel turf = optionalTurf.get();
            turf.setDeletedAt(LocalDateTime.now()); // Set deletedAt to current timestamp
            turfRepository.save(turf); // ✅ Save updated record
            return true;
        }
        return false; // Turf not found
    }
}
