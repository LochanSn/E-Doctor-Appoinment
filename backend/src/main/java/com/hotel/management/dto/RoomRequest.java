package com.hotel.management.dto;

import com.hotel.management.entity.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequest {

    @NotNull
    private Long hotelId;

    @NotBlank
    private String roomNumber;

    @NotNull
    @Min(1)
    private Integer floorNumber;

    @NotNull
    @Min(1)
    private Integer minCapacity;

    @NotNull
    @Min(1)
    private Integer maxCapacity;

    private RoomStatus status;
}