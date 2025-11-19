package br.edu.ppg.hub.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuração de cache da aplicação usando Caffeine.
 *
 * Define políticas de expiração e tamanho máximo para diferentes caches.
 *
 * @author PPG Hub
 * @since 1.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configura o gerenciador de cache com Caffeine.
     *
     * Cache "openalex":
     * - Máximo de 1000 entradas
     * - Expiração após 7 dias sem acesso
     * - Usado para cachear dados da API OpenAlex
     *
     * @return CacheManager configurado
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("openalex");
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Configura o Caffeine cache builder com políticas de cache.
     *
     * @return Caffeine configurado
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(1000)                          // Máximo 1000 entradas
                .expireAfterWrite(7, TimeUnit.DAYS)         // Expira após 7 dias
                .recordStats();                              // Habilita estatísticas
    }
}
