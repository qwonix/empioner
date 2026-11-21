package ru.qwonix.empioner.telegram.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmpionerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpionerServiceApplication.class, args);
    }

}
