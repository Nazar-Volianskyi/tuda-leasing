package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.*;
import com.bobocode.tudaleasing.entity.Car;
import com.bobocode.tudaleasing.mapper.CarMapper;
import com.bobocode.tudaleasing.security.JwtAuthenticationFilter;
import com.bobocode.tudaleasing.security.JwtUtil;
import com.bobocode.tudaleasing.security.UserDetailsServiceImpl;
import com.bobocode.tudaleasing.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CarController – slice tests")
class CarControllerTest {

    @Autowired MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean CarService carService;
    @MockitoBean CarMapper  carMapper;
    @MockitoBean CacheManager cacheManager;

    @MockitoBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean JwtUtil                 jwtUtil;
    @MockitoBean UserDetailsServiceImpl  userDetailsService;


    @Test
    @DisplayName("GET /cars – returns 200 with page of cars")
    void getCars_returns200() throws Exception {
        CarCatalogDto car = new CarCatalogDto(1L, "BMW", "3 Series", "Sedan",
                "Black", 2023L, new BigDecimal("50000"), "Petrol", "Auto", "http://img.url/1.jpg");
        Page<CarCatalogDto> page = new PageImpl<>(List.of(car));

        when(carService.getCars(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].brand").value("BMW"))
                .andExpect(jsonPath("$.content[0].model").value("3 Series"));
    }

    @Test
    @DisplayName("GET /cars – returns empty page when no cars")
    void getCars_returnsEmptyPage() throws Exception {
        when(carService.getCars(any(), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }


    @Test
    @DisplayName("GET /cars/{id} – returns 200 with car details")
    void getCarById_returns200() throws Exception {
        CarDetailsDto dto = new CarDetailsDto(1L, "BMW", "3 Series", "Sedan",
                "Black", 2023L, new BigDecimal("50000"), "Comfortable car", true, List.of(), null);

        when(carService.getCarById(1L)).thenReturn(dto);

        mockMvc.perform(get("/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("BMW"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("GET /cars/{id} – throws RuntimeException when car not found")
    void getCarById_returns500WhenNotFound() {
        when(carService.getCarById(999L)).thenThrow(new RuntimeException("Car not found"));

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                mockMvc.perform(get("/cars/999")).andReturn()
        ).hasMessageContaining("Car not found");
    }


    @Test
    @DisplayName("GET /cars/filters – returns 200 with filter options")
    void getFilters_returns200() throws Exception {
        CarFiltersDto filtersDto = CarFiltersDto.builder()
                .brands(List.of("BMW", "Audi"))
                .models(List.of("3 Series", "A4"))
                .categories(List.of("Sedan"))
                .colors(List.of("Black"))
                .years(List.of(2022, 2023))
                .minPrice(new BigDecimal("30000"))
                .maxPrice(new BigDecimal("100000"))
                .build();

        when(carService.getAvailableFilters(any())).thenReturn(filtersDto);

        mockMvc.perform(get("/cars/filters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brands[0]").value("BMW"))
                .andExpect(jsonPath("$.brands[1]").value("Audi"));
    }


    @Test
    @DisplayName("GET /cars/options – returns 200 with manager options")
    void getManagerOptions_returns200() throws Exception {
        CarManagerOptionsDto options = new CarManagerOptionsDto(List.of(), List.of(), List.of(), List.of());
        when(carService.getManagerOptions()).thenReturn(options);

        mockMvc.perform(get("/cars/options"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("DELETE /cars/delete/{id} – returns 200 and confirmation message")
    void deleteCar_returns200() throws Exception {
        doNothing().when(carService).deleteCar(1L);

        mockMvc.perform(delete("/cars/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));
    }


    @Test
    @DisplayName("PUT /cars/{id} – returns 200 with updated car details")
    void updateCar_returns200() throws Exception {
        CarCreateDto updateDto = new CarCreateDto(1L, 2L, 3L, 2024L,
                new BigDecimal("55000"), "Updated desc", true, null, null);
        Car updatedCar = new Car();
        updatedCar.setId(1L);
        CarDetailsDto detailsDto = new CarDetailsDto(1L, "BMW", "3 Series", "Sedan",
                "Black", 2024L, new BigDecimal("55000"), "Updated desc", true, List.of(), null);

        when(carService.updateCar(eq(1L), any(CarCreateDto.class))).thenReturn(updatedCar);
        when(carMapper.toDetailsDto(updatedCar)).thenReturn(detailsDto);

        mockMvc.perform(put("/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.price").value(55000));
    }
}
