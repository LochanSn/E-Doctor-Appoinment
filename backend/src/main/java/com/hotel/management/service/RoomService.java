package com.hotel.management.service;

import com.hotel.management.dto.DashboardSummaryResponse;
import com.hotel.management.dto.RoomRequest;
import com.hotel.management.entity.Hotel;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.RoomStatus;
import com.hotel.management.repository.HotelRepository;
import com.hotel.management.repository.RoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public Room createRoom(RoomRequest request) {
        if (request.getMinCapacity() > request.getMaxCapacity()) {
            throw new IllegalArgumentException("minCapacity cannot be greater than maxCapacity");
        }

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));

        if (roomRepository.existsByHotelIdAndRoomNumber(hotel.getId(), request.getRoomNumber().trim())) {
            throw new IllegalArgumentException("Room number already exists for this hotel");
        }

        Room room = Room.builder()
                .hotelId(hotel.getId())
                .hotelName(hotel.getName())
                .roomNumber(request.getRoomNumber().trim())
                .floorNumber(request.getFloorNumber())
                .minCapacity(request.getMinCapacity())
                .maxCapacity(request.getMaxCapacity())
                .status(request.getStatus() == null ? RoomStatus.AVAILABLE : request.getStatus())
                .build();

        return roomRepository.save(room);
    }

    public List<Room> getRooms(RoomStatus status) {
        if (status == null) {
            return roomRepository.findAllByOrderByHotelNameAscFloorNumberAscRoomNumberAsc();
        }
        return roomRepository.findByStatusOrderByHotelNameAscFloorNumberAscRoomNumberAsc(status);
    }

    public Room updateRoomStatus(Long roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setStatus(status);
        return roomRepository.save(room);
    }

    public DashboardSummaryResponse getDashboardSummary() {
        long all = roomRepository.count();
        long available = roomRepository.findByStatusOrderByHotelNameAscFloorNumberAscRoomNumberAsc(RoomStatus.AVAILABLE).size();
        long occupied = roomRepository.findByStatusOrderByHotelNameAscFloorNumberAscRoomNumberAsc(RoomStatus.OCCUPIED).size();
        long reserved = roomRepository.findByStatusOrderByHotelNameAscFloorNumberAscRoomNumberAsc(RoomStatus.RESERVED).size();
        long maintenance = roomRepository.findByStatusOrderByHotelNameAscFloorNumberAscRoomNumberAsc(RoomStatus.MAINTENANCE).size();

        return new DashboardSummaryResponse(all, available, occupied, reserved, maintenance);
    }
}