package com.hotel.management.repository;

import com.hotel.management.entity.Room;
import com.hotel.management.entity.RoomStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatusOrderByHotelNameAscFloorNumberAscRoomNumberAsc(RoomStatus status);

    List<Room> findAllByOrderByHotelNameAscFloorNumberAscRoomNumberAsc();

    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);

    Optional<Room> findFirstByHotelIdAndStatusAndMinCapacityLessThanEqualAndMaxCapacityGreaterThanEqualOrderByFloorNumberAscRoomNumberAsc(
            Long hotelId,
            RoomStatus status,
            Integer minCapacity,
            Integer maxCapacity
    );
}