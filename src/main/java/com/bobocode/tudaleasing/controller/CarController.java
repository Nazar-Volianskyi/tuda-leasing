package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.*;
import com.bobocode.tudaleasing.entity.Car;
import com.bobocode.tudaleasing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    @PostMapping
    public ResponseEntity<String> createCar(@RequestBody CarCreateDto dto) {

        Car savedCar = carService.addCar(dto);

        return ResponseEntity.ok("Car is successfully added with ID : " + savedCar.getId());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok("Car with ID: " + id + " has been deleted.");
    }

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
