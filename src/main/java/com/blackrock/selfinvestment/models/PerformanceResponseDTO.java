package com.blackrock.selfinvestment.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PerformanceResponseDTO {
    private String time;      // HH:mm:ss.SSS
    private String memory;    // "XXX.XX MB"
    private int threads;
}