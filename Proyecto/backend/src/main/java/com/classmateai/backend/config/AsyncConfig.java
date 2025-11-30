package com.classmateai.backend.config;

<<<<<<< HEAD
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
=======
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
>>>>>>> b1c20e0c2f38419b1e4d501ef49ed331f4c02454

@Configuration
@EnableAsync
public class AsyncConfig {
<<<<<<< HEAD

    @Bean(name = "audioProcessingExecutor")
    public Executor audioProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AudioProcessing-");
        executor.initialize();
        return executor;
    }
}
=======
    // Esta clase habilita el procesamiento asíncrono.
    // Al estar separada, @DataJpaTest puede ignorarla o manejarla mejor,
    // evitando el error de carga de contexto.
}
>>>>>>> b1c20e0c2f38419b1e4d501ef49ed331f4c02454
