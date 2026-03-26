package com.hotel.management.dto;

import com.hotel.management.entity.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomStatusUpdateRequest {

    @NotNull
    private RoomStatus status;
}