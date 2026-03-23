package com.bobocode.tudaleasing.service;


import com.bobocode.tudaleasing.dto.*;
import com.bobocode.tudaleasing.mapper.CarMapper;
import com.bobocode.tudaleasing.repository.spec.CarSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bobocode.tudaleasing.entity.*;
import com.bobocode.tudaleasing.repository.*;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Car addCar(CarCreateDto dto) {
        Model model = modelRepository.findById(dto.modelId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found"));
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Color color = colorRepository.findById(dto.colorId())
                .orElseThrow(() -> new IllegalArgumentException("Color not found"));

        Car car = new Car();
        car.setModel(model);
        car.setCategory(category);
        car.setColor(color);
        car.setYear(dto.year());
        car.setFullPrice(dto.fullPrice());
        car.setDescription(dto.description());
        car.setAvailable(dto.available() != null ? dto.available() : true);

        if (dto.specs() != null) {
            CarSpec specs = new CarSpec();
            specs.setBodyType(dto.specs().bodyType());
            specs.setDoors(dto.specs().doors());
            specs.setSeats(dto.specs().seats());
            specs.setEngineCapacity(dto.specs().engineCapacity());
            specs.setEnginePower(dto.specs().enginePower());
            specs.setFuelType(dto.specs().fuelType());
            specs.setGearbox(dto.specs().gearbox());
            specs.setDriveType(dto.specs().driveType());
            specs.setMaxSpeed(dto.specs().maxSpeed());
            specs.setAcceleration0100(dto.specs().acceleration0100());
            specs.setFuelTankCapacity(dto.specs().fuelTankCapacity());
            specs.setTrunkCapacity(dto.specs().trunkCapacity());
            specs.setWeight(dto.specs().weight());
            specs.setCar(car);
            car.setSpecs(specs);
        }

        if(dto.images() != null && !dto.images().isEmpty()) {
            List<CarImage> images = dto.images().stream().map(carImageDto -> {
                CarImage carImage = new CarImage();
                carImage.setImageUrl(carImageDto.imageUrl());
                carImage.setIsMain(carImageDto.isMain());

                carImage.setCar(car);
                return carImage;
            }).collect(Collectors.toList());

            car.setImages(images);
        }


        return carRepository.save(car);

    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Car with ID - " + id +" not found"));
        carRepository.delete(car);
    }

    public Page<CarCatalogDto> getCars(CarFilterRequest filter, Pageable pageable) {
        if (filter == null) {
            filter = new CarFilterRequest();
        }
        var spec = CarSpecification.build(filter);
        return carRepository.findAll(spec, pageable)
                .map(carMapper::toCatalogDto);
    }

    public CarDetailsDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        return carMapper.toDetailsDto(car);
    }

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
