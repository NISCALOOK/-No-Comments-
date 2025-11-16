package com.classmateai.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Esta clase habilita el procesamiento asíncrono.
    // Al estar separada, @DataJpaTest puede ignorarla o manejarla mejor,
    // evitando el error de carga de contexto.
}
