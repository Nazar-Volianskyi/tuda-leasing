package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.CarCatalogDto;
import com.bobocode.tudaleasing.dto.CarDetailsDto;
import com.bobocode.tudaleasing.dto.CarFilterRequest;
import com.bobocode.tudaleasing.dto.CarFiltersDto;
import com.bobocode.tudaleasing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public Page<CarCatalogDto> getCars(CarFilterRequest filter,
                                       Pageable pageable) {
        return carService.getCars(filter, pageable);
    }

    @GetMapping("/{id}")
    public CarDetailsDto getCar(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @GetMapping("/filters")
    public CarFiltersDto getFilters(CarFilterRequest filter) {
        return carService.getAvailableFilters(filter);
    }
}