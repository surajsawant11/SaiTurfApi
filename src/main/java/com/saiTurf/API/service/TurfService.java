package com.saiTurf.API.service;

import com.saiTurf.API.model.TurfDetailModel;
import com.saiTurf.API.repository.TurfRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TurfService {

    private final TurfRepository turfRepository;

    public TurfService(TurfRepository turfRepository) {
        this.turfRepository = turfRepository;
    }

    public TurfDetailModel registerTurf(TurfDetailModel turf) {
        if (turfRepository.existsByName(turf.getName())) {
            throw new RuntimeException("Turf with this name already exists.");
        }
        return turfRepository.save(turf);
    }

    public List<TurfDetailModel> getAllTurfs() {
        return turfRepository.findAll();
    }
}
