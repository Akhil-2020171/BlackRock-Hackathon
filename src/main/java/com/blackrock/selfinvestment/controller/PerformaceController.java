package com.blackrock.selfinvestment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackrock.selfinvestment.models.PerformanceResponseDTO;
import com.blackrock.selfinvestment.service.PerformanceService;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class PerformaceController {

    private PerformanceService performanceService = new PerformanceService();

    PerformaceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping(path="/performance", produces = "application/json")
    public ResponseEntity<?> getPerformance() {
        PerformanceResponseDTO performanceReport = performanceService.generateReport(System.currentTimeMillis());
        return ResponseEntity.ok(performanceReport);
    }
}
