package com.saiTurf.API.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.saiTurf.API.model.BookingModel;

@Repository
public interface BookingRepository extends JpaRepository<BookingModel, Long> {
	
	List<BookingModel> findByUserId(Long userId);
	
	@Query(value = """
		    SELECT EXISTS (
		        SELECT 1 FROM t_booking b 
		        WHERE b.turf_id = :turfId 
		        AND b.booking_date = :bookingDate 
		    )
		""", nativeQuery = true)
		boolean existsByTurfAndTime(
		    @Param("turfId") Long turfId,
		    @Param("bookingDate") LocalDate bookingDate
//		    @Param("startTime") LocalTime startTime,
//		    @Param("endTime") LocalTime endTime
		);


	List<BookingModel> findByTurfId(Long turfId);

	@Query("SELECT DISTINCT b.bookingDate FROM BookingModel b WHERE b.turf.id = :turfId")
    List<LocalDate> findBookedDatesByTurfId(@Param("turfId") Long turfId);
	
}
