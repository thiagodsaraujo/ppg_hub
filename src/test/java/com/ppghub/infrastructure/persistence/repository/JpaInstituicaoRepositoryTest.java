package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes de integração para JpaInstituicaoRepository.
 * Testa queries customizadas e derived queries contra PostgreSQL real via TestContainers.
 */
@DisplayName("JpaInstituicaoRepository Integration Tests")
class JpaInstituicaoRepositoryTest extends RepositoryTestBase {

    @Autowired
    private JpaInstituicaoRepository repository;

    private InstituicaoEntity unifesp;
    private InstituicaoEntity usp;
    private InstituicaoEntity pucSp;

    @BeforeEach
    void setUp() {
        // Limpar dados existentes
        repository.deleteAll();

        // Preparar dados de teste
        unifesp = InstituicaoEntity.builder()
                .nome("Universidade Federal de São Paulo")
                .sigla("UNIFESP")
                .cnpj("60428831000155")
                .tipo("PUBLICA")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .ativo(true)
                .openalexInstitutionId("I123456789")
                .rorId("https://ror.org/04wffgt70")
                .openalexWorksCount(15000)
                .openalexLastSyncAt(LocalDateTime.now().minusDays(5))
                .build();

        usp = InstituicaoEntity.builder()
                .nome("Universidade de São Paulo")
                .sigla("USP")
                .cnpj("63025530000104")
                .tipo("PUBLICA")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .ativo(true)
                .openalexInstitutionId("I987654321")
                .rorId("https://ror.org/036rp1748")
                .openalexWorksCount(50000)
                .openalexLastSyncAt(LocalDateTime.now().minusDays(2))
                .build();

        pucSp = InstituicaoEntity.builder()
                .nome("Pontifícia Universidade Católica de São Paulo")
                .sigla("PUC-SP")
                .cnpj("60897557000198")
                .tipo("PRIVADA")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .ativo(true)
                .openalexInstitutionId(null) // Sem OpenAlex ID
                .rorId(null)
                .openalexWorksCount(0)
                .build();

        // Salvar entidades
        repository.saveAll(List.of(unifesp, usp, pucSp));
        repository.flush();
    }

    @Nested
    @DisplayName("findBySigla Tests")
    class FindBySiglaTests {

        @Test
        @DisplayName("Deve encontrar instituição por sigla")
        void shouldFindBySigla() {
            // Act
            Optional<InstituicaoEntity> result = repository.findBySigla("UNIFESP");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getNome()).isEqualTo("Universidade Federal de São Paulo");
            assertThat(result.get().getSigla()).isEqualTo("UNIFESP");
        }

        @Test
        @DisplayName("Deve retornar vazio quando sigla não existe")
        void shouldReturnEmptyWhenSiglaNotExists() {
            // Act
            Optional<InstituicaoEntity> result = repository.findBySigla("INEXISTENTE");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCnpj Tests")
    class FindByCnpjTests {

        @Test
        @DisplayName("Deve encontrar instituição por CNPJ")
        void shouldFindByCnpj() {
            // Act
            Optional<InstituicaoEntity> result = repository.findByCnpj("60428831000155");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getSigla()).isEqualTo("UNIFESP");
        }

        @Test
        @DisplayName("Deve retornar vazio quando CNPJ não existe")
        void shouldReturnEmptyWhenCnpjNotExists() {
            // Act
            Optional<InstituicaoEntity> result = repository.findByCnpj("00000000000000");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByOpenalexInstitutionId Tests")
    class FindByOpenalexInstitutionIdTests {

        @Test
        @DisplayName("Deve encontrar instituição por OpenAlex ID")
        void shouldFindByOpenalexInstitutionId() {
            // Act
            Optional<InstituicaoEntity> result = repository.findByOpenalexInstitutionId("I123456789");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getSigla()).isEqualTo("UNIFESP");
        }
    }

    @Nested
    @DisplayName("findByRorId Tests")
    class FindByRorIdTests {

        @Test
        @DisplayName("Deve encontrar instituição por ROR ID")
        void shouldFindByRorId() {
            // Act
            Optional<InstituicaoEntity> result = repository.findByRorId("https://ror.org/04wffgt70");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getSigla()).isEqualTo("UNIFESP");
        }
    }

    @Nested
    @DisplayName("findByCidadeAndEstado Tests")
    class FindByCidadeAndEstadoTests {

