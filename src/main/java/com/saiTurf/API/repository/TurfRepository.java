package com.saiTurf.API.repository;

import com.saiTurf.API.model.TurfDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurfRepository extends JpaRepository<TurfDetailModel, Long> {
    boolean existsByName(String name);
}
