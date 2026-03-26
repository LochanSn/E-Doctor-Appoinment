package com.hotel.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class BookingRequest {

    @NotNull
    private Long hotelId;

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    private LocalDate checkOutDate;

    @NotNull
    @Min(1)
    private Integer guests;

    private List<String> services;

    private String specialRequest;
}