package com.bobocode.tudaleasing;

import org.springframework.boot.SpringApplication;

public class TestTudaLeasingApplication {

    public static void main(String[] args) {
        SpringApplication.from(TudaLeasingApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
