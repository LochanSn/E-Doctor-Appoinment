package com.hotel.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long all;
    private long available;
    private long occupied;
    private long reserved;
    private long maintenance;
}