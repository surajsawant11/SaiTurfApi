package com.saiTurf.API.controller;

import com.saiTurf.API.model.BookingModel;
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

    // Get All Bookings
    @GetMapping
    public ResponseEntity<List<BookingModel>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Get Booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<BookingModel> getBookingById(@PathVariable Long id) {
        Optional<BookingModel> booking = bookingService.getBookingById(id);
        return booking.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping("/save")
    public ResponseEntity<BookingModel> saveOrUpdateBooking(@RequestBody BookingModel booking) {
        if (booking.getId() == null) {
            // If ID is not provided, it's a new booking (Create)
            BookingModel savedBooking = bookingService.saveBooking(booking);
            return ResponseEntity.ok(savedBooking);
        } else {
            // If ID is provided, update existing booking
            BookingModel updatedBooking = bookingService.updateBooking(booking.getId(), booking);
            return ResponseEntity.ok(updatedBooking);
        }
    }
   
    // Delete Booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
