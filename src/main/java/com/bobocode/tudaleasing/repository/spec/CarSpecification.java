package com.bobocode.tudaleasing.repository.spec;

import com.bobocode.tudaleasing.dto.CarFilterRequest;
import com.bobocode.tudaleasing.entity.Car;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecification {

    public static Specification<Car> build(CarFilterRequest f) {
        return (root, query, cb) -> {

            query.distinct(true);

            var predicates = cb.conjunction();

            Join<?, ?> model = root.join("model");
            Join<?, ?> brand = model.join("brand");
            Join<?, ?> category = root.join("category");
            Join<?, ?> color = root.join("color");
            Join<?, ?> specs = root.join("specs");


            if (f.getBrand() != null) {
                predicates = cb.and(predicates,
                        cb.equal(brand.get("name"), f.getBrand()));
            }

            if (f.getModel() != null) {
                predicates = cb.and(predicates,
                        cb.equal(model.get("name"), f.getModel()));
            }

            if (f.getCategory() != null) {
                predicates = cb.and(predicates,
                        cb.equal(category.get("name"), f.getCategory()));
            }

            if (f.getColor() != null) {
                predicates = cb.and(predicates,
                        cb.equal(color.get("name"), f.getColor()));
            }

            if (f.getFuelType() != null) {
                predicates = cb.and(predicates,
                        cb.equal(specs.get("fuelType"), f.getFuelType()));
            }

            if (f.getGearbox() != null) {
                predicates = cb.and(predicates,
                        cb.equal(specs.get("gearbox"), f.getGearbox()));
            }

            if (f.getMinPrice() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("fullPrice"), f.getMinPrice()));
            }

            if (f.getMaxPrice() != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("fullPrice"), f.getMaxPrice()));
            }

            if (f.getYearFrom() != null) {
                predicates = cb.and(predicates,
                        cb.ge(root.get("year"), f.getYearFrom()));
            }

            if (f.getYearTo() != null) {
                predicates = cb.and(predicates,
                        cb.le(root.get("year"), f.getYearTo()));
            }


            if (f.getBodyType() != null) {
                predicates = cb.and(predicates,
                        cb.equal(specs.get("bodyType"), f.getBodyType()));
            }

            if (f.getDoors() != null) {
                predicates = cb.and(predicates,
                        cb.equal(specs.get("doors"), f.getDoors()));
            }

            if (f.getSeats() != null) {
                predicates = cb.and(predicates,
                        cb.equal(specs.get("seats"), f.getSeats()));
            }

            if (f.getEngineCapacityFrom() != null) {
                predicates = cb.and(predicates,
                        cb.ge(specs.get("engineCapacity"), f.getEngineCapacityFrom()));
            }

            if (f.getEngineCapacityTo() != null) {
                predicates = cb.and(predicates,
                        cb.le(specs.get("engineCapacity"), f.getEngineCapacityTo()));
            }

            if (f.getEnginePowerFrom() != null) {
                predicates = cb.and(predicates,
                        cb.ge(specs.get("enginePower"), f.getEnginePowerFrom()));
            }

            if (f.getEnginePowerTo() != null) {
                predicates = cb.and(predicates,
                        cb.le(specs.get("enginePower"), f.getEnginePowerTo()));
            }

            if (f.getDriveType() != null) {
                predicates = cb.and(predicates,
                        cb.equal(specs.get("driveType"), f.getDriveType()));
            }

            if (f.getMaxSpeedFrom() != null) {
                predicates = cb.and(predicates,
                        cb.ge(specs.get("maxSpeed"), f.getMaxSpeedFrom()));
            }

            if (f.getMaxSpeedTo() != null) {
                predicates = cb.and(predicates,
                        cb.le(specs.get("maxSpeed"), f.getMaxSpeedTo()));
            }

            return predicates;
        };
    }
}