package br.edu.ppg.hub;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Classe principal da aplicação PPG Hub.
 *
 * Sistema de Gestão para Programas de Pós-Graduação
 *
 * @author PPG Team
 * @version 0.1.0
 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@Slf4j
@OpenAPIDefinition(
        info = @Info(
                title = "PPG Hub API",
                version = "0.1.0",
                description = "Sistema de Gestão para Programas de Pós-Graduação",
                contact = @Contact(
                        name = "PPG Team",
                        email = "admin@ppg.edu.br"
                ),
                license = @License(
                        name = "MIT",
                        url = "https://opensource.org/licenses/MIT"
                )
        )
)
public class PpgHubApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context = SpringApplication.run(PpgHubApplication.class, args);
            logApplicationStartup(context.getEnvironment());
        } catch (Exception e) {
            log.error("Erro ao iniciar a aplicação", e);
            System.exit(1);
        }
    }

    /**
     * Loga informações de inicialização da aplicação
     */
    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }

        String serverPort = env.getProperty("server.port", "8000");
        String contextPath = env.getProperty("server.servlet.context-path", "/");

        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Não foi possível determinar o endereço IP do host", e);
        }

        log.info("""

                ----------------------------------------------------------
                Aplicação '{}' está rodando!
                Versão: {}
                Perfil(s): {}
                Acesso Local:    {}://localhost:{}{}
                Acesso Externo:  {}://{}:{}{}
                Swagger UI:      {}://localhost:{}/swagger-ui.html
                API Docs:        {}://localhost:{}/api-docs
                ----------------------------------------------------------
                """,
                env.getProperty("spring.application.name", "PPG Hub"),
                env.getProperty("ppg.app.version", "0.1.0"),
                String.join(", ", env.getActiveProfiles().length == 0 ?
                        new String[]{"default"} : env.getActiveProfiles()),
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath,
                protocol,
                serverPort,
                protocol,
                serverPort
        );
    }
}
