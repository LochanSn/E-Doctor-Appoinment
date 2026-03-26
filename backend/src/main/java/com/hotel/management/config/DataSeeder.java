package com.hotel.management.config;

import com.hotel.management.entity.Hotel;
import com.hotel.management.entity.Role;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.RoomStatus;
import com.hotel.management.entity.User;
import com.hotel.management.repository.HotelRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedHotelRavi();
    }

    private void seedAdmin() {
        if (!userRepository.existsByEmail("owner@hotel.com")) {
            userRepository.save(User.builder()
                    .name("Owner")
                    .email("owner@hotel.com")
                    .password(passwordEncoder.encode("Owner@123"))
                    .role(Role.ADMIN)
                    .build());
        }
    }

    private void seedHotelRavi() {
        Hotel hotel = hotelRepository.findAll().stream()
                .filter(item -> "Hotel Ravi".equalsIgnoreCase(item.getName()))
                .findFirst()
                .orElseGet(() -> hotelRepository.save(Hotel.builder()
                        .name("Hotel Ravi")
                        .city("Bengaluru")
                        .address("MG Road")
                        .pricePerNight(120.0)
                        .build()));

        if (roomRepository.findAllByOrderByHotelNameAscFloorNumberAscRoomNumberAsc().stream()
                .noneMatch(room -> room.getHotelId().equals(hotel.getId()))) {

            List<Room> rooms = List.of(
                    createRoom(hotel, "101", 1, 2, 5, RoomStatus.AVAILABLE),
                    createRoom(hotel, "102", 1, 2, 5, RoomStatus.AVAILABLE),
                    createRoom(hotel, "103", 1, 2, 5, RoomStatus.RESERVED),
                    createRoom(hotel, "104", 1, 2, 5, RoomStatus.MAINTENANCE),
                    createRoom(hotel, "201", 2, 6, 7, RoomStatus.AVAILABLE),
                    createRoom(hotel, "202", 2, 6, 7, RoomStatus.OCCUPIED),
                    createRoom(hotel, "203", 2, 6, 7, RoomStatus.AVAILABLE),
                    createRoom(hotel, "204", 2, 6, 7, RoomStatus.AVAILABLE),
                    createRoom(hotel, "301", 3, 2, 5, RoomStatus.AVAILABLE),
                    createRoom(hotel, "302", 3, 2, 5, RoomStatus.AVAILABLE)
            );
            roomRepository.saveAll(rooms);
        }
    }

    private Room createRoom(Hotel hotel, String number, int floor, int minCap, int maxCap, RoomStatus status) {
        return Room.builder()
                .hotelId(hotel.getId())
                .hotelName(hotel.getName())
                .roomNumber(number)
                .floorNumber(floor)
                .minCapacity(minCap)
                .maxCapacity(maxCap)
                .status(status)
                .build();
    }
}