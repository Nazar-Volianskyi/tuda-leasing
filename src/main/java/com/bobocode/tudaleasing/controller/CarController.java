package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.*;
import com.bobocode.tudaleasing.entity.Car;
import com.bobocode.tudaleasing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bobocode.tudaleasing.mapper.CarMapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarMapper carMapper;
    private final CarService carService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createCar(
            @RequestPart("car") CarCreateDto dto,
            @RequestPart("files") List<MultipartFile> files) {

        Car savedCar = carService.addCar(dto, files);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Car is successfully added with ID : " + savedCar.getId());
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

    @PutMapping("/{id}")
    public ResponseEntity<CarDetailsDto> updateCar(
            @PathVariable Long id,
            @RequestBody CarCreateDto updateDto) {

        Car updatedCar = carService.updateCar(id, updateDto);

        return ResponseEntity.ok(carMapper.toDetailsDto(updatedCar));
    }
}
