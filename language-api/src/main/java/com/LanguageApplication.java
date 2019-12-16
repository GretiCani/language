package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"com.language"})
@EntityScan(basePackages = {"com.language"})
@ComponentScan(basePackages = {"com.language"})
public class LanguageApplication {

    public static void main(String[] args){
        SpringApplication.run(LanguageApplication.class);
    }
}
