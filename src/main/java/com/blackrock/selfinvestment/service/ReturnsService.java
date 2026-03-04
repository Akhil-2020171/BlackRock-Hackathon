package com.blackrock.selfinvestment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.blackrock.selfinvestment.helper.Helper;
import com.blackrock.selfinvestment.models.ReturnsRequestDTO;
import com.blackrock.selfinvestment.models.ReturnsResponseDTO;
import com.blackrock.selfinvestment.models.ReturnsResponseIndexDTO;
import com.blackrock.selfinvestment.models.SavingByDatesDTO;
import com.blackrock.selfinvestment.models.SavingsByDatesIndexDTO;
import com.blackrock.selfinvestment.models.kGroupResponseDTO;
import com.blackrock.selfinvestment.models.kGroupsDTO;
import com.blackrock.selfinvestment.models.pMomentsDTO;
import com.blackrock.selfinvestment.models.qMomentsDTO;
import com.blackrock.selfinvestment.models.transactionResponseDTO;
import com.blackrock.selfinvestment.models.transactionsDTO;

@Service
public class ReturnsService {

    public ReturnsResponseDTO calculateNPS(ReturnsRequestDTO request) {
        double monthlySalary = request.getWage();
        double yearlySalary = monthlySalary * 12;

        double totalCeiling = 0;
        double totalAmount = 0;
        List<transactionResponseDTO> transactions = new ArrayList<>();
        for(transactionsDTO transaction : request.getTransactions()) {
            LocalDateTime date = transaction.getDate();
            double amount = transaction.getAmount();
            if(amount < 0 || amount > monthlySalary) continue; // Skip invalid transactions
            
            double ceiling = Math.ceil(amount/100) * 100; // Round up to nearest 100
            double remanent = ceiling - amount;
            transactions.add(new transactionResponseDTO(date, amount, ceiling, remanent));
            
            totalCeiling += ceiling;
            totalAmount += amount;
        }

        List<kGroupResponseDTO> kGroupResponses = new ArrayList<>();
        for(kGroupsDTO group : request.getK()) {
            LocalDateTime kStart = group.getStart();
            LocalDateTime kEnd = group.getEnd();
            double kAmount = 0;
            // System.out.println("Processing k group: " + kStart + " to " + kEnd);

            // 1. Need to check if any transaction falls within this k period
            for(transactionResponseDTO transaction : transactions) {
                LocalDateTime transactionDate = transaction.getDate();
                if(Helper.isBetweenInclusive(transactionDate, kStart, kEnd)) {
                    double remanent = transaction.getRemanent();
                    // If any transaction falls with q
                    // System.out.println("Transaction date: " + transactionDate);
                    for(qMomentsDTO q : request.getQ()) {
                        LocalDateTime qStart = q.getStart();
                        LocalDateTime qEnd = q.getEnd();
                        // System.out.println("Checking q moment: " + qStart + " to " + qEnd);
                        if(Helper.isBetweenInclusive(transactionDate, qStart, qEnd)) {
                            remanent = q.getFixed();
                            // System.out.println("Found q moment for transaction date: " + transactionDate);
                            break;
                        }
                    }

                    // If any transaction falls with p
                    for(pMomentsDTO p : request.getP()) {
                        LocalDateTime pStart = p.getStart();
                        LocalDateTime pEnd = p.getEnd();
                        // System.out.println("Checking p moment: " + pStart + " to " + pEnd);
                        if(Helper.isBetweenInclusive(transactionDate, pStart, pEnd)) {
                            remanent += p.getExtra(); 
                            // System.out.println("Found p moment for transaction date: " + transactionDate);
                        }
                    }
                    // System.out.println("Adding remanent: " + remanent + " for transaction date: " + transactionDate);
                    kAmount += remanent;
                }
            }
            kGroupResponses.add(new kGroupResponseDTO(kAmount, kStart, kEnd));
        }

        double inflation = request.getInflation()/100.0; 
        int timePeriod = 60 - request.getAge(); // Assuming retirement age is 60
        List<SavingByDatesDTO> savingByDates = new ArrayList<>();
        for(kGroupResponseDTO kGroup : kGroupResponses) {
            double amount = kGroup.getAmount();
            LocalDateTime start = kGroup.getStart();
            LocalDateTime end = kGroup.getEnd();

            double nps_dedcution = Math.min(Math.min(amount, 0.1 * yearlySalary), 200000);
            double taxBenefit = Helper.taxPerSalary(yearlySalary) - Helper.taxPerSalary(yearlySalary - nps_dedcution);    
        
            double compoundInterest = amount * Math.pow(1.0711, timePeriod);
            double npsRealValue = compoundInterest / Math.pow(1 + inflation, timePeriod);
            double profit = npsRealValue - amount;
            profit = Math.round(profit * 100.0) / 100.0;
            savingByDates.add(new SavingByDatesDTO(start, end, amount, profit, taxBenefit));
        }

        return new ReturnsResponseDTO(totalAmount, totalCeiling, savingByDates);
    }

