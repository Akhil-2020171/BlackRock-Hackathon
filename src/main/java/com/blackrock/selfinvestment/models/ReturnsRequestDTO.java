package com.blackrock.selfinvestment.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnsRequestDTO {
    private int age;
    private double wage;
    private double inflation;
    private List<qMomentsDTO> q;
    private List<pMomentsDTO> p;
    private List<kGroupsDTO> k;
    private List<transactionsDTO> transactions;
}
