package com.bobocode.tudaleasing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
@Getter @Setter
@NoArgsConstructor
public class Car {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    @Column(name = "year")
    private Long year;

    @Column(name = "full_price", precision = 10, scale = 2)
    private BigDecimal fullPrice;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "available")
    private Boolean available = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<CarImage> images = new ArrayList<>();

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    private CarSpec specs;
}