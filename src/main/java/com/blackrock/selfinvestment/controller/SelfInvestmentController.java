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

/**
 * Controller class for handling transaction-related endpoints.
 */
@RestController
@RequestMapping("/blackrock/challenge/v1")
public class SelfInvestmentController {

    private TransactionService transactionService = new TransactionService();     

    SelfInvestmentController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Parse the list of transactions and return the response.
     * @param transactions List of transactions to be parsed.
     * @return List of transactionResponseDTO containing the parsed transactions.
     */
    @PostMapping(path="transactions:parse", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> parseTransactions(@RequestBody List<transactionsDTO> transactions) {
        List<transactionResponseDTO> responseList = transactionService.parseTransactions(transactions);
        return ResponseEntity.ok(responseList);
    }

    /**
     * Validate the list of transactions and return the response.
     * @param transactions transactionValidatorDTO containing the transactions to be validated and wage information.
     * @return Map containing the lists of valid and invalid transactions along with validation messages.
     */
    @PostMapping(path="transactions:validator", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> validateTransactions(@RequestBody  transactionValidatorDTO transactions) {
        Map<String, Object> response = transactionService.validateTransactions(transactions);
        return ResponseEntity.ok(response);
    }

    /**
     * Filter and validate the list of transactions based on the provided criteria.
     * @param transactions transactionFilterDTO containing the transactions to be filtered and validated along with filter criteria and wage information.
     * @return Map containing the lists of valid and invalid transactions after filtering along with validation messages.
     */
    @PostMapping(path=":filter", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> filterTransactions(@RequestBody transactionFilterDTO transactions) {
        Map<String, Object> response = transactionService.filterAndValidate(transactions);
        return ResponseEntity.ok(response);
    }
}
