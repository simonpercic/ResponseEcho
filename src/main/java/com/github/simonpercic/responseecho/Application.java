package com.github.simonpercic.responseecho;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@SpringBootApplication
public class Application {

    @Bean
    public Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
