package com.blackrock.selfinvestment.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class transactionFilterDTO {
    List<qMomentsDTO> q;
    List<pMomentsDTO> p;
    List<kGroupsDTO> k;
    double wage;
    List<transactionsDTO> transactions;
}
