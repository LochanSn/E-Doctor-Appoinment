package com.hotel.management.service;

import com.hotel.management.dto.BookingRequest;
import com.hotel.management.entity.Booking;
import com.hotel.management.entity.BookingStatus;
import com.hotel.management.entity.Hotel;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.RoomStatus;
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.HotelRepository;
import com.hotel.management.repository.RoomRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    private static final Map<String, Double> SERVICE_PRICES = new LinkedHashMap<>();

    static {
        SERVICE_PRICES.put("BREAKFAST", 15.0);
        SERVICE_PRICES.put("AIRPORT_PICKUP", 30.0);
        SERVICE_PRICES.put("SPA_ACCESS", 40.0);
        SERVICE_PRICES.put("CITY_TOUR", 25.0);
    }

    public Booking createBooking(String customerEmail, BookingRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }

        Room room = roomRepository
                .findFirstByHotelIdAndStatusAndMinCapacityLessThanEqualAndMaxCapacityGreaterThanEqualOrderByFloorNumberAscRoomNumberAsc(
                        hotel.getId(),
                        RoomStatus.AVAILABLE,
                        request.getGuests(),
                        request.getGuests())
                .orElseThrow(() -> new IllegalArgumentException("No available room matches this guest count"));

        List<String> selectedServices = request.getServices() == null
                ? List.of()
                : request.getServices().stream()
                .map(service -> service == null ? "" : service.trim().toUpperCase())
                .filter(SERVICE_PRICES::containsKey)
                .toList();

        double roomCost = nights * hotel.getPricePerNight();
        double serviceCost = selectedServices.stream().mapToDouble(SERVICE_PRICES::get).sum();

        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);

        Booking booking = Booking.builder()
                .customerEmail(customerEmail)
                .hotelId(hotel.getId())
                .hotelName(hotel.getName())
                .roomId(room.getId())
                .roomNumber(room.getRoomNumber())
                .floorNumber(room.getFloorNumber())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .guests(request.getGuests())
                .nights((int) nights)
                .bookedServices(selectedServices.stream().collect(Collectors.joining(", ")))
                .specialRequest(request.getSpecialRequest())
                .totalAmount(roomCost + serviceCost)
                .status(BookingStatus.BOOKED)
                .createdAt(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(String customerEmail) {
        return bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);
    }

    public Booking cancelBooking(Long bookingId, String customerEmail) {
        Booking booking = bookingRepository.findByIdAndCustomerEmail(bookingId, customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);

        if (booking.getRoomId() != null) {
            Room room = roomRepository.findById(booking.getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned room not found"));
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }

        return bookingRepository.save(booking);
    }
}