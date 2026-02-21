package com.blackrock.selfinvestment.controller;

import org.springframework.web.bind.annotation.RestController;

import com.blackrock.selfinvestment.models.transactionFilterDTO;
import com.blackrock.selfinvestment.models.transactionResponseDTO;
import com.blackrock.selfinvestment.models.transactionValidatorDTO;
import com.blackrock.selfinvestment.models.transactionsDTO;
import com.blackrock.selfinvestment.service.TransactionService;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class SelfInvestmentController {

    private TransactionService transactionService = new TransactionService();     

    SelfInvestmentController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(path="/transactions:parse", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> parseTransactions(@RequestBody List<transactionsDTO> transactions) {
        List<transactionResponseDTO> responseList = transactionService.parseTransactions(transactions);
        return ResponseEntity.ok(responseList);
    }

    @PostMapping(path="/transactions:validator", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> validateTransactions(@RequestBody  transactionValidatorDTO transactions) {
        Map<String, Object> response = transactionService.validateTransactions(transactions);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path="/transactions:filter", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> filterTransactions(@RequestBody transactionFilterDTO transactions) {
        Map<String, Object> response = transactionService.filterAndValidate(transactions);
        return ResponseEntity.ok(response);
    }
}
