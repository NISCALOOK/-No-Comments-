package com.classmateai.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    // --- DEJA ESTA CLASE TAL COMO ESTABA AL PRINCIPIO ---
    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Value("${frontend.url:http://localhost:3000}")
        private String frontendUrl;

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins(frontendUrl, "http://localhost:5173", "http://127.0.0.1:5173") // <-- Añadí 127.0.0.1 por si acaso
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
        }
    }
}