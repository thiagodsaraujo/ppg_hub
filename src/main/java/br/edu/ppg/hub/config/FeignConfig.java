package br.edu.ppg.hub.config;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuração do Feign Client para integração com APIs externas.
 *
 * Define timeouts, logging e tratamento de erros para chamadas HTTP externas.
 *
 * @author PPG Hub
 * @since 1.0
 */
@Configuration
@Slf4j
public class FeignConfig {

    /**
     * Configura timeout de 10 segundos para conexão e leitura
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            10, TimeUnit.SECONDS,  // Connection timeout
            10, TimeUnit.SECONDS,  // Read timeout
            true                    // Follow redirects
        );
    }

    /**
     * Configura logging FULL para requests do Feign
     * Permite ver todas as requisições e respostas
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Decoder customizado para tratamento de erros das APIs externas
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            log.error("Erro na chamada Feign [{}]: Status {} - {}",
                methodKey,
                response.status(),
                response.reason());

            return switch (response.status()) {
                case 400 -> new IllegalArgumentException(
                    "Requisição inválida para API externa: " + response.reason()
                );
                case 404 -> new IllegalArgumentException(
                    "Recurso não encontrado na API externa: " + response.reason()
                );
                case 429 -> new RuntimeException(
                    "Limite de requisições excedido na API externa. Tente novamente mais tarde."
                );
                case 500, 502, 503, 504 -> new RuntimeException(
                    "Erro no servidor da API externa. Tente novamente mais tarde."
                );
                default -> new RuntimeException(
                    "Erro ao chamar API externa: " + response.status() + " - " + response.reason()
                );
            };
        };
    }
}
