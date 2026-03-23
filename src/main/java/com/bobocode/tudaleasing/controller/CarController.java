package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.CarCreateDto;
import com.bobocode.tudaleasing.entity.Car;
import com.bobocode.tudaleasing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    @PostMapping
    public ResponseEntity<String> createCar(@RequestBody CarCreateDto dto) {

        Car savedCar = carService.addCar(dto);

        return ResponseEntity.ok("Car is successfully added with : " + savedCar.getId());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok("Car with ID: " + id + " has been deleted.");
    }
}
