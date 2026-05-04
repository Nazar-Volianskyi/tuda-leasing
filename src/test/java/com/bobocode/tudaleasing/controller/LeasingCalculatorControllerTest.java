package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.LeasingCalculatorRequest;
import com.bobocode.tudaleasing.dto.LeasingCalculatorResponse;
import com.bobocode.tudaleasing.security.JwtAuthenticationFilter;
import com.bobocode.tudaleasing.security.JwtUtil;
import com.bobocode.tudaleasing.security.UserDetailsServiceImpl;
import com.bobocode.tudaleasing.service.LeasingCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeasingCalculatorController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("LeasingCalculatorController – slice tests")
class LeasingCalculatorControllerTest {

    @Autowired MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean LeasingCalculatorService calculatorService;
    @MockitoBean CacheManager             cacheManager;

    @MockitoBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean JwtUtil                 jwtUtil;
    @MockitoBean UserDetailsServiceImpl  userDetailsService;


    @Test
    @DisplayName("POST /api/calculator/leasing – returns 200 with leasing response")
    void calculateLeasing_returns200() throws Exception {
        LeasingCalculatorResponse response = LeasingCalculatorResponse.builder()
                .monthlyPaymentNetto(new BigDecimal("2500.00"))
                .monthlyPaymentBrutto(new BigDecimal("3075.00"))
                .initialFeeAmount(new BigDecimal("20000"))
                .buyoutAmount(new BigDecimal("10000"))
                .build();

        when(calculatorService.calculateLeasing(any())).thenReturn(response);

        LeasingCalculatorRequest request = buildRequest(new BigDecimal("100000"), 48, 20, 10);

        mockMvc.perform(post("/api/calculator/leasing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPaymentNetto").value(2500.00))
                .andExpect(jsonPath("$.monthlyPaymentBrutto").value(3075.00))
                .andExpect(jsonPath("$.initialFeeAmount").value(20000))
                .andExpect(jsonPath("$.buyoutAmount").value(10000));
    }

    @Test
    @DisplayName("POST /api/calculator/leasing – null fields are absent in response")
    void calculateLeasing_creditFieldsAbsent() throws Exception {
        LeasingCalculatorResponse response = LeasingCalculatorResponse.builder()
                .monthlyPaymentNetto(new BigDecimal("2500.00"))
                .monthlyPaymentBrutto(new BigDecimal("3075.00"))
                .initialFeeAmount(new BigDecimal("20000"))
                .buyoutAmount(new BigDecimal("10000"))
                .build();

        when(calculatorService.calculateLeasing(any())).thenReturn(response);

        LeasingCalculatorRequest request = buildRequest(new BigDecimal("100000"), 48, 20, 10);

        mockMvc.perform(post("/api/calculator/leasing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPaymentCredit").doesNotExist());
    }


    @Test
    @DisplayName("POST /api/calculator/credit – returns 200 with credit response")
    void calculateCredit_returns200() throws Exception {
        LeasingCalculatorResponse response = LeasingCalculatorResponse.builder()
                .monthlyPaymentCredit(new BigDecimal("1800.50"))
                .initialFeeAmount(new BigDecimal("15000"))
                .rrso(new BigDecimal("12.18"))
                .build();

        when(calculatorService.calculateCredit(any())).thenReturn(response);

        LeasingCalculatorRequest request = buildRequest(new BigDecimal("80000"), 60, 15, 0);

        mockMvc.perform(post("/api/calculator/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPaymentCredit").value(1800.50))
                .andExpect(jsonPath("$.initialFeeAmount").value(15000))
                .andExpect(jsonPath("$.rrso").value(12.18));
    }

    @Test
    @DisplayName("POST /api/calculator/credit – leasing fields absent in credit response")
    void calculateCredit_leasingFieldsAbsent() throws Exception {
        LeasingCalculatorResponse response = LeasingCalculatorResponse.builder()
                .monthlyPaymentCredit(new BigDecimal("1800.50"))
                .initialFeeAmount(new BigDecimal("15000"))
                .rrso(new BigDecimal("12.18"))
                .build();

        when(calculatorService.calculateCredit(any())).thenReturn(response);

        LeasingCalculatorRequest request = buildRequest(new BigDecimal("80000"), 60, 15, 0);

        mockMvc.perform(post("/api/calculator/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPaymentNetto").doesNotExist())
                .andExpect(jsonPath("$.buyoutAmount").doesNotExist());
    }


    private LeasingCalculatorRequest buildRequest(BigDecimal carPrice, int months,
                                                  int initialPct, int buyoutPct) {
        LeasingCalculatorRequest req = new LeasingCalculatorRequest();
        req.setCarPrice(carPrice);
        req.setPeriodMonths(months);
        req.setInitialFeePercent(initialPct);
        req.setBuyoutPercent(buyoutPct);
        return req;
    }
}
