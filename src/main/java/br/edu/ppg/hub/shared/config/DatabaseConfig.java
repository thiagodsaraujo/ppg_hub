package br.edu.ppg.hub.shared.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Configuração do banco de dados.
 *
 * Configura:
 * - HikariCP (connection pool)
 * - JPA repositories
 * - JPA auditing (@CreatedDate, @LastModifiedDate)
 * - Transaction management
 */
@Configuration
@EnableJpaRepositories(basePackages = {
        "br.edu.ppg.hub.core.infrastructure.repository",
        "br.edu.ppg.hub.auth.infrastructure.repository",
        "br.edu.ppg.hub.academic.infrastructure.repository"
})
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.idle-timeout:600000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1800000}")
    private long maxLifetime;

    /**
     * Configura o DataSource com HikariCP.
     * HikariCP é o connection pool mais rápido disponível.
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // Conexão
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        // Pool settings
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);

        // Performance settings
        config.setAutoCommit(true);
        config.setConnectionTestQuery("SELECT 1");

        // Pool name
        config.setPoolName("PPG-HikariPool");

        // Leak detection (útil em desenvolvimento)
        config.setLeakDetectionThreshold(60000); // 60 segundos

        // Propriedades do PostgreSQL
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(config);
    }
}
