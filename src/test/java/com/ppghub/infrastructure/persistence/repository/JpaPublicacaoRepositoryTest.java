package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.AutoriaEntity;
import com.ppghub.infrastructure.persistence.entity.DocenteEntity;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.entity.PublicacaoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes de integração para JpaPublicacaoRepository.
 * Testa queries customizadas incluindo joins complexos com Docente e Instituição.
 */
@DisplayName("JpaPublicacaoRepository Integration Tests")
class JpaPublicacaoRepositoryTest extends RepositoryTestBase {

    @Autowired
    private JpaPublicacaoRepository publicacaoRepository;

    @Autowired
    private JpaInstituicaoRepository instituicaoRepository;

    @Autowired
    private JpaDocenteRepository docenteRepository;

    @Autowired
    private JpaAutoriaRepository autoriaRepository;

    private InstituicaoEntity unifesp;
    private InstituicaoEntity usp;
    private DocenteEntity docente1;
    private DocenteEntity docente2;
    private PublicacaoEntity pub1;
    private PublicacaoEntity pub2;
    private PublicacaoEntity pub3;

    @BeforeEach
    void setUp() {
        // Limpar dados existentes
        autoriaRepository.deleteAll();
        publicacaoRepository.deleteAll();
        docenteRepository.deleteAll();
        instituicaoRepository.deleteAll();

        // Criar instituições
        unifesp = InstituicaoEntity.builder()
                .nome("Universidade Federal de São Paulo")
                .sigla("UNIFESP")
                .tipo("PUBLICA")
                .cidade("São Paulo")
                .estado("SP")
                .ativo(true)
                .build();

        usp = InstituicaoEntity.builder()
                .nome("Universidade de São Paulo")
                .sigla("USP")
                .tipo("PUBLICA")
                .cidade("São Paulo")
                .estado("SP")
                .ativo(true)
                .build();

        unifesp = instituicaoRepository.save(unifesp);
        usp = instituicaoRepository.save(usp);

        // Criar docentes
        docente1 = DocenteEntity.builder()
                .instituicao(unifesp)
                .nomeCompleto("Dr. João Silva")
                .email("joao@unifesp.br")
                .ativo(true)
                .build();

        docente2 = DocenteEntity.builder()
                .instituicao(usp)
                .nomeCompleto("Dr. Maria Santos")
                .email("maria@usp.br")
                .ativo(true)
                .build();

        docente1 = docenteRepository.save(docente1);
        docente2 = docenteRepository.save(docente2);

        // Criar publicações
        pub1 = PublicacaoEntity.builder()
                .openalexWorkId("W1234567890")
                .doi("10.1234/pub1.2024")
                .titulo("Machine Learning in Healthcare")
                .anoPublicacao(2024)
                .dataPublicacao(LocalDate.of(2024, 1, 15))
                .tipo("ARTICLE")
                .citedByCount(100)
                .build();

        pub2 = PublicacaoEntity.builder()
                .openalexWorkId("W2345678901")
                .doi("10.1234/pub2.2023")
                .titulo("Deep Learning Applications")
                .anoPublicacao(2023)
                .dataPublicacao(LocalDate.of(2023, 6, 20))
                .tipo("ARTICLE")
                .citedByCount(250)
                .build();

        pub3 = PublicacaoEntity.builder()
                .openalexWorkId("W3456789012")
                .doi("10.1234/pub3.2024")
                .titulo("AI in Medical Diagnosis")
                .anoPublicacao(2024)
                .dataPublicacao(LocalDate.of(2024, 3, 10))
                .tipo("BOOK_CHAPTER")
                .citedByCount(50)
                .build();

        pub1 = publicacaoRepository.save(pub1);
        pub2 = publicacaoRepository.save(pub2);
        pub3 = publicacaoRepository.save(pub3);

        // Criar autorias
        AutoriaEntity autoria1 = AutoriaEntity.builder()
                .publicacao(pub1)
                .docente(docente1)
                .positionOrder(1)
                .authorPosition("first")
                .isCorresponding(true)
                .build();

        AutoriaEntity autoria2 = AutoriaEntity.builder()
                .publicacao(pub2)
                .docente(docente1)
                .positionOrder(1)
                .authorPosition("first")
                .isCorresponding(false)
                .build();

        AutoriaEntity autoria3 = AutoriaEntity.builder()
                .publicacao(pub2)
                .docente(docente2)
                .positionOrder(2)
                .authorPosition("last")
                .isCorresponding(true)
                .build();

        AutoriaEntity autoria4 = AutoriaEntity.builder()
                .publicacao(pub3)
                .docente(docente2)
                .positionOrder(1)
                .authorPosition("first")
                .isCorresponding(true)
                .build();

        autoriaRepository.saveAll(List.of(autoria1, autoria2, autoria3, autoria4));
    }

