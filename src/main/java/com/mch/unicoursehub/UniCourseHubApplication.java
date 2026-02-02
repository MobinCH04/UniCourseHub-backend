package com.mch.unicoursehub;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * The main entry point for the UniCourseHub Spring Boot application.
 * <p>
 * This class is responsible for bootstrapping the application context
 * and performing initial setup tasks, such as setting the default time zone.
 */
@SpringBootApplication
public class UniCourseHubApplication {

    /**
     * The main method used to start the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(UniCourseHubApplication.class, args);
    }

    /**
     * Initializes application-level settings after the Spring context is constructed.
     * <p>
     * Specifically, this method sets the default time zone to "Asia/Tehran"
     * to ensure consistent date and time handling throughout the application.
     */
    @PostConstruct
    public void init() {
        // Set the default time zone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tehran"));
    }

}
