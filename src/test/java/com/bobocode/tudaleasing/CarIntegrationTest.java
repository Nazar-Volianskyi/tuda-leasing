package com.bobocode.tudaleasing;

import com.bobocode.tudaleasing.dto.*;
import com.bobocode.tudaleasing.entity.enums.Role;
import com.bobocode.tudaleasing.service.ImageStorageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integration tests – full application stack")
class CarIntegrationTest {

    @Autowired MockMvc       mockMvc;

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper =
            new com.fasterxml.jackson.databind.ObjectMapper();

    @MockitoBean ImageStorageService imageStorageService;

    private static String adminToken;

    private static Long createdCarId;

    @Test
    @Order(1)
    @DisplayName("POST /auth/login – default admin can log in and receive JWT")
    void login_defaultAdmin_receivesToken() throws Exception {
        AuthRequestDto credentials = new AuthRequestDto("admin@tudaleasing.pl", "Admin@1234");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("admin@tudaleasing.pl"))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        AuthResponseDto response = objectMapper.readValue(body, AuthResponseDto.class);
        adminToken = response.token();
        assertThat(adminToken).isNotBlank();
    }

    @Test
    @Order(2)
    @DisplayName("POST /auth/login – wrong password returns 401")
    void login_wrongPassword_returns401() throws Exception {
        AuthRequestDto credentials = new AuthRequestDto("admin@tudaleasing.pl", "WrongPass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @Order(3)
    @DisplayName("GET /cars – public endpoint returns 200 with (possibly empty) page")
    void getCars_publicEndpoint_returns200() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("GET /cars/filters – public endpoint returns 200")
    void getFilters_publicEndpoint_returns200() throws Exception {
        mockMvc.perform(get("/cars/filters"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("GET /cars/options – public endpoint returns 200")
    void getManagerOptions_publicEndpoint_returns200() throws Exception {
        mockMvc.perform(get("/cars/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brands").isArray())
                .andExpect(jsonPath("$.models").isArray())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.colors").isArray());
    }

    @Test
    @Order(6)
    @DisplayName("POST /cars – unauthenticated request returns 403")
    void createCar_unauthenticated_returns403() throws Exception {
        mockMvc.perform(multipart("/cars"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    @DisplayName("POST /cars – authenticated manager can create a car")
    void createCar_asAdmin_returns201() throws Exception {
        ensureAdminToken();
        when(imageStorageService.uploadImage(any()))
                .thenReturn("https://cdn.example.com/test-car.jpg");

        CarSpecDto specs = new CarSpecDto("SUV", 4L, 5L,
                new BigDecimal("2.0"), 150L, "Petrol", "Automatic",
                "AWD", 220, new BigDecimal("8.5"), 70L, 500L, 1800L);

        CarCreateDto carDto = new CarCreateDto(
                1L, 1L, 1L, 2023L,
                new BigDecimal("150000"), "Test integration car",
                true, specs, null);

        org.springframework.mock.web.MockMultipartFile carPart =
                new org.springframework.mock.web.MockMultipartFile(
                        "car", "", MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(carDto));
        org.springframework.mock.web.MockMultipartFile filePart =
                new org.springframework.mock.web.MockMultipartFile(
                        "files", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});

        MvcResult result = mockMvc.perform(multipart("/cars")
                        .file(carPart)
                        .file(filePart)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        String idStr = body.replaceAll("[^0-9]", "");
        createdCarId = Long.parseLong(idStr);
        assertThat(createdCarId).isPositive();
    }

    @Test
    @Order(8)
    @DisplayName("GET /cars/{id} – returns 200 with the newly created car")
    void getCarById_returnsCreatedCar() throws Exception {
        ensureCarCreated();

        mockMvc.perform(get("/cars/" + createdCarId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCarId))
                .andExpect(jsonPath("$.description").value("Test integration car"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @Order(9)
    @DisplayName("PUT /cars/{id} – authenticated manager can update a car")
    void updateCar_asAdmin_returns200() throws Exception {
        ensureAdminToken();
        ensureCarCreated();

        CarCreateDto updateDto = new CarCreateDto(
                1L, 1L, 1L, 2024L,
                new BigDecimal("160000"), "Updated integration car",
                false, null, null);

        mockMvc.perform(put("/cars/" + createdCarId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated integration car"))
                .andExpect(jsonPath("$.year").value(2024));
    }

    @Test
    @Order(10)
    @DisplayName("DELETE /cars/delete/{id} – unauthenticated request returns 403")
    void deleteCar_unauthenticated_returns403() throws Exception {
        ensureCarCreated();

        mockMvc.perform(delete("/cars/delete/" + createdCarId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    @DisplayName("DELETE /cars/delete/{id} – authenticated manager can delete a car")
    void deleteCar_asAdmin_returns200() throws Exception {
        ensureAdminToken();
        ensureCarCreated();

        mockMvc.perform(delete("/cars/delete/" + createdCarId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(createdCarId.toString())));
    }

    @Test
    @Order(12)
    @DisplayName("GET /cars/{id} – car not found after deletion throws exception")
    void getCarById_afterDelete_returns5xx() {
        ensureCarCreatedSync();

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                mockMvc.perform(get("/cars/" + createdCarId)).andReturn()
        ).hasMessageContaining("Car not found");
    }


    @Test
    @Order(13)
    @DisplayName("POST /api/calculator/leasing – public endpoint returns 200 with valid calculation")
    void calculateLeasing_publicEndpoint_returns200() throws Exception {
        LeasingCalculatorRequest request = new LeasingCalculatorRequest();
        request.setCarPrice(new BigDecimal("100000"));
        request.setPeriodMonths(48);
        request.setInitialFeePercent(20);
        request.setBuyoutPercent(10);

        mockMvc.perform(post("/api/calculator/leasing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPaymentNetto").isNumber())
                .andExpect(jsonPath("$.monthlyPaymentBrutto").isNumber())
                .andExpect(jsonPath("$.initialFeeAmount").value(20000))
                .andExpect(jsonPath("$.buyoutAmount").value(10000));
    }

    @Test
    @Order(14)
    @DisplayName("POST /api/calculator/credit – public endpoint returns 200 with valid calculation")
    void calculateCredit_publicEndpoint_returns200() throws Exception {
        LeasingCalculatorRequest request = new LeasingCalculatorRequest();
        request.setCarPrice(new BigDecimal("80000"));
        request.setPeriodMonths(60);
        request.setInitialFeePercent(15);
        request.setBuyoutPercent(0);

        mockMvc.perform(post("/api/calculator/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPaymentCredit").isNumber())
                .andExpect(jsonPath("$.rrso").value(12.18))
                .andExpect(jsonPath("$.initialFeeAmount").value(12000));
    }


    @Test
    @Order(15)
    @DisplayName("GET /admin/users – unauthenticated returns 403")
    void getUsers_unauthenticated_returns403() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(16)
    @DisplayName("GET /admin/users – authenticated ADMINISTRATOR returns 200 with user list")
    void getUsers_asAdmin_returns200() throws Exception {
        ensureAdminToken();

        mockMvc.perform(get("/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").isNotEmpty());
    }

    @Test
    @Order(17)
    @DisplayName("POST /admin/users – creates new manager user")
    void createUser_asAdmin_returns201() throws Exception {
        ensureAdminToken();

        CreateUserRequestDto request = new CreateUserRequestDto(
                "manager@test.com", "Manager@123", "Test", "Manager", Role.MANAGER);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("manager@test.com"))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    @Order(18)
    @DisplayName("POST /admin/users – duplicate email throws exception")
    void createUser_duplicateEmail_returns5xx() {
        ensureAdminTokenSync();

        CreateUserRequestDto duplicate = new CreateUserRequestDto(
                "manager@test.com", "AnyPass@1", "Another", "User", Role.MANAGER);

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                mockMvc.perform(post("/admin/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(duplicate))
                                .header("Authorization", "Bearer " + adminToken))
                        .andReturn()
        ).hasMessageContaining("manager@test.com");
    }


    private void ensureAdminToken() throws Exception {
        if (adminToken == null) {
            login_defaultAdmin_receivesToken();
        }
    }

    private void ensureAdminTokenSync() {
        if (adminToken == null) {
            try { login_defaultAdmin_receivesToken(); } catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    private void ensureCarCreated() throws Exception {
        if (createdCarId == null) {
            ensureAdminToken();
            createCar_asAdmin_returns201();
        }
    }

    private void ensureCarCreatedSync() {
        if (createdCarId == null) {
            try {
                ensureAdminToken();
                createCar_asAdmin_returns201();
            } catch (Exception e) { throw new RuntimeException(e); }
        }
    }
}

