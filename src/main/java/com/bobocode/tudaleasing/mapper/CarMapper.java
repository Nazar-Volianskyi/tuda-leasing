package com.bobocode.tudaleasing.mapper;


import com.bobocode.tudaleasing.dto.*;
import com.bobocode.tudaleasing.entity.Car;
import com.bobocode.tudaleasing.entity.CarImage;
import com.bobocode.tudaleasing.entity.CarSpec;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(source = "model.brand.name", target = "brand")
    @Mapping(source = "model.name", target = "model")
    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "color.name", target = "color")
    @Mapping(source = "fullPrice", target = "price")
    @Mapping(source = "specs.fuelType", target = "fuelType")
    @Mapping(source = "specs.gearbox", target = "gearbox")
    @Mapping(target = "imageUrl", expression = "java(getMainImage(car))")
    CarCatalogDto toCatalogDto(Car car);

    @Mapping(source = "model.brand.name", target = "brand")
    @Mapping(source = "model.name", target = "model")
    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "color.name", target = "color")
    @Mapping(source = "fullPrice", target = "price")
    @Mapping(target = "images", expression = "java(mapImages(car))")
    @Mapping(source = "specs", target = "specs")
    CarDetailsDto toDetailsDto(Car car);
    CarSpecDto toSpecDto(CarSpec spec);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "color", ignore = true)
    Car toEntity(CarCreateDto dto);

    CarSpec toSpecEntity(CarSpecDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "car", ignore = true)
    CarImage toImageEntity(CarImageDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "color", ignore = true)
    void updateEntity(CarCreateDto dto, @MappingTarget Car car);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "car", ignore = true)
    void updateSpecEntity(CarSpecDto dto, @MappingTarget CarSpec spec);

    @AfterMapping
    default void linkRelations(@MappingTarget Car car) {
        if (car.getSpecs() != null) {
            car.getSpecs().setCar(car);
        }
        if (car.getImages() != null) {
            car.getImages().forEach(img -> img.setCar(car));
        }
    }

    default List<String> mapImages(Car car) {
        if (car.getImages() == null) return List.of();

        return car.getImages().stream()
                .map(CarImage::getImageUrl)
                .toList();
    }
    default String getMainImage(Car car) {
        if (car.getImages() == null) return null;

        return car.getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsMain()))
                .map(CarImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }
}