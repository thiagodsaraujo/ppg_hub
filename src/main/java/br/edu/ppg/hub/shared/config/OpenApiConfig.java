package br.edu.ppg.hub.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI 3.0 (Swagger).
 *
 * Documenta a API REST com suporte a JWT.
 * Acesso: http://localhost:8000/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:PPG Hub}")
    private String applicationName;

    @Value("${server.port:8000}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        // Nome do esquema de segurança
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Informações da API
                .info(new Info()
                        .title("PPG Hub API")
                        .version("1.0.0")
                        .description("""
                                # PPG Hub - Sistema de Gestão para Programas de Pós-Graduação

                                API RESTful completa para gerenciar múltiplos programas de pós-graduação.

                                ## Funcionalidades Principais

                                - **CORE**: Instituições, Programas, Linhas de Pesquisa
                                - **AUTH**: Autenticação JWT, Usuários, Roles, Permissões
                                - **ACADEMIC**: Docentes, Discentes, Disciplinas, Matrículas, Bancas

                                ## Autenticação

                                Esta API usa **JWT (JSON Web Token)** para autenticação.

                                ### Como usar:

                                1. Faça login em `/api/v1/auth/login` com email e senha
                                2. Copie o token retornado
                                3. Clique no botão "Authorize" (cadeado) no topo
                                4. Digite: `Bearer {seu-token-aqui}`
                                5. Clique em "Authorize"

                                Agora você pode acessar endpoints protegidos!

                                ## Roles (Papéis)

                                - **SUPERADMIN**: Acesso total ao sistema
                                - **ADMIN_INSTITUCIONAL**: Administrador da instituição
                                - **COORDENADOR**: Coordenador de programa
                                - **SECRETARIA**: Secretaria acadêmica
                                - **DOCENTE**: Professor do programa
                                - **DISCENTE**: Estudante
                                - **TECNICO**: Técnico administrativo
                                - **VISITANTE**: Consulta pública

                                ## Suporte

                                - Email: admin@ppg.edu.br
                                - Documentação: /api/v1/docs
                                """)
                        .contact(new Contact()
                                .name("PPG Team")
                                .email("admin@ppg.edu.br")
                                .url("https://github.com/your-org/ppg-hub"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )

                // Servidores
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("http://localhost:" + serverPort + "/api/v1")
                                .description("API Base Path")
                ))

                // Esquema de segurança JWT
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT obtido no endpoint /auth/login")
                        )
                );
    }
}
