package com.blackrock.selfinvestment.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class validTransactionFilterDTO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private double amount;
    private double ceiling;
    private double remanent;
    private boolean inKPeriod;
}
