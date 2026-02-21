package com.blackrock.selfinvestment.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.blackrock.selfinvestment.helper.Helper;
import com.blackrock.selfinvestment.models.invalidTransactionDTO;
import com.blackrock.selfinvestment.models.kGroupsDTO;
import com.blackrock.selfinvestment.models.pMomentsDTO;
import com.blackrock.selfinvestment.models.qMomentsDTO;
import com.blackrock.selfinvestment.models.transactionFilterDTO;
import com.blackrock.selfinvestment.models.transactionFilterResponseDTO;
import com.blackrock.selfinvestment.models.transactionFilterValidatorDTO;
import com.blackrock.selfinvestment.models.transactionResponseDTO;
import com.blackrock.selfinvestment.models.transactionValidatorDTO;
import com.blackrock.selfinvestment.models.transactionsDTO;
import com.blackrock.selfinvestment.models.validTransactionDTO;
import com.blackrock.selfinvestment.models.validTransactionFilterDTO;

@Service
public class TransactionService {

    public List<transactionResponseDTO> parseTransactions(List<transactionsDTO> transactions) {
        List<transactionResponseDTO> responseList = new ArrayList<>();
        for (transactionsDTO transaction : transactions) {
            double ammount = transaction.getAmount();
            Date date = transaction.getDate();
            double ceiling = Math.ceil(ammount / 100) * 100;
            double remanent = ceiling - ammount;
            responseList.add(new transactionResponseDTO(date, ammount, ceiling, remanent));
        }
        return responseList;
    }

    public Map<String, Object> validateTransactions(transactionValidatorDTO transactions) {
        List<transactionResponseDTO> transactionList = transactions.getTransactions();

        Map<String, Object> response = new HashMap<>();
        List<validTransactionDTO> validatedTransactions = new ArrayList<>();
        List<invalidTransactionDTO> invalidTransactions = new ArrayList<>();

        for (transactionResponseDTO transaction : transactionList) {
            Date date = transaction.getDate();
            double ammount = transaction.getAmount();
            double ceiling = transaction.getCeiling();
            double remanent = transaction.getRemanent();

            if (ammount <= 0) {
                invalidTransactions.add(new invalidTransactionDTO(date, ammount, ceiling, remanent,
                        "Negative or zero amount is not allowed"));
            } else if (ammount > transactions.getWage()) {
                invalidTransactions
                        .add(new invalidTransactionDTO(date, ammount, ceiling, remanent, "Amount exceeds wage"));
            } // Duplicate transaction check
            else if (validatedTransactions.stream()
                    .anyMatch(t -> t.getDate().equals(date) && t.getAmount() == ammount)) {
                invalidTransactions
                        .add(new invalidTransactionDTO(date, ammount, ceiling, remanent, "Duplicate transaction"));
            } else {
                validatedTransactions.add(new validTransactionDTO(date, ammount, ceiling, remanent));
            }
        }

        response.put("valid", validatedTransactions);
        response.put("invalid", invalidTransactions);
        return response;

    }

    public Map<String, Object> validateTransactions(transactionFilterValidatorDTO transactions) {
        List<transactionFilterResponseDTO> transactionList = transactions.getTransactions();

        Map<String, Object> response = new HashMap<>();
        List<validTransactionFilterDTO> validatedTransactions = new ArrayList<>();
        List<invalidTransactionDTO> invalidTransactions = new ArrayList<>();

        for (transactionFilterResponseDTO transaction : transactionList) {
            Date date = transaction.getDate();
            double ammount = transaction.getAmount();
            double ceiling = transaction.getCeiling();
            double remanent = transaction.getRemanent();

            if (ammount <= 0) {
                invalidTransactions.add(new invalidTransactionDTO(date, ammount, ceiling, remanent,
                        "Negative or zero amount is not allowed"));
            } else if (ammount > transactions.getWage()) {
                invalidTransactions
                        .add(new invalidTransactionDTO(date, ammount, ceiling, remanent, "Amount exceeds wage"));
            } // Duplicate transaction check
            else if (validatedTransactions.stream()
                    .anyMatch(t -> t.getDate().equals(date) && t.getAmount() == ammount)) {
                invalidTransactions
                        .add(new invalidTransactionDTO(date, ammount, ceiling, remanent, "Duplicate transaction"));
            } else {
                validatedTransactions.add(new validTransactionFilterDTO(date, ammount, ceiling, remanent, transaction.isInKPeriod()));
            }
        }

        response.put("valid", validatedTransactions);
        response.put("invalid", invalidTransactions);
        return response;

    }

    public Map<String, Object> filterAndValidate(transactionFilterDTO filterDTO) {
        List<transactionFilterResponseDTO> calculatedList = new ArrayList<>();

        for (transactionsDTO transaction : filterDTO.getTransactions()) {

            Date date = transaction.getDate();
            double originalAmount = transaction.getAmount();

            if (originalAmount <= 0) {
                calculatedList.add(
                    new transactionFilterResponseDTO(
                        date,
                        originalAmount,
                        0,
                        0,
                        false
                    )
                );
                continue;
            }

            double ceiling = Math.ceil(originalAmount / 100) * 100;
            double remanent = ceiling - originalAmount;

            boolean inKPeriod = false;

            // ---------------- Q RULE ----------------
            qMomentsDTO selectedQ = null;
            if (filterDTO.getQ() != null) {
                for (qMomentsDTO q : filterDTO.getQ()) {
                    if (Helper.isBetweenInclusive(date, q.getStart(), q.getEnd())) {
                        if (selectedQ == null ||
                                q.getStart().after(selectedQ.getStart())) {
                            selectedQ = q;
                        }
                    }
                }
            }

            if (selectedQ != null) {

                if (selectedQ.getFixed() == 0) {
                    continue;
                }

                remanent = selectedQ.getFixed();
                ceiling = originalAmount + remanent;
            }

            // ---------------- P RULE ----------------
            if (filterDTO.getP() != null) {
                for (pMomentsDTO p : filterDTO.getP()) {
                    if (Helper.isBetweenInclusive(date, p.getStart(), p.getEnd())) {
                        remanent += p.getExtra();
                    }
                }
            }

            // ---------------- K RULE ----------------
            if (filterDTO.getK() != null) {
                for (kGroupsDTO k : filterDTO.getK()) {
                    if (Helper.isBetweenInclusive(date, k.getStart(), k.getEnd())) {
                        inKPeriod = true;
                    }
                }
            }

            calculatedList.add(
                    new transactionFilterResponseDTO(
                            date,
                            originalAmount,
                            ceiling,
                            remanent,
                            inKPeriod));
        }

        // Validate using your existing function
        transactionFilterValidatorDTO validatorDTO =
                new transactionFilterValidatorDTO(calculatedList, filterDTO.getWage());

        return validateTransactions(validatorDTO);
    }
}
