package com.blackrock.selfinvestment.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class validTransactionFilterDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date date;
    double amount;
    double ceiling;
    double remanent;
    boolean inKPeriod;
}
