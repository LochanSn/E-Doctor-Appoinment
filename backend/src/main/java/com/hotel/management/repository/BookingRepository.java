package com.hotel.management.repository;

import com.hotel.management.entity.Booking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
    Optional<Booking> findByIdAndCustomerEmail(Long id, String customerEmail);
}