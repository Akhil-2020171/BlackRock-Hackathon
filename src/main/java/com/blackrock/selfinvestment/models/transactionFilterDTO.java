package com.blackrock.selfinvestment.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class transactionFilterDTO {
    private List<qMomentsDTO> q;
    private List<pMomentsDTO> p;
    private List<kGroupsDTO> k;
    private double wage;
    private List<transactionsDTO> transactions;
}
