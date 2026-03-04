package com.blackrock.selfinvestment.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnsResponseIndexDTO {
    private double totalTransactionAmount;
    private double totalCeiling;
    private List<SavingsByDatesIndexDTO> savingByDates;
}
