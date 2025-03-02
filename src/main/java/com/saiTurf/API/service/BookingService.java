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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private TurfRepository turfRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ✅ Get all bookings
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get all bookings for a specific user
    public List<BookingDTO> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get booking by ID
    public Optional<BookingDTO> getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(this::convertToDTO);
    }

    // ✅ Save a new booking (PREVENTS OVERLAPPING BOOKINGS)
    @Transactional
    public BookingDTO saveBooking(BookingDTO bookingDTO) {
        Long turfId = bookingDTO.getTurfId();
        LocalDate bookingDate = bookingDTO.getBookingDate();
        LocalTime startTime = bookingDTO.getStartTime();
        LocalTime endTime = bookingDTO.getEndTime();

        // 🔥 Check if the turf is already booked for the given time
        try {
        	boolean isBooked = bookingRepository.existsByTurfAndTime(turfId, bookingDate);
        	if (isBooked) {
                throw new IllegalStateException("Turf is already booked for the selected date and time.");
            }
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// TODO: handle exception
		}
        

        BookingModel booking = convertToEntity(bookingDTO);
        BookingModel savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }

    // ✅ Update an existing booking (ALSO PREVENTS OVERLAPPING BOOKINGS)
    @Transactional
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        BookingModel existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Long turfId = existingBooking.getTurf().getId();
        LocalDate bookingDate = bookingDTO.getBookingDate();
        LocalTime startTime = bookingDTO.getStartTime();
        LocalTime endTime = bookingDTO.getEndTime();

        // 🔥 Check if the updated booking overlaps with another booking (excluding itself)
        boolean isBooked = bookingRepository.existsByTurfAndTime(turfId, bookingDate) 
                           && !existingBooking.getId().equals(id);
        if (isBooked) {
            throw new IllegalStateException("Turf is already booked for the selected date and time.");
        }

        // ✅ Update booking details
        existingBooking.setBookingDate(bookingDTO.getBookingDate());
        existingBooking.setStartTime(bookingDTO.getStartTime());
        existingBooking.setEndTime(bookingDTO.getEndTime());
        existingBooking.setTotalPrice(bookingDTO.getTotalPrice());
        existingBooking.setStatus(BookingModel.BookingStatus.valueOf(bookingDTO.getStatus().name()));


        BookingModel updatedBooking = bookingRepository.save(existingBooking);
        return convertToDTO(updatedBooking);
    }

    // ✅ Delete a booking
    @Transactional
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    // ✅ Convert BookingModel → BookingDTO
    private BookingDTO convertToDTO(BookingModel booking) {
        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
//        bookingDTO.setUser(modelMapper.map(booking.getUser(), UserDTO.class));
//        bookingDTO.setTurf(modelMapper.map(booking.getTurf(), TurfDTO.class));
        bookingDTO.setTurfId(booking.getTurf().getId());
        bookingDTO.setStatus(BookingDTO.BookingStatus.valueOf(booking.getStatus().name()));
        return bookingDTO;
    }

    // ✅ Convert BookingDTO → BookingModel
    private BookingModel convertToEntity(BookingDTO bookingDTO) {
        BookingModel booking = new BookingModel();

        // 🔥 Fetch user and turf from DB
        UserModel user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        TurfDetailModel turf = turfRepository.findById(bookingDTO.getTurfId())
                .orElseThrow(() -> new RuntimeException("Turf not found"));

        booking.setUser(user);
        booking.setTurf(turf);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setTotalPrice(bookingDTO.getTotalPrice());
        booking.setStatus(BookingModel.BookingStatus.valueOf(bookingDTO.getStatus().name()));

        return booking;
    }

    public List<BookingDTO> getBookedSlotsByTurf(Long turfId) {
        List<BookingModel> bookings = bookingRepository.findByTurfId(turfId);
        return bookings.stream()
                .map(booking -> {
                    BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
                    bookingDTO.setUser(null); // No need for user details
                    bookingDTO.setTurf(null); // No need for full turf details
                    bookingDTO.setStatus(BookingDTO.BookingStatus.valueOf(booking.getStatus().name())); // ✅ Correct
                    return bookingDTO;
                })
                .collect(Collectors.toList());
    }
    
    public List<LocalDate> getBookedDatesByTurfId(Long turfId) {
        return bookingRepository.findBookedDatesByTurfId(turfId);
    }


}
