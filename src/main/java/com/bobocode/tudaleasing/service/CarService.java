package com.bobocode.tudaleasing.service;


import com.bobocode.tudaleasing.dto.CarCatalogDto;
import com.bobocode.tudaleasing.dto.CarDetailsDto;
import com.bobocode.tudaleasing.dto.CarFilterRequest;
import com.bobocode.tudaleasing.dto.CarFiltersDto;
import com.bobocode.tudaleasing.entity.Car;
import com.bobocode.tudaleasing.mapper.CarMapper;
import com.bobocode.tudaleasing.repository.CarRepository;
import com.bobocode.tudaleasing.repository.spec.CarSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

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
}}