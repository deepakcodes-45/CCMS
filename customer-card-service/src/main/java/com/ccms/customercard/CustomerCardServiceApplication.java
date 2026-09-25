package com.ccms.customercard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomerCardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerCardServiceApplication.class, args);
        System.out.println("Customer card service started successfully");
    }
}