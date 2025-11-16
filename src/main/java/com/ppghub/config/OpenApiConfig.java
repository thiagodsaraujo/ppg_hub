package com.ppghub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para documentação da API.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("PPG Hub API")
                .version("0.1.0")
                .description("""
                    API REST para gestão de programas de pós-graduação com integração OpenAlex.

                    ## Funcionalidades
                    - Gestão de instituições, programas e docentes
                    - Integração com OpenAlex para dados bibliométricos
                    - Importação e sincronização de publicações
                    - Métricas e analytics

                    ## OpenAlex
                    Sistema integrado com a API do OpenAlex para enriquecimento de dados acadêmicos.
                    """)
                .contact(new Contact()
                    .name("PPG Hub Team")
                    .email("contato@ppghub.com")
                    .url("https://github.com/ppghub"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080" + contextPath)
                    .description("Servidor de Desenvolvimento")
            ));
    }
}
