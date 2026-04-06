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
    public ResponseEntity<Page<CarCatalogDto>> getCars(CarFilterRequest filter,
                                                       Pageable pageable) {
        return ResponseEntity.ok(carService.getCars(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDetailsDto> getCar(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @GetMapping("/filters")
    public ResponseEntity<CarFiltersDto> getFilters(CarFilterRequest filter) {
        return ResponseEntity.ok(carService.getAvailableFilters(filter));
    }
}
