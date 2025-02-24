package com.saiTurf.API.controller;

import com.saiTurf.API.dto.BookingDTO;
import com.saiTurf.API.model.BookingModel;
import com.saiTurf.API.model.UserModel;
import com.saiTurf.API.model.TurfDetailModel;
import com.saiTurf.API.service.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ModelMapper modelMapper;

    // ✅ Get All Bookings as DTOs
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<BookingDTO> bookingDTOs = bookingService.getAllBookings();
        return ResponseEntity.ok(bookingDTOs);
    }


    // ✅ Get Booking by ID as DTO
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        Optional<BookingModel> booking = bookingService.getBookingById(id);
        return booking.map(b -> ResponseEntity.ok(convertToDTO(b)))
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Save or Update Booking using DTO
    @PostMapping("/save")
    public ResponseEntity<BookingDTO> saveOrUpdateBooking(@RequestBody BookingDTO bookingDTO) {
        BookingDTO savedBookingDTO;

        if (bookingDTO.getId() == null) {
            savedBookingDTO = bookingService.saveBooking(bookingDTO); // New booking
        } else {
            savedBookingDTO = bookingService.updateBooking(bookingDTO.getId(), bookingDTO); // Update existing
        }

        return ResponseEntity.ok(savedBookingDTO);
    }


    // ✅ Delete Booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Convert BookingModel → BookingDTO
    private BookingDTO convertToDTO(BookingModel booking) {
        return new BookingDTO(
            booking.getId(),
            booking.getUser().getId(),
            booking.getTurf().getId(),
            booking.getBookingDate(),
            booking.getStartTime(),
            booking.getEndTime(),
            booking.getTotalPrice(),
            booking.getStatus().name() // Convert Enum to String
        );
    }

    // ✅ Convert BookingDTO → BookingModel
    private BookingModel convertToEntity(BookingDTO bookingDTO) {
        BookingModel booking = new BookingModel();
        booking.setId(bookingDTO.getId());

        // Fetch user and turf from DB
        UserModel user = bookingService.getUserById(bookingDTO.getUserId());
        TurfDetailModel turf = bookingService.getTurfById(bookingDTO.getTurfId());

        booking.setUser(user);
        booking.setTurf(turf);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setTotalPrice(bookingDTO.getTotalPrice());
        booking.setStatus(BookingModel.BookingStatus.valueOf(bookingDTO.getStatus()));

        return booking;
    }
}
