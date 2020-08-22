package com.sarahu.packageappapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SnykPackageApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnykPackageApplication.class, args);
    }

}
