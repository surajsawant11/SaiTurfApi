package com.saiTurf.API.controller;

import com.saiTurf.API.config.JwtUtil;
import com.saiTurf.API.dto.BookingDTO;
import com.saiTurf.API.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Check Availability
    @GetMapping("/booked-dates/{turfId}")
    public ResponseEntity<List<LocalDate>> getBookedDates(@PathVariable Long turfId) {
        List<LocalDate> bookedDates = bookingService.getBookedDatesByTurfId(turfId);
        return ResponseEntity.ok(bookedDates);
    }


    // ✅ Get all bookings (Admin gets all, User gets only their bookings)
    @GetMapping
    public ResponseEntity<?> getAllBookings(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: Token is missing or invalid");
        }

        String jwt = token.replace("Bearer ", "");
        String role = jwtUtil.extractRole(jwt);
        Long userId = jwtUtil.extractUserId(jwt);

        if (role == null || userId == null) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token data");
        }

        List<BookingDTO> bookingDTOs;
        if ("ADMIN".equals(role)) {
            bookingDTOs = bookingService.getAllBookings();
        } else {
            bookingDTOs = bookingService.getBookingsByUserId(userId);
        }

        return ResponseEntity.ok(bookingDTOs);
    }

    // ✅ Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: Token is missing or invalid");
        }

        String jwt = token.replace("Bearer ", "");
        String role = jwtUtil.extractRole(jwt);
        Long userId = jwtUtil.extractUserId(jwt);

        Optional<BookingDTO> bookingOptional = bookingService.getBookingById(id);

        if (bookingOptional.isPresent()) {
            BookingDTO booking = bookingOptional.get();
            
            // If user is not ADMIN and trying to access someone else's booking, deny access
            if (!"ADMIN".equals(role) && !booking.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).body("Forbidden: You are not allowed to access this booking");
            }

            return ResponseEntity.ok(booking);
        }

        return ResponseEntity.status(404).body("Booking not found");
    }

    // ✅ Create a new booking
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO, 
                                           @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: Token is missing or invalid");
        }

        String jwt = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(jwt);

        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token data");
        }

        // Set the user ID from the token to prevent booking for another user
        bookingDTO.setUserId(userId);

        BookingDTO savedBooking = bookingService.saveBooking(bookingDTO);
        return ResponseEntity.ok(savedBooking);
    }

    // ✅ Update an existing booking
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, 
                                           @RequestBody BookingDTO bookingDTO,
                                           @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: Token is missing or invalid");
        }

        String jwt = token.replace("Bearer ", "");
        String role = jwtUtil.extractRole(jwt);
        Long userId = jwtUtil.extractUserId(jwt);

        Optional<BookingDTO> existingBookingOptional = bookingService.getBookingById(id);

        if (existingBookingOptional.isPresent()) {
            BookingDTO existingBooking = existingBookingOptional.get();
            
            // Only ADMIN or the booking owner can update
            if (!"ADMIN".equals(role) && !existingBooking.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).body("Forbidden: You are not allowed to update this booking");
            }

            BookingDTO updatedBooking = bookingService.updateBooking(id, bookingDTO);
            return ResponseEntity.ok(updatedBooking);
        }

        return ResponseEntity.status(404).body("Booking not found");
    }

    // ✅ Delete a booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id, 
                                           @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: Token is missing or invalid");
        }

        String jwt = token.replace("Bearer ", "");
        String role = jwtUtil.extractRole(jwt);
        Long userId = jwtUtil.extractUserId(jwt);

        Optional<BookingDTO> bookingOptional = bookingService.getBookingById(id);

        if (bookingOptional.isPresent()) {
            BookingDTO booking = bookingOptional.get();

            // Only ADMIN or the booking owner can delete
            if (!"ADMIN".equals(role) && !booking.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).body("Forbidden: You are not allowed to delete this booking");
            }

            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(404).body("Booking not found");
    }
}
