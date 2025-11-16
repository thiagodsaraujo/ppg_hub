package com.ppghub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicação principal do PPG Hub - Sistema de gestão de programas de pós-graduação
 * com integração OpenAlex para dados bibliométricos.
 *
 * @author PPG Hub Team
 * @version 0.1.0
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class PpgHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(PpgHubApplication.class, args);
    }
}
