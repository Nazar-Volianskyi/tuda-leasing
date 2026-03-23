package com.bobocode.tudaleasing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "car_specs")
@Setter
@Getter
@NoArgsConstructor
public class CarSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "body_type", length = 50)
    private String bodyType;

    @Column(name = "doors")
    private Long doors;

    @Column(name = "seats")
    private Long seats;

    @Column(name = "engine_capacity", precision = 4, scale = 2)
    private BigDecimal engineCapacity;

    @Column(name = "engine_power")
    private Long enginePower;

    @Column(name = "fuel_type", length = 50)
    private String fuelType;

    @Column(length = 50)
    private String gearbox;

    @Column(name = "drive_type", length = 50)
    private String driveType;

    @Column(name = "max_speed")
    private Integer maxSpeed;

    @Column(name = "acceleration_0_100", precision = 4, scale = 1)
    private BigDecimal acceleration0100;

    @Column(name = "fuel_tank_capacity")
    private Long fuelTankCapacity;

    @Column(name = "trunk_capacity")
    private Long trunkCapacity;

    @Column(name = "weight")
    private Long weight;

}
