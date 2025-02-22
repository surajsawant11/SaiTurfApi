package com.saiTurf.API.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saiTurf.API.model.TurfDetailModel;
import com.saiTurf.API.service.TurfService;

@RestController
@RequestMapping("/api/turfs")
public class TurfController {

    private final TurfService turfService;

    public TurfController(TurfService turfService) {
        this.turfService = turfService;
    }
    @GetMapping()
    public ResponseEntity<List<TurfDetailModel>> getAllTurfs() {
        return ResponseEntity.ok(turfService.getAllTurfs());
    }

    // ✅ Register a new Turf (Only for ADMIN)
    @PostMapping("/save")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TurfDetailModel> registerTurf(@RequestBody TurfDetailModel turf) {
        TurfDetailModel savedTurf = turfService.registerTurf(turf);
        return ResponseEntity.ok(savedTurf);
    }

    // ✅ Get all Turfs (Both Admin & Users can see)
    
    
    

}
