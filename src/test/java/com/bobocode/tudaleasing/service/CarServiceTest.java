package com.bobocode.tudaleasing.service;

import com.bobocode.tudaleasing.dto.*;
import com.bobocode.tudaleasing.entity.*;
import com.bobocode.tudaleasing.mapper.CarMapper;
import com.bobocode.tudaleasing.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarService – unit tests")
class CarServiceTest {

    @Mock CarRepository carRepository;
    @Mock ModelRepository modelRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock ColorRepository colorRepository;
    @Mock CarMapper carMapper;
    @Mock ImageStorageService imageStorageService;

    @InjectMocks CarService carService;

    private Car sampleCar;
    private CarCreateDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleCar = new Car();
        sampleCar.setId(1L);
        sampleCar.setFullPrice(new BigDecimal("50000"));
        sampleCar.setAvailable(true);

        sampleDto = new CarCreateDto(1L, 2L, 3L, 2023L,
                new BigDecimal("50000"), "Sample car", true, null, null);
    }


    @Test
    @DisplayName("addCar: saves car and returns saved entity")
    void addCar_savesAndReturnsCar() {
        when(carMapper.toEntity(sampleDto)).thenReturn(sampleCar);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(new Model()));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(new Category()));
        when(colorRepository.findById(3L)).thenReturn(Optional.of(new Color()));
        when(carRepository.save(sampleCar)).thenReturn(sampleCar);

        Car result = carService.addCar(sampleDto, List.of());

        assertThat(result).isEqualTo(sampleCar);
        verify(carRepository).save(sampleCar);
    }

    @Test
    @DisplayName("addCar: sets available=true when dto.available is null")
    void addCar_setsAvailableTrue_whenNull() {
        CarCreateDto dtoWithNullAvailable = new CarCreateDto(1L, 2L, 3L, 2022L,
                new BigDecimal("40000"), "desc", null, null, null);
        Car carWithNullAvailable = new Car();
        carWithNullAvailable.setAvailable(null);

        when(carMapper.toEntity(dtoWithNullAvailable)).thenReturn(carWithNullAvailable);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(new Model()));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(new Category()));
        when(colorRepository.findById(3L)).thenReturn(Optional.of(new Color()));
        when(carRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Car result = carService.addCar(dtoWithNullAvailable, List.of());

        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("addCar: uploads images and marks first as main")
    void addCar_uploadsImages_firstIsMain() {
        org.springframework.mock.web.MockMultipartFile file1 =
                new org.springframework.mock.web.MockMultipartFile("file1", "img1.jpg", "image/jpeg", new byte[1]);
        org.springframework.mock.web.MockMultipartFile file2 =
                new org.springframework.mock.web.MockMultipartFile("file2", "img2.jpg", "image/jpeg", new byte[1]);

        when(carMapper.toEntity(sampleDto)).thenReturn(sampleCar);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(new Model()));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(new Category()));
        when(colorRepository.findById(3L)).thenReturn(Optional.of(new Color()));
        when(imageStorageService.uploadImage(file1)).thenReturn("http://cdn/img1.jpg");
        when(imageStorageService.uploadImage(file2)).thenReturn("http://cdn/img2.jpg");
        when(carRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Car result = carService.addCar(sampleDto, List.of(file1, file2));

        assertThat(result.getImages()).hasSize(2);
        assertThat(result.getImages().get(0).getIsMain()).isTrue();
        assertThat(result.getImages().get(1).getIsMain()).isFalse();
    }

    @Test
    @DisplayName("addCar: throws when model not found")
    void addCar_throwsWhenModelNotFound() {
        when(carMapper.toEntity(sampleDto)).thenReturn(sampleCar);
        when(modelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.addCar(sampleDto, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Model not found");
    }

    @Test
    @DisplayName("addCar: throws when category not found")
    void addCar_throwsWhenCategoryNotFound() {
        when(carMapper.toEntity(sampleDto)).thenReturn(sampleCar);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(new Model()));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.addCar(sampleDto, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found");
    }


    @Test
    @DisplayName("deleteCar: deletes car when found")
    void deleteCar_deletesWhenFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(sampleCar));

        carService.deleteCar(1L);

        verify(carRepository).delete(sampleCar);
    }

    @Test
    @DisplayName("deleteCar: throws when car not found")
    void deleteCar_throwsWhenNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.deleteCar(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }


    @Test
    @DisplayName("getCarById: returns DTO when car found")
    void getCarById_returnsDtoWhenFound() {
        CarDetailsDto dto = new CarDetailsDto(1L, "BMW", "3 Series", "Sedan",
                "Black", 2023L, new BigDecimal("50000"), "desc", true, List.of(), null);

        when(carRepository.findById(1L)).thenReturn(Optional.of(sampleCar));
        when(carMapper.toDetailsDto(sampleCar)).thenReturn(dto);

        CarDetailsDto result = carService.getCarById(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("getCarById: throws RuntimeException when car not found")
    void getCarById_throwsWhenNotFound() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.getCarById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }


    @Test
    @DisplayName("updateCar: updates and saves car")
    void updateCar_updatesAndSaves() {
        CarCreateDto updateDto = new CarCreateDto(1L, 2L, 3L, 2024L,
                new BigDecimal("55000"), "Updated desc", false, null, null);

        when(carRepository.findById(1L)).thenReturn(Optional.of(sampleCar));
        when(modelRepository.findById(1L)).thenReturn(Optional.of(new Model()));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(new Category()));
        when(colorRepository.findById(3L)).thenReturn(Optional.of(new Color()));
        when(carRepository.save(sampleCar)).thenReturn(sampleCar);

        Car result = carService.updateCar(1L, updateDto);

        assertThat(result).isEqualTo(sampleCar);
        verify(carRepository).save(sampleCar);
    }

    @Test
    @DisplayName("updateCar: throws when car not found")
    void updateCar_throwsWhenNotFound() {
        when(carRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.updateCar(100L, sampleDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("100");
    }


    @Test
    @DisplayName("getCars: returns page of catalog DTOs")
    @SuppressWarnings("unchecked")
    void getCars_returnsPage() {
        CarCatalogDto catalogDto = new CarCatalogDto(1L, "BMW", "3 Series",
                "Sedan", "Black", 2023L, new BigDecimal("50000"), "Petrol", "Auto", null);
        Page<Car>        carPage    = new PageImpl<>(List.of(sampleCar));
        CarFilterRequest filter     = new CarFilterRequest();
        Pageable         pageable   = PageRequest.of(0, 10);

        when(carRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(carPage);
        when(carMapper.toCatalogDto(sampleCar)).thenReturn(catalogDto);

        Page<CarCatalogDto> result = carService.getCars(filter, pageable);

        assertThat(result.getContent()).containsExactly(catalogDto);
    }

    @Test
    @DisplayName("getCars: handles null filter gracefully")
    @SuppressWarnings("unchecked")
    void getCars_handlesNullFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        assertThatCode(() -> carService.getCars(null, pageable))
                .doesNotThrowAnyException();
    }
}