        @Test
        @DisplayName("Deve encontrar todas as instituições de São Paulo/SP")
        void shouldFindByCidadeAndEstado() {
            // Act
            List<InstituicaoEntity> result = repository.findByCidadeAndEstado("São Paulo", "SP");

            // Assert
            assertThat(result).hasSize(3);
            assertThat(result).extracting(InstituicaoEntity::getSigla)
                    .containsExactlyInAnyOrder("UNIFESP", "USP", "PUC-SP");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há instituições na cidade/estado")
        void shouldReturnEmptyListWhenNoCityStateMatch() {
            // Act
            List<InstituicaoEntity> result = repository.findByCidadeAndEstado("Rio de Janeiro", "RJ");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByAtivoTrue Tests")
    class FindByAtivoTrueTests {

        @Test
        @DisplayName("Deve retornar apenas instituições ativas")
        void shouldReturnOnlyActiveInstitutions() {
            // Arrange - adicionar uma instituição inativa
            InstituicaoEntity inativa = InstituicaoEntity.builder()
                    .nome("Instituição Inativa")
                    .sigla("INATIVA")
                    .tipo("PUBLICA")
                    .cidade("São Paulo")
                    .estado("SP")
                    .ativo(false)
                    .build();
            repository.save(inativa);
            repository.flush();

            // Act
            List<InstituicaoEntity> result = repository.findByAtivoTrue();

            // Assert
            assertThat(result).hasSize(3);
            assertThat(result).allMatch(InstituicaoEntity::getAtivo);
            assertThat(result).extracting(InstituicaoEntity::getSigla)
                    .containsExactlyInAnyOrder("UNIFESP", "USP", "PUC-SP");
        }
    }

    @Nested
    @DisplayName("findByTipo Tests")
    class FindByTipoTests {

        @Test
        @DisplayName("Deve retornar instituições públicas")
        void shouldReturnPublicInstitutions() {
            // Act
            List<InstituicaoEntity> result = repository.findByTipo("PUBLICA");

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(InstituicaoEntity::getSigla)
                    .containsExactlyInAnyOrder("UNIFESP", "USP");
        }

        @Test
        @DisplayName("Deve retornar instituições privadas")
        void shouldReturnPrivateInstitutions() {
            // Act
            List<InstituicaoEntity> result = repository.findByTipo("PRIVADA");

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSigla()).isEqualTo("PUC-SP");
        }
    }

    @Nested
    @DisplayName("findInstituicoesNeedingSync Tests")
    class FindInstituicoesNeedingSyncTests {

        @Test
        @DisplayName("Deve retornar instituições que precisam de sincronização")
        void shouldReturnInstitutionsNeedingSync() {
            // Arrange - Threshold de 3 dias atrás
            LocalDateTime threshold = LocalDateTime.now().minusDays(3);

            // Act
            List<InstituicaoEntity> result = repository.findInstituicoesNeedingSync(threshold);

            // Assert
            // UNIFESP tem sync de 5 dias atrás (precisa sync)
            // USP tem sync de 2 dias atrás (não precisa sync)
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSigla()).isEqualTo("UNIFESP");
        }

        @Test
        @DisplayName("Deve incluir instituições que nunca foram sincronizadas com OpenAlex ID")
        void shouldIncludeNeverSyncedInstitutions() {
            // Arrange - Adicionar instituição com OpenAlex ID mas sem sync
            InstituicaoEntity neverSynced = InstituicaoEntity.builder()
                    .nome("Instituição Não Sincronizada")
                    .sigla("NEVER-SYNC")
                    .tipo("PUBLICA")
                    .cidade("Campinas")
                    .estado("SP")
                    .ativo(true)
                    .openalexInstitutionId("I999999999")
                    .openalexLastSyncAt(null)
                    .build();
            repository.save(neverSynced);
            repository.flush();

            LocalDateTime threshold = LocalDateTime.now().minusDays(1);

            // Act
            List<InstituicaoEntity> result = repository.findInstituicoesNeedingSync(threshold);

            // Assert
            assertThat(result).hasSizeGreaterThanOrEqualTo(1);
            assertThat(result).extracting(InstituicaoEntity::getSigla)
                    .contains("NEVER-SYNC");
        }
    }

    @Nested
    @DisplayName("findInstituicoesSemOpenAlexId Tests")
    class FindInstituicoesSemOpenAlexIdTests {

        @Test
        @DisplayName("Deve retornar apenas instituições ativas sem OpenAlex ID")
        void shouldReturnActiveInstitutionsWithoutOpenAlexId() {
            // Act
            List<InstituicaoEntity> result = repository.findInstituicoesSemOpenAlexId();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSigla()).isEqualTo("PUC-SP");
            assertThat(result.get(0).getOpenalexInstitutionId()).isNull();
            assertThat(result.get(0).getAtivo()).isTrue();
        }

