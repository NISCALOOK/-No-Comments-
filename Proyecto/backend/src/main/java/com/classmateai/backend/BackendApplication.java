package com.classmateai.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// En tu clase principal con @SpringBootApplication
@SpringBootApplication
public class TuApplication {
    public static void main(String[] args) {
        SpringApplication.run(TuApplication.class, args);
    }

    @Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontendUrl, "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
}