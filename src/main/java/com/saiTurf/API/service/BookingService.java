package com.saiTurf.API.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saiTurf.API.dto.BookingDTO;
import com.saiTurf.API.model.BookingModel;
import com.saiTurf.API.model.TurfDetailModel;
import com.saiTurf.API.model.UserModel;
import com.saiTurf.API.repository.BookingRepository;
import com.saiTurf.API.repository.TurfRepository;
import com.saiTurf.API.repository.UserRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TurfRepository turfDetailRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ✅ Get all bookings and return as DTOs
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get booking by ID
    public Optional<BookingModel> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    // ✅ Save new booking
    public BookingDTO saveBooking(BookingDTO bookingDTO) {
        BookingModel booking = modelMapper.map(bookingDTO, BookingModel.class);
        BookingModel savedBooking = bookingRepository.save(booking);
        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    // ✅ Update existing booking
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        BookingModel existingBooking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Update only non-null properties
        if (bookingDTO.getBookingDate() != null) {
            existingBooking.setBookingDate(bookingDTO.getBookingDate());
        }
        if (bookingDTO.getStartTime() != null) {
            existingBooking.setStartTime(bookingDTO.getStartTime());
        }
        if (bookingDTO.getEndTime() != null) {
            existingBooking.setEndTime(bookingDTO.getEndTime());
        }
        if (bookingDTO.getTotalPrice() != null) {
            existingBooking.setTotalPrice(bookingDTO.getTotalPrice());
        }

        // ✅ Convert String to Enum before setting status
        if (bookingDTO.getStatus() != null) {
            try {
                BookingModel.BookingStatus statusEnum = BookingModel.BookingStatus.valueOf(bookingDTO.getStatus().toUpperCase());
                existingBooking.setStatus(statusEnum);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid booking status: " + bookingDTO.getStatus());
            }
        }

        existingBooking.setUpdatedAt(LocalDateTime.now());

        // Save updated booking
        BookingModel updatedBooking = bookingRepository.save(existingBooking);
        return convertToDTO(updatedBooking);
    }


    // ✅ Delete a booking
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    // ✅ Fetch user by ID
    public UserModel getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // ✅ Fetch turf by ID
    public TurfDetailModel getTurfById(Long turfId) {
        return turfDetailRepository.findById(turfId)
                .orElseThrow(() -> new RuntimeException("Turf not found with ID: " + turfId));
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
                booking.getStatus().name()
        );
    }

    // ✅ Convert BookingDTO → BookingModel
    private BookingModel convertToEntity(BookingDTO bookingDTO) {
        BookingModel booking = new BookingModel();
        booking.setId(bookingDTO.getId());

        // Fetch user and turf from DB
        UserModel user = getUserById(bookingDTO.getUserId());
        TurfDetailModel turf = getTurfById(bookingDTO.getTurfId());

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
