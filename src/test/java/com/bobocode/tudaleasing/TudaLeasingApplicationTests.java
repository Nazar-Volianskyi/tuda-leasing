package com.bobocode.tudaleasing;

import com.bobocode.tudaleasing.service.ImageStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class TudaLeasingApplicationTests {

    @MockitoBean
    ImageStorageService imageStorageService;

    @Test
    @DisplayName("Application context loads without errors")
    void contextLoads() {
    }
}