    @Nested
    @DisplayName("findByOpenalexWorkId Tests")
    class FindByOpenalexWorkIdTests {

        @Test
        @DisplayName("Deve encontrar publicação por OpenAlex Work ID")
        void shouldFindByOpenalexWorkId() {
            // Act
            Optional<PublicacaoEntity> result = publicacaoRepository.findByOpenalexWorkId("W1234567890");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getTitulo()).isEqualTo("Machine Learning in Healthcare");
            assertThat(result.get().getDoi()).isEqualTo("10.1234/pub1.2024");
        }

        @Test
        @DisplayName("Deve retornar vazio quando OpenAlex Work ID não existe")
        void shouldReturnEmptyWhenOpenalexWorkIdNotExists() {
            // Act
            Optional<PublicacaoEntity> result = publicacaoRepository.findByOpenalexWorkId("INVALID");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByDoi Tests")
    class FindByDoiTests {

        @Test
        @DisplayName("Deve encontrar publicação por DOI")
        void shouldFindByDoi() {
            // Act
            Optional<PublicacaoEntity> result = publicacaoRepository.findByDoi("10.1234/pub1.2024");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getOpenalexWorkId()).isEqualTo("W1234567890");
        }

        @Test
        @DisplayName("Deve retornar vazio quando DOI não existe")
        void shouldReturnEmptyWhenDoiNotExists() {
            // Act
            Optional<PublicacaoEntity> result = publicacaoRepository.findByDoi("INVALID");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByAnoPublicacao Tests")
    class FindByAnoPublicacaoTests {

        @Test
        @DisplayName("Deve retornar publicações do ano 2024")
        void shouldReturnPublicationsFrom2024() {
            // Act
            List<PublicacaoEntity> result = publicacaoRepository.findByAnoPublicacao(2024);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(PublicacaoEntity::getTitulo)
                    .containsExactlyInAnyOrder(
                            "Machine Learning in Healthcare",
                            "AI in Medical Diagnosis"
                    );
        }

        @Test
        @DisplayName("Deve retornar publicações do ano 2023")
        void shouldReturnPublicationsFrom2023() {
            // Act
            List<PublicacaoEntity> result = publicacaoRepository.findByAnoPublicacao(2023);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitulo()).isEqualTo("Deep Learning Applications");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há publicações no ano")
        void shouldReturnEmptyListWhenNoPublicationsInYear() {
            // Act
            List<PublicacaoEntity> result = publicacaoRepository.findByAnoPublicacao(2020);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByTipo Tests")
    class FindByTipoTests {

        @Test
        @DisplayName("Deve retornar publicações do tipo ARTICLE")
        void shouldReturnArticlePublications() {
            // Act
            List<PublicacaoEntity> result = publicacaoRepository.findByTipo("ARTICLE");

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(p -> p.getTipo().equals("ARTICLE"));
        }

        @Test
        @DisplayName("Deve retornar publicações do tipo BOOK_CHAPTER")
        void shouldReturnBookChapterPublications() {
            // Act
            List<PublicacaoEntity> result = publicacaoRepository.findByTipo("BOOK_CHAPTER");

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitulo()).isEqualTo("AI in Medical Diagnosis");
        }
    }

    @Nested
    @DisplayName("findByInstituicaoId Tests")
    class FindByInstituicaoIdTests {

        @Test
        @DisplayName("Deve retornar publicações da UNIFESP via docentes")
        void shouldReturnPublicationsFromUnifesp() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<PublicacaoEntity> result = publicacaoRepository.findByInstituicaoId(unifesp.getId(), pageable);

            // Assert
            // Docente1 (UNIFESP) tem pub1 e pub2
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(PublicacaoEntity::getTitulo)
                    .containsExactlyInAnyOrder(
                            "Machine Learning in Healthcare",
                            "Deep Learning Applications"
                    );
        }

        @Test
        @DisplayName("Deve retornar publicações da USP via docentes")
        void shouldReturnPublicationsFromUsp() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<PublicacaoEntity> result = publicacaoRepository.findByInstituicaoId(usp.getId(), pageable);

            // Assert
            // Docente2 (USP) tem pub2 e pub3
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(PublicacaoEntity::getTitulo)
                    .containsExactlyInAnyOrder(
                            "Deep Learning Applications",
                            "AI in Medical Diagnosis"
                    );
        }

        @Test
        @DisplayName("Deve retornar página vazia quando instituição não tem publicações")
        void shouldReturnEmptyPageWhenInstitutionHasNoPublications() {
            // Arrange - Criar instituição sem docentes/publicações
            InstituicaoEntity emptyInst = instituicaoRepository.save(
                    InstituicaoEntity.builder()
                            .nome("Instituição Vazia")
                            .sigla("EMPTY")
                            .tipo("PRIVADA")
                            .cidade("Curitiba")
                            .estado("PR")
                            .ativo(true)
                            .build()
            );

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<PublicacaoEntity> result = publicacaoRepository.findByInstituicaoId(emptyInst.getId(), pageable);

            // Assert
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Deve respeitar paginação")
        void shouldRespectPagination() {
            // Act
            Pageable pageable = PageRequest.of(0, 1);
            Page<PublicacaoEntity> result = publicacaoRepository.findByInstituicaoId(unifesp.getId(), pageable);

            // Assert
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("findByDocenteId Tests")
    class FindByDocenteIdTests {

        @Test
        @DisplayName("Deve retornar publicações do docente1 ordenadas por ano")
        void shouldReturnPublicationsFromDocente1() {
            // Act
            List<PublicacaoEntity> result = publicacaoRepository.findByDocenteId(docente1.getId());

            // Assert
            // Docente1 tem pub1 (2024) e pub2 (2023), deve vir ordenado DESC
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitulo()).isEqualTo("Machine Learning in Healthcare"); // 2024
            assertThat(result.get(1).getTitulo()).isEqualTo("Deep Learning Applications"); // 2023
        }

        @Test
        @DisplayName("Deve retornar publicações do docente2")
        void shouldReturnPublicationsFromDocente2() {
            // Act
            List<PublicacaoEntity> result = publicacaoRepository.findByDocenteId(docente2.getId());

            // Assert
            // Docente2 tem pub2 (2023) e pub3 (2024)
            assertThat(result).hasSize(2);
            assertThat(result).extracting(PublicacaoEntity::getTitulo)
                    .containsExactlyInAnyOrder(
                            "Deep Learning Applications",
                            "AI in Medical Diagnosis"
                    );
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando docente não tem publicações")
        void shouldReturnEmptyListWhenDocenteHasNoPublications() {
            // Arrange - Criar docente sem publicações
            DocenteEntity emptyDocente = docenteRepository.save(
                    DocenteEntity.builder()
                            .instituicao(unifesp)
                            .nomeCompleto("Dr. Sem Publicações")
                            .email("sem@unifesp.br")
                            .ativo(true)
                            .build()
            );

            // Act
            List<PublicacaoEntity> result = publicacaoRepository.findByDocenteId(emptyDocente.getId());

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByOrderByCitedByCountDesc Tests")
    class FindByOrderByCitedByCountDescTests {

        @Test
        @DisplayName("Deve retornar publicações ordenadas por citações (DESC)")
        void shouldReturnPublicationsOrderedByCitations() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<PublicacaoEntity> result = publicacaoRepository.findByOrderByCitedByCountDesc(pageable);

            // Assert
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent().get(0).getCitedByCount()).isEqualTo(250); // pub2
            assertThat(result.getContent().get(1).getCitedByCount()).isEqualTo(100); // pub1
            assertThat(result.getContent().get(2).getCitedByCount()).isEqualTo(50);  // pub3

            assertThat(result.getContent()).extracting(PublicacaoEntity::getTitulo)
                    .containsExactly(
                            "Deep Learning Applications",
                            "Machine Learning in Healthcare",
                            "AI in Medical Diagnosis"
                    );
        }

        @Test
        @DisplayName("Deve respeitar paginação para mais citadas")
        void shouldRespectPaginationForMostCited() {
            // Act
            Pageable pageable = PageRequest.of(0, 2);
            Page<PublicacaoEntity> result = publicacaoRepository.findByOrderByCitedByCountDesc(pageable);

            // Assert
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getContent().get(0).getCitedByCount()).isEqualTo(250);
        }
    }

    @Nested
    @DisplayName("existsByOpenalexWorkId Tests")
    class ExistsByOpenalexWorkIdTests {

        @Test
        @DisplayName("Deve retornar true quando OpenAlex Work ID existe")
        void shouldReturnTrueWhenOpenalexWorkIdExists() {
            // Act
            boolean result = publicacaoRepository.existsByOpenalexWorkId("W1234567890");

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando OpenAlex Work ID não existe")
        void shouldReturnFalseWhenOpenalexWorkIdNotExists() {
            // Act
            boolean result = publicacaoRepository.existsByOpenalexWorkId("INVALID");

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByDoi Tests")
    class ExistsByDoiTests {

        @Test
        @DisplayName("Deve retornar true quando DOI existe")
        void shouldReturnTrueWhenDoiExists() {
            // Act
            boolean result = publicacaoRepository.existsByDoi("10.1234/pub1.2024");

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando DOI não existe")
        void shouldReturnFalseWhenDoiNotExists() {
            // Act
            boolean result = publicacaoRepository.existsByDoi("INVALID");

            // Assert
            assertThat(result).isFalse();
        }
    }
}
