package com.bobocode.tudaleasing.repository;

import com.bobocode.tudaleasing.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
