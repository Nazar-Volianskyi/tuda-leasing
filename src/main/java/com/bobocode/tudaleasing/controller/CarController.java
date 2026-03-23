package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.CarCreateDto;
import com.bobocode.tudaleasing.entity.Car;
import com.bobocode.tudaleasing.dto.CarCatalogDto;
import com.bobocode.tudaleasing.dto.CarDetailsDto;
import com.bobocode.tudaleasing.dto.CarFilterRequest;
import com.bobocode.tudaleasing.dto.CarFiltersDto;
import com.bobocode.tudaleasing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    @PostMapping
    public ResponseEntity<String> createCar(@RequestBody CarCreateDto dto) {

    @GetMapping
    public Page<CarCatalogDto> getCars(CarFilterRequest filter,
                                       Pageable pageable) {
        return carService.getCars(filter, pageable);
    }
        Car savedCar = carService.addCar(dto);

    @GetMapping("/{id}")
    public CarDetailsDto getCar(@PathVariable Long id) {
        return carService.getCarById(id);
        return ResponseEntity.ok("Car is successfully added with : " + savedCar.getId());
    }

    @GetMapping("/filters")
    public CarFiltersDto getFilters(CarFilterRequest filter) {
        return carService.getAvailableFilters(filter);
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok("Car with ID: " + id + " has been deleted.");
    }
}