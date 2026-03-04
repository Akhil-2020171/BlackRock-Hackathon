package com.blackrock.selfinvestment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackrock.selfinvestment.models.ReturnsRequestDTO;
import com.blackrock.selfinvestment.models.ReturnsResponseDTO;
import com.blackrock.selfinvestment.models.ReturnsResponseIndexDTO;
import com.blackrock.selfinvestment.service.ReturnsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Controller for handling requests related to self-investment returns, including NPS calculations.
 */
@RestController
@RequestMapping("/blackrock/challenge/v1")
public class SelfInvestmentReturnController {
    
    private ReturnsService returnsService = new ReturnsService();

    SelfInvestmentReturnController(ReturnsService returnsService) {
        this.returnsService = returnsService;
    }

    @PostMapping(path="returns:nps", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> calculateNPS(@RequestBody ReturnsRequestDTO request) {
        ReturnsResponseDTO response = returnsService.calculateNPS(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path="returns:index", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> calculateIndexReturns(@RequestBody ReturnsRequestDTO request) {
        ReturnsResponseIndexDTO response = returnsService.calculateIndexReturns(request);
        return ResponseEntity.ok(response);
    }
}