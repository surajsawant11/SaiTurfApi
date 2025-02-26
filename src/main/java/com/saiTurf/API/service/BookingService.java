package com.saiTurf.API.service;

import com.saiTurf.API.dto.BookingDTO;
import com.saiTurf.API.dto.TurfDTO;
import com.saiTurf.API.dto.UserDTO;
import com.saiTurf.API.model.BookingModel;
import com.saiTurf.API.model.TurfDetailModel;
import com.saiTurf.API.model.UserModel;
import com.saiTurf.API.repository.BookingRepository;
import com.saiTurf.API.repository.TurfRepository;
import com.saiTurf.API.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // ✅ Get all bookings
    public List<BookingDTO> getAllBookings() {
        List<BookingModel> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    
    public List<BookingDTO> getBookingsByUserId(Long userId) {
        List<BookingModel> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    // ✅ Get booking by ID
    public Optional<BookingDTO> getBookingById(Long id) {
        Optional<BookingModel> booking = bookingRepository.findById(id);
        return booking.map(this::convertToDTO);
    }

    // ✅ Save a new booking
    public BookingDTO saveBooking(BookingDTO bookingDTO) {
        BookingModel booking = convertToEntity(bookingDTO);
        BookingModel savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }

    // ✅ Update an existing booking
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        BookingModel existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        existingBooking.setBookingDate(bookingDTO.getBookingDate());
        existingBooking.setStartTime(bookingDTO.getStartTime());
        existingBooking.setEndTime(bookingDTO.getEndTime());
        existingBooking.setTotalPrice(bookingDTO.getTotalPrice());
        existingBooking.setStatus(BookingModel.BookingStatus.valueOf(bookingDTO.getStatus()));

        BookingModel updatedBooking = bookingRepository.save(existingBooking);
        return convertToDTO(updatedBooking);
    }

    // ✅ Delete a booking
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    // ✅ Convert BookingModel → BookingDTO
    private BookingDTO convertToDTO(BookingModel booking) {
        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        bookingDTO.setUser(modelMapper.map(booking.getUser(), UserDTO.class));
        bookingDTO.setTurf(modelMapper.map(booking.getTurf(), TurfDTO.class));
        bookingDTO.setStatus(booking.getStatus().name());
        return bookingDTO;
    }

    // ✅ Convert BookingDTO → BookingModel
    private BookingModel convertToEntity(BookingDTO bookingDTO) {
        BookingModel booking = new BookingModel();

        // Fetch user and turf from DB
        UserModel user = userRepository.findById(bookingDTO.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        TurfDetailModel turf = turfDetailRepository.findById(bookingDTO.getTurf().getId())
                .orElseThrow(() -> new RuntimeException("Turf not found"));

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