    public ReturnsResponseIndexDTO calculateIndexReturns(ReturnsRequestDTO request) {
        double monthlySalary = request.getWage();
        double yearlySalary = monthlySalary * 12;

        double totalCeiling = 0;
        double totalAmount = 0;
        List<transactionResponseDTO> transactions = new ArrayList<>();
        for(transactionsDTO transaction : request.getTransactions()) {
            LocalDateTime date = transaction.getDate();
            double amount = transaction.getAmount();
            if(amount < 0 || amount > monthlySalary) continue; // Skip invalid transactions
            
            double ceiling = Math.ceil(amount/100) * 100; // Round up to nearest 100
            double remanent = ceiling - amount;
            transactions.add(new transactionResponseDTO(date, amount, ceiling, remanent));
            
            totalCeiling += ceiling;
            totalAmount += amount;
        }

        List<kGroupResponseDTO> kGroupResponses = new ArrayList<>();
        for(kGroupsDTO group : request.getK()) {
            LocalDateTime kStart = group.getStart();
            LocalDateTime kEnd = group.getEnd();
            double kAmount = 0;
            // System.out.println("Processing k group: " + kStart + " to " + kEnd);

            // 1. Need to check if any transaction falls within this k period
            for(transactionResponseDTO transaction : transactions) {
                LocalDateTime transactionDate = transaction.getDate();
                if(Helper.isBetweenInclusive(transactionDate, kStart, kEnd)) {
                    double remanent = transaction.getRemanent();
                    // If any transaction falls with q
                    // System.out.println("Transaction date: " + transactionDate);
                    for(qMomentsDTO q : request.getQ()) {
                        LocalDateTime qStart = q.getStart();
                        LocalDateTime qEnd = q.getEnd();
                        // System.out.println("Checking q moment: " + qStart + " to " + qEnd);
                        if(Helper.isBetweenInclusive(transactionDate, qStart, qEnd)) {
                            remanent = q.getFixed();
                            // System.out.println("Found q moment for transaction date: " + transactionDate);
                            break;
                        }
                    }

                    // If any transaction falls with p
                    for(pMomentsDTO p : request.getP()) {
                        LocalDateTime pStart = p.getStart();
                        LocalDateTime pEnd = p.getEnd();
                        // System.out.println("Checking p moment: " + pStart + " to " + pEnd);
                        if(Helper.isBetweenInclusive(transactionDate, pStart, pEnd)) {
                            remanent += p.getExtra(); 
                            // System.out.println("Found p moment for transaction date: " + transactionDate);
                        }
                    }
                    // System.out.println("Adding remanent: " + remanent + " for transaction date: " + transactionDate);
                    kAmount += remanent;
                }
            }
            kGroupResponses.add(new kGroupResponseDTO(kAmount, kStart, kEnd));
        }

        double inflation = request.getInflation()/100.0; 
        int timePeriod = 60 - request.getAge(); // Assuming retirement age is 60
        List<SavingsByDatesIndexDTO> savingByDates = new ArrayList<>();
        for(kGroupResponseDTO kGroup : kGroupResponses) {
            double amount = kGroup.getAmount();
            LocalDateTime start = kGroup.getStart();
            LocalDateTime end = kGroup.getEnd();

            double compoundInterest = amount * Math.pow(1.1449, timePeriod);
            double niftyRealValue = compoundInterest / Math.pow(1 + inflation, timePeriod);
            niftyRealValue = Math.round(niftyRealValue * 100.0) / 100.0;
            savingByDates.add(new SavingsByDatesIndexDTO(niftyRealValue, start, end));
        }

        return new ReturnsResponseIndexDTO(totalAmount, totalCeiling, savingByDates);
    }
    
}
