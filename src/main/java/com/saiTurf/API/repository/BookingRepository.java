package com.saiTurf.API.repository;

import com.saiTurf.API.model.BookingModel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<BookingModel, Long> {
	
	List<BookingModel> findByUserId(Long userId);

}
