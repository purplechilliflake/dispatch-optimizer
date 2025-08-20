package com.freightfox.dispatchoptimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This annotation is crucial. It turns this class into a Spring Boot application launcher.
@SpringBootApplication
public class DispatchOptimizerApplication {

    // This is the main entry point for the Java application.
    // Execution starts here.
    public static void main(String[] args) {
        // This line tells Spring to start up, create the application context,
        // and run our application.
        SpringApplication.run(DispatchOptimizerApplication.class, args);
    }

}