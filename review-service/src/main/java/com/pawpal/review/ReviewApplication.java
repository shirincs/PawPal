package com.pawpal.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.pawpal.review.rest", "com.pawpal.review.service", "com.pawpal.review.client"})
@EnableJpaRepositories(basePackages = "com.pawpal.review.repository")
@EntityScan(basePackages = "com.pawpal.review.model")
public class ReviewApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewApplication.class, args);
    }
}
