package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.IntegrationTestBase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Classe base para testes de Repository com JPA.
 * Usa TestContainers PostgreSQL e desabilita o H2.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public abstract class RepositoryTestBase extends IntegrationTestBase {
}
