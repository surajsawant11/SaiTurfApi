package com.saiTurf.API.controller;

import com.saiTurf.API.config.JwtUtil;
import com.saiTurf.API.dto.BookingDTO;
import com.saiTurf.API.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings(@RequestHeader("Authorization") String token) {
        // Extract role and userId from the token
        String role = jwtUtil.extractRole(token.replace("Bearer ", ""));
        Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));

        List<BookingDTO> bookingDTOs;

        if ("ADMIN".equals(role)) {
            // Admin gets all bookings
            bookingDTOs = bookingService.getAllBookings();
        } else {
            // Regular user gets only their bookings
            bookingDTOs = bookingService.getBookingsByUserId(userId);
        }

        return ResponseEntity.ok(bookingDTOs);
    }


    // ✅ Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        Optional<BookingDTO> booking = bookingService.getBookingById(id);
        return booking.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Save a new booking
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        BookingDTO savedBooking = bookingService.saveBooking(bookingDTO);
        return ResponseEntity.ok(savedBooking);
    }

    // ✅ Update an existing booking
    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) {
        BookingDTO updatedBooking = bookingService.updateBooking(id, bookingDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    // ✅ Delete a booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
