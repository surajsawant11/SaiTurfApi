package com.saiTurf.API.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.saiTurf.API.model.TurfDetailModel;
import com.saiTurf.API.service.TurfService;

//import jakarta.persistence.criteria.Path;

@RestController
@RequestMapping("/api/turfs")
public class TurfController {

	@Value("${image.api.url}") // 🔹 Defined in `application.properties`
	private String imageUrl;

	private final TurfService turfService;

	public TurfController(TurfService turfService) {
		this.turfService = turfService;
	}

	@GetMapping()
	public ResponseEntity<List<TurfDetailModel>> getAllTurfs() {
		List<TurfDetailModel> turfDetailModelList = turfService.getAllTurfs().stream().filter(Objects::nonNull) // ✅
																												// Filters
																												// out
																												// null
																												// values
				.peek(a -> a.setImageUrl(imageUrl + "/500/" + a.getImageUrl())) // ✅ Correct way to modify objects
																				// inside stream
				.collect(Collectors.toList());

		return ResponseEntity.ok(turfDetailModelList); // ✅ Return modified list

	}

	// ✅ Register a new Turf (Only for ADMIN)
	@PostMapping("/save")
//    @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TurfDetailModel> registerTurf(@RequestBody TurfDetailModel turf) {
		TurfDetailModel savedTurf = turfService.registerTurf(turf);
		return ResponseEntity.ok(savedTurf);
	}

	@PostMapping("/save1")
	public ResponseEntity<?> registerTurf(@RequestParam("name") String name, @RequestParam("location") String location,
			@RequestParam("pricePerHour") BigDecimal pricePerHour, @RequestParam("capacity") int capacity,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {

		try {
			// 🔹 Step 1: Save Turf without image URL (to get the ID)
			TurfDetailModel turf = new TurfDetailModel(name, location, pricePerHour, capacity, null);
			TurfDetailModel savedTurf = turfService.registerTurf(turf); // ID is generated

			// 🔹 Step 2: Save image with ID as filename
			String imageUrl = null;
			if (imageFile != null && !imageFile.isEmpty()) {
				imageUrl = turfService.saveImage(imageFile, savedTurf.getId()); // Save image with ID
				savedTurf.setImageUrl(imageUrl);
				turfService.registerTurf(savedTurf); // Update Turf with image path
			}

			return ResponseEntity.ok(savedTurf);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Failed to save image", "message", e.getMessage()));
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "Invalid data", "message", "Check if all required fields are valid"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Unexpected error", "message", e.getMessage()));
		}
	}

}
