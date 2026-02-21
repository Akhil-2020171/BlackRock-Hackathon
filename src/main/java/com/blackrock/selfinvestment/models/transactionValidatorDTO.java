package com.blackrock.selfinvestment.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class transactionValidatorDTO {
    List<transactionResponseDTO> transactions;
    double wage;
}
