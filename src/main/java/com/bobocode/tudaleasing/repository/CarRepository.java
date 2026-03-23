package com.bobocode.tudaleasing.repository;

import com.bobocode.tudaleasing.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    @EntityGraph(attributePaths = {
            "model.brand",
            "category",
            "color",
            "specs",
            "images"

    })
    Page<Car> findAll(Specification<Car> spec, Pageable pageable);



    @EntityGraph(attributePaths = {
            "model.brand",
            "category",
            "color",
            "specs",
            "images"
    })
    Optional<Car> findById(Long id);

    @Query("SELECT DISTINCT b.name FROM Brand b JOIN b.models m JOIN m.cars c")
    List<String> findDistinctBrands();

    @Query("""
    SELECT DISTINCT m.name FROM Model m JOIN m.brand b JOIN m.cars c
    WHERE (:brand IS NULL OR b.name = :brand)
    """)
    List<String> findDistinctModels(@Param("brand") String brand);

    @Query("SELECT DISTINCT cat.name FROM Category cat JOIN cat.cars c")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT col.name FROM Color col JOIN col.cars c")
    List<String> findDistinctColors();

    @Query("SELECT DISTINCT c.year FROM Car c")
    List<Integer> findDistinctYears();

    @Query("SELECT MIN(c.fullPrice) FROM Car c")
    BigDecimal findMinPrice();

    @Query("SELECT MAX(c.fullPrice) FROM Car c")
    BigDecimal findMaxPrice();

    @Query("SELECT DISTINCT s.fuelType FROM CarSpec s")
    List<String> findDistinctFuelTypes();

    @Query("SELECT DISTINCT s.gearbox FROM CarSpec s")
    List<String> findDistinctGearboxes();

    @Query("SELECT DISTINCT s.bodyType FROM CarSpec s")
    List<String> findDistinctBodyTypes();

    @Query("SELECT DISTINCT s.doors FROM CarSpec s")
    List<Integer> findDistinctDoors();

    @Query("SELECT DISTINCT s.seats FROM CarSpec s")
    List<Integer> findDistinctSeats();

    @Query("SELECT DISTINCT s.engineCapacity FROM CarSpec s")
    List<BigDecimal> findDistinctEngineCapacities();

    @Query("SELECT DISTINCT s.enginePower FROM CarSpec s")
    List<Integer> findDistinctEnginePowers();

    @Query("SELECT DISTINCT s.driveType FROM CarSpec s")
    List<String> findDistinctDriveTypes();

    @Query("SELECT DISTINCT s.maxSpeed FROM CarSpec s")
    List<Integer> findDistinctMaxSpeeds();
}
