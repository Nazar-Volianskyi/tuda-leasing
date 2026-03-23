package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.LeasingCalculatorRequest;
import com.bobocode.tudaleasing.dto.LeasingCalculatorResponse;
import com.bobocode.tudaleasing.service.LeasingCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculator")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeasingCalculatorController {

    private final LeasingCalculatorService calculatorService;

    @PostMapping("/leasing")
    public ResponseEntity<LeasingCalculatorResponse> calculateLeasing(
            @RequestBody LeasingCalculatorRequest request) {
        return ResponseEntity.ok(calculatorService.calculateLeasing(request));
    }

    @PostMapping("/credit")
    public ResponseEntity<LeasingCalculatorResponse> calculateCredit(
            @RequestBody LeasingCalculatorRequest request) {
        return ResponseEntity.ok(calculatorService.calculateCredit(request));
    }
}