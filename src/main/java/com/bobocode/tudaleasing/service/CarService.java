package com.bobocode.tudaleasing.service;


import com.bobocode.tudaleasing.dto.*;
import com.bobocode.tudaleasing.mapper.CarMapper;
import com.bobocode.tudaleasing.repository.spec.CarSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bobocode.tudaleasing.entity.*;
import com.bobocode.tudaleasing.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final ModelRepository modelRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    private final CarMapper carMapper;
    private final ImageStorageService imageStorageService;

    @Transactional
    public Car addCar(CarCreateDto dto, List<MultipartFile> files) {
        Car car = carMapper.toEntity(dto);

        if (files != null && !files.isEmpty()) {
            List<CarImage> images = files.stream()
                    .map(file -> {
                        String url = imageStorageService.uploadImage(file);
                        CarImage carImage = new CarImage();
                        carImage.setImageUrl(url);
                        carImage.setCar(car);
                        carImage.setIsMain(false);
                        return carImage;
                    })
                    .toList();

            if (!images.isEmpty()) images.get(0).setIsMain(true);
            car.setImages(images);
        }

        car.setModel(modelRepository.findById(dto.modelId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found")));
        car.setCategory(categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found")));
        car.setColor(colorRepository.findById(dto.colorId())
                .orElseThrow(() -> new IllegalArgumentException("Color not found")));

        if (car.getAvailable() == null) {
            car.setAvailable(true);
        }

        return carRepository.save(car);
    }

    @Transactional
    public Car updateCar(Long carId, CarCreateDto dto) {
        Car existingCar = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car with ID " + carId + " not found"));
        carMapper.updateEntity(dto, existingCar);
        if (dto.modelId() != null) {
            existingCar.setModel(modelRepository.findById(dto.modelId())
                    .orElseThrow(() -> new IllegalArgumentException("Model not found")));
        }
        if (dto.categoryId() != null) {
            existingCar.setCategory(categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found")));
        }
        if (dto.colorId() != null) {
            existingCar.setColor(colorRepository.findById(dto.colorId())
                    .orElseThrow(() -> new IllegalArgumentException("Color not found")));
        }
        if (dto.available() != null) {
            existingCar.setAvailable(dto.available());
        }
        return carRepository.save(existingCar);
    }

    @Transactional
    @CacheEvict(value = "car_details", key = "#id")
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Car with ID - " + id +" not found"));
        carRepository.delete(car);
    }

    @Transactional(readOnly = true)
    public Page<CarCatalogDto> getCars(CarFilterRequest filter, Pageable pageable) {
        if (filter == null) {
            filter = new CarFilterRequest();
        }
        var spec = CarSpecification.build(filter);
        return carRepository.findAll(spec, pageable)
                .map(carMapper::toCatalogDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "car_details", key = "#id")
    public CarDetailsDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        return carMapper.toDetailsDto(car);
    }
    @Cacheable(value = "filters", key = "#filter.brand != null ? #filter.brand : 'all'")
    @Transactional(readOnly = true)
    public CarFiltersDto getAvailableFilters(CarFilterRequest filter) {
        if (filter == null) filter = new CarFilterRequest();

        return CarFiltersDto.builder()
                .brands(carRepository.findDistinctBrands())
                .models(carRepository.findDistinctModels(filter.getBrand()))
                .categories(carRepository.findDistinctCategories())
                .colors(carRepository.findDistinctColors())
                .years(carRepository.findDistinctYears())
                .fuelTypes(carRepository.findDistinctFuelTypes())
                .gearboxes(carRepository.findDistinctGearboxes())
                .minPrice(carRepository.findMinPrice())
                .maxPrice(carRepository.findMaxPrice())

                .bodyTypes(carRepository.findDistinctBodyTypes())
                .doors(carRepository.findDistinctDoors())
                .seats(carRepository.findDistinctSeats())
                .engineCapacities(carRepository.findDistinctEngineCapacities())
                .enginePowers(carRepository.findDistinctEnginePowers())
                .driveTypes(carRepository.findDistinctDriveTypes())
                .maxSpeeds(carRepository.findDistinctMaxSpeeds())
                .build();
    }


}
