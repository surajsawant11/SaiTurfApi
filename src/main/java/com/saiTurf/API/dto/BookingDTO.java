package com.saiTurf.API.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingDTO {
    private Long id;
    private Long userId;  // Only store user ID
    private Long turfId;  // Only store turf ID
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal totalPrice;
    private String status; // String to prevent enum serialization issues

    // ✅ No-arg constructor for ModelMapper
    public BookingDTO() {}

    // ✅ Constructor (if needed)
    public BookingDTO(Long id, Long userId, Long turfId, LocalDate bookingDate, 
                      LocalTime startTime, LocalTime endTime, BigDecimal totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.turfId = turfId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // ✅ Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTurfId() {
        return turfId;
    }

    public void setTurfId(Long turfId) {
        this.turfId = turfId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
