package com.saiTurf.API.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.saiTurf.API.dto.TurfDTO;
import com.saiTurf.API.model.TurfDetailModel;
import com.saiTurf.API.service.TurfService;

//import jakarta.persistence.criteria.Path;

@RestController
@RequestMapping("/api/turfs")
public class TurfController {

	@Value("${image.api.url}") // 🔹 Defined in `application.properties`
	private String preFixImageUrl;

	@Autowired
	private final TurfService turfService;
	
	@Autowired
	private ModelMapper modelMapper;


	public TurfController(TurfService turfService) {
		this.turfService = turfService;
	}

	@GetMapping
	public ResponseEntity<List<TurfDTO>> getAllTurfs() {
	    List<TurfDetailModel> turfs = turfService.getAllTurfs();
	    List<TurfDTO> turfDTOs = turfs.stream()
                .map(turf -> modelMapper.map(turf, TurfDTO.class))
                .toList();

	    return ResponseEntity.ok(turfDTOs);
	}


	@PostMapping(value = "/save", consumes = "multipart/form-data")
	public ResponseEntity<?> registerTurf(@RequestParam(value = "id", required = false) Long id, // Add ID for update
			@RequestParam("name") String name, @RequestParam("location") String location,
			@RequestParam("pricePerHour") BigDecimal pricePerHour, @RequestParam("capacity") int capacity,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {

		try {
			TurfDetailModel turf;

			if (id != null) { // ✅ Update existing Turf
				turf = turfService.findById(id); // Fetch existing Turf
				if (turf == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Turf not found"));
				}
				turf.setName(name);
				turf.setLocation(location);
				turf.setPricePerHour(pricePerHour);
				turf.setCapacity(capacity);
				turf.setUpdatedAt(LocalDateTime.now()); // Update timestamp
			} else { // ✅ Create new Turf
				turf = new TurfDetailModel(name, location, pricePerHour, capacity, null);
			}

			TurfDetailModel savedTurf = turfService.registerTurf(turf); // Save/Update Turf

			// ✅ Save Image (Only If Provided)
			if (imageFile != null && !imageFile.isEmpty()) {
				String imageUrl = turfService.saveImage(imageFile, savedTurf.getId());
				savedTurf.setImageUrl( "/images/turf/500/" + imageUrl);
				savedTurf = turfService.registerTurf(savedTurf); // Update Turf with image URL
			}

			return ResponseEntity.ok(savedTurf); 

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Failed to save image", "message", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Unexpected error", "message", e.getMessage()));
		}
	}
	
	// 🔹 Soft Delete Turf
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTurf(@PathVariable Long id) {
        boolean deleted = turfService.softDeleteTurf(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Turf deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Turf not found"));
        }
    }

}
