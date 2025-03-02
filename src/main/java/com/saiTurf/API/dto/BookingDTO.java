package com.saiTurf.API.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingDTO {
    private Long id;
    private UserDTO user;
    private TurfDTO turf;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal totalPrice;
    private Long turfId;
    private Long userId;
    private BookingStatus status;  // ✅ Change from String to Enum

    // ✅ Add Enum for Booking Status
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED;
    }

    // Constructors
    public BookingDTO() {}

    public BookingDTO(Long id, UserDTO user, TurfDTO turf, LocalDate bookingDate, 
                      LocalTime startTime, LocalTime endTime, BigDecimal totalPrice, BookingStatus status) {
        this.id = id;
        this.user = user;
        this.turf = turf;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public TurfDTO getTurf() {
        return turf;
    }

    public void setTurf(TurfDTO turf) {
        this.turf = turf;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

	public Long getTurfId() {
		return turfId;
	}

	public void setTurfId(Long turfId) {
		this.turfId = turfId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
    
}
