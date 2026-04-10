package com.bobocode.tudaleasing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TudaLeasingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TudaLeasingApplication.class, args);
    }

}
