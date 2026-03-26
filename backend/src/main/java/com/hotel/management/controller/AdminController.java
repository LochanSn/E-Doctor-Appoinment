package com.hotel.management.controller;

import com.hotel.management.dto.DashboardSummaryResponse;
import com.hotel.management.dto.RoomRequest;
import com.hotel.management.dto.RoomStatusUpdateRequest;
import com.hotel.management.entity.Hotel;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.RoomStatus;
import com.hotel.management.service.HotelService;
import com.hotel.management.service.RoomService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final HotelService hotelService;
    private final RoomService roomService;

    @PostMapping("/hotels")
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelService.createHotel(hotel));
    }

    @PutMapping("/hotels/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelService.updateHotel(id, hotel));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(roomService.getDashboardSummary());
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getRooms(@RequestParam(required = false) RoomStatus status) {
        return ResponseEntity.ok(roomService.getRooms(status));
    }

    @PostMapping("/rooms")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @PutMapping("/rooms/{id}/status")
    public ResponseEntity<Room> updateRoomStatus(@PathVariable Long id, @Valid @RequestBody RoomStatusUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoomStatus(id, request.getStatus()));
    }
}