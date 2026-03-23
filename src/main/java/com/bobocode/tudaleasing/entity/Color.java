package com.bobocode.tudaleasing.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colors")
@Getter
@Setter
@NoArgsConstructor
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "hex_code", length = 10)
    private String hexCode;

    @OneToMany(mappedBy = "color")
    @Setter(AccessLevel.PRIVATE)
    private List<Car> cars = new ArrayList<>();
}
