package com.critiquehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CritiqueHubApplication {

    protected CritiqueHubApplication() { }

    public static void main(final String[] args) {
        SpringApplication.run(CritiqueHubApplication.class, args);
    }
}