        @Test
        @DisplayName("Não deve retornar instituições inativas sem OpenAlex ID")
        void shouldNotReturnInactiveInstitutions() {
            // Arrange - Adicionar instituição inativa sem OpenAlex ID
            InstituicaoEntity inativaSemOpenAlex = InstituicaoEntity.builder()
                    .nome("Instituição Inativa Sem OpenAlex")
                    .sigla("INATIVA-NO-OA")
                    .tipo("PRIVADA")
                    .cidade("São Paulo")
                    .estado("SP")
                    .ativo(false)
                    .openalexInstitutionId(null)
                    .build();
            repository.save(inativaSemOpenAlex);
            repository.flush();

            // Act
            List<InstituicaoEntity> result = repository.findInstituicoesSemOpenAlexId();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result).extracting(InstituicaoEntity::getSigla)
                    .doesNotContain("INATIVA-NO-OA");
        }
    }

    @Nested
    @DisplayName("searchByNome Tests")
    class SearchByNomeTests {

        @Test
        @DisplayName("Deve buscar por nome parcial (case insensitive)")
        void shouldSearchByPartialName() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<InstituicaoEntity> result = repository.searchByNome("federal", pageable);

            // Assert
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getSigla()).isEqualTo("UNIFESP");
        }

        @Test
        @DisplayName("Deve buscar ignorando maiúsculas/minúsculas")
        void shouldSearchCaseInsensitive() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<InstituicaoEntity> result1 = repository.searchByNome("UNIVERSIDADE", pageable);
            Page<InstituicaoEntity> result2 = repository.searchByNome("universidade", pageable);

            // Assert
            assertThat(result1.getContent()).hasSize(3);
            assertThat(result2.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não encontra nome")
        void shouldReturnEmptyPageWhenNoMatch() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<InstituicaoEntity> result = repository.searchByNome("Harvard", pageable);

            // Assert
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Deve respeitar paginação")
        void shouldRespectPagination() {
            // Act
            Pageable pageable = PageRequest.of(0, 2);
            Page<InstituicaoEntity> result = repository.searchByNome("São Paulo", pageable);

            // Assert
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("findTopByPublicacoes Tests")
    class FindTopByPublicacoesTests {

        @Test
        @DisplayName("Deve retornar instituições ordenadas por número de publicações")
        void shouldReturnInstitutionsOrderedByPublicationsCount() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<InstituicaoEntity> result = repository.findTopByPublicacoes(pageable);

            // Assert
            assertThat(result.getContent()).hasSize(2); // PUC-SP tem 0 publicações
            assertThat(result.getContent().get(0).getSigla()).isEqualTo("USP"); // 50000
            assertThat(result.getContent().get(1).getSigla()).isEqualTo("UNIFESP"); // 15000
        }

        @Test
        @DisplayName("Não deve incluir instituições sem publicações")
        void shouldNotIncludeInstitutionsWithoutPublications() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<InstituicaoEntity> result = repository.findTopByPublicacoes(pageable);

            // Assert
            assertThat(result.getContent()).allMatch(i -> i.getOpenalexWorksCount() > 0);
            assertThat(result.getContent()).extracting(InstituicaoEntity::getSigla)
                    .doesNotContain("PUC-SP");
        }
    }

    @Nested
    @DisplayName("countByEstado Tests")
    class CountByEstadoTests {

        @Test
        @DisplayName("Deve contar instituições por estado")
        void shouldCountInstitutionsByState() {
            // Arrange - Adicionar uma instituição no RJ
            InstituicaoEntity ufrj = InstituicaoEntity.builder()
                    .nome("Universidade Federal do Rio de Janeiro")
                    .sigla("UFRJ")
                    .tipo("PUBLICA")
                    .cidade("Rio de Janeiro")
                    .estado("RJ")
                    .ativo(true)
                    .build();
            repository.save(ufrj);
            repository.flush();

            // Act
            List<Object[]> result = repository.countByEstado();

            // Assert
            assertThat(result).hasSize(2);

            // Verificar SP
            Object[] sp = result.stream()
                    .filter(r -> "SP".equals(r[0]))
                    .findFirst()
                    .orElseThrow();
            assertThat(sp[0]).isEqualTo("SP");
            assertThat(sp[1]).isEqualTo(3L);

            // Verificar RJ
            Object[] rj = result.stream()
                    .filter(r -> "RJ".equals(r[0]))
                    .findFirst()
                    .orElseThrow();
            assertThat(rj[0]).isEqualTo("RJ");
            assertThat(rj[1]).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("existsByCnpj Tests")
    class ExistsByCnpjTests {

        @Test
        @DisplayName("Deve retornar true quando CNPJ existe")
        void shouldReturnTrueWhenCnpjExists() {
            // Act
            boolean result = repository.existsByCnpj("60428831000155");

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando CNPJ não existe")
        void shouldReturnFalseWhenCnpjNotExists() {
            // Act
            boolean result = repository.existsByCnpj("00000000000000");

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsBySigla Tests")
    class ExistsBySiglaTests {

        @Test
        @DisplayName("Deve retornar true quando sigla existe")
        void shouldReturnTrueWhenSiglaExists() {
            // Act
            boolean result = repository.existsBySigla("UNIFESP");

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando sigla não existe")
        void shouldReturnFalseWhenSiglaNotExists() {
            // Act
            boolean result = repository.existsBySigla("INEXISTENTE");

            // Assert
            assertThat(result).isFalse();
        }
    }
}
