package com.critiquehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the CritiqueHub Spring Boot application.
 */
@SpringBootApplication
public class CritiqueHubApplication {

    /**
     * Default constructor.
     * We keep this protected or private without an exception
     * so Spring can still instantiate the context.
     */
    protected CritiqueHubApplication() { }

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(CritiqueHubApplication.class, args);
    }
}
