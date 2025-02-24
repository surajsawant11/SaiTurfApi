package com.saiTurf.API.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saiTurf.API.model.BookingModel;
import com.saiTurf.API.repository.BookingRepository;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public BookingModel saveBooking(BookingModel booking) {
        return bookingRepository.save(booking);
    }

    public List<BookingModel> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<BookingModel> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public BookingModel updateBooking(Long id, BookingModel updatedBooking) {
        return bookingRepository.findById(id)
            .map(booking -> {
                booking.setStatus(updatedBooking.getStatus());
                booking.setUpdatedAt(updatedBooking.getUpdatedAt());
                booking.setCancelledAt(updatedBooking.getCancelledAt());
                return bookingRepository.save(booking);
            })
            .orElseThrow(() -> new RuntimeException("Booking not found with id " + id));
    }
}
