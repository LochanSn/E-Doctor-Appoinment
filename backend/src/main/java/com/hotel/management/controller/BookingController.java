package com.hotel.management.controller;

import com.hotel.management.dto.BookingRequest;
import com.hotel.management.entity.Booking;
import com.hotel.management.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequest request, Authentication authentication) {
        String customerEmail = authentication.getName();
        return ResponseEntity.ok(bookingService.createBooking(customerEmail, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication) {
        String customerEmail = authentication.getName();
        return ResponseEntity.ok(bookingService.getMyBookings(customerEmail));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id, Authentication authentication) {
        String customerEmail = authentication.getName();
        return ResponseEntity.ok(bookingService.cancelBooking(id, customerEmail));
    }
}