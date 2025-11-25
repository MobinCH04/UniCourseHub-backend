package com.mch.unicoursehub;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class UniCourseHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniCourseHubApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Set the default time zone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tehran"));
    }

}
