package com.ppghub.domain.service;

import com.ppghub.application.dto.request.PublicacaoCreateRequest;
import com.ppghub.application.dto.response.PublicacaoResponse;
import com.ppghub.application.mapper.PublicacaoMapper;
import com.ppghub.infrastructure.persistence.entity.PublicacaoEntity;
import com.ppghub.infrastructure.persistence.repository.JpaPublicacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para PublicacaoService.
 * Segue padrão AAA (Arrange, Act, Assert) e usa AssertJ para assertions fluentes.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PublicacaoService Tests")
class PublicacaoServiceTest {

    @Mock
    private JpaPublicacaoRepository repository;

    @Mock
    private PublicacaoMapper mapper;

    @InjectMocks
    private PublicacaoService service;

    private PublicacaoEntity mockEntity;
    private PublicacaoResponse mockResponse;
    private PublicacaoCreateRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Arrange - Preparar dados mock
        mockEntity = PublicacaoEntity.builder()
                .id(1L)
                .openalexWorkId("W1234567890")
                .doi("10.1234/example.2024.001")
                .pmid("12345678")
                .pmcid("PMC1234567")
                .titulo("Machine Learning Applications in Healthcare: A Comprehensive Review")
                .tituloOriginal("Machine Learning Applications in Healthcare: A Comprehensive Review")
                .resumo("This paper presents a comprehensive review of machine learning applications...")
                .anoPublicacao(2024)
                .dataPublicacao(LocalDate.of(2024, 3, 15))
                .tipo("ARTICLE")
                .idioma("en")
                .fonteNome("Journal of Medical Informatics")
                .fonteIssn("1234-5678")
                .fonteOpenalexId("S123456789")
                .volume("42")
                .issue("3")
                .paginaInicial("145")
                .paginaFinal("178")
                .citedByCount(25)
                .isRetracted(false)
                .isParatext(false)
                .isOa(true)
                .landingPageUrl("https://example.com/article/123")
                .pdfUrl("https://example.com/article/123.pdf")
                .build();

        mockResponse = PublicacaoResponse.builder()
                .id(1L)
                .openalexWorkId("W1234567890")
                .doi("10.1234/example.2024.001")
                .pmid("12345678")
                .pmcid("PMC1234567")
                .titulo("Machine Learning Applications in Healthcare: A Comprehensive Review")
                .tituloOriginal("Machine Learning Applications in Healthcare: A Comprehensive Review")
                .resumo("This paper presents a comprehensive review of machine learning applications...")
                .anoPublicacao(2024)
                .dataPublicacao(LocalDate.of(2024, 3, 15))
                .tipo("ARTICLE")
                .idioma("en")
                .fonteNome("Journal of Medical Informatics")
                .fonteIssn("1234-5678")
                .fonteOpenalexId("S123456789")
                .volume("42")
                .issue("3")
                .paginaInicial("145")
                .paginaFinal("178")
                .citedByCount(25)
                .isRetracted(false)
                .isParatext(false)
                .isOa(true)
                .landingPageUrl("https://example.com/article/123")
                .pdfUrl("https://example.com/article/123.pdf")
                .build();

        mockRequest = PublicacaoCreateRequest.builder()
                .openalexWorkId("W1234567890")
                .doi("10.1234/example.2024.001")
                .pmid("12345678")
                .pmcid("PMC1234567")
                .titulo("Machine Learning Applications in Healthcare: A Comprehensive Review")
                .tituloOriginal("Machine Learning Applications in Healthcare: A Comprehensive Review")
                .resumo("This paper presents a comprehensive review of machine learning applications...")
                .anoPublicacao(2024)
                .dataPublicacao(LocalDate.of(2024, 3, 15))
                .tipo("ARTICLE")
                .idioma("en")
                .fonteNome("Journal of Medical Informatics")
                .fonteIssn("1234-5678")
                .fonteOpenalexId("S123456789")
                .volume("42")
                .issue("3")
                .paginaInicial("145")
                .paginaFinal("178")
                .citedByCount(25)
                .isRetracted(false)
                .isParatext(false)
                .isOa(true)
                .landingPageUrl("https://example.com/article/123")
                .pdfUrl("https://example.com/article/123.pdf")
                .build();
    }

    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar lista de todas as publicações")
        void shouldReturnAllPublications() {
            // Arrange
            List<PublicacaoEntity> entities = Arrays.asList(mockEntity);
            List<PublicacaoResponse> responses = Arrays.asList(mockResponse);

            when(repository.findAll()).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<PublicacaoResponse> result = service.findAll();

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitulo()).contains("Machine Learning");
            assertThat(result.get(0).getDoi()).isEqualTo("10.1234/example.2024.001");

            verify(repository, times(1)).findAll();
            verify(mapper, times(1)).toResponseList(entities);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver publicações")
        void shouldReturnEmptyListWhenNoPublications() {
            // Arrange
            when(repository.findAll()).thenReturn(Arrays.asList());
            when(mapper.toResponseList(anyList())).thenReturn(Arrays.asList());

            // Act
            List<PublicacaoResponse> result = service.findAll();

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar publicação quando ID existe")
        void shouldReturnPublicationWhenIdExists() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<PublicacaoResponse> result = service.findById(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            assertThat(result.get().getOpenalexWorkId()).isEqualTo("W1234567890");

            verify(repository, times(1)).findById(1L);
            verify(mapper, times(1)).toResponse(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando ID não existe")
        void shouldReturnEmptyWhenIdNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<PublicacaoResponse> result = service.findById(999L);

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findById(999L);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("findByOpenalexWorkId Tests")
    class FindByOpenalexWorkIdTests {

        @Test
        @DisplayName("Deve retornar publicação quando OpenAlex Work ID existe")
        void shouldReturnPublicationWhenOpenalexWorkIdExists() {
            // Arrange
            when(repository.findByOpenalexWorkId("W1234567890")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<PublicacaoResponse> result = service.findByOpenalexWorkId("W1234567890");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getOpenalexWorkId()).isEqualTo("W1234567890");

            verify(repository, times(1)).findByOpenalexWorkId("W1234567890");
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando OpenAlex Work ID não existe")
        void shouldReturnEmptyWhenOpenalexWorkIdNotExists() {
            // Arrange
            when(repository.findByOpenalexWorkId("INVALID")).thenReturn(Optional.empty());

            // Act
            Optional<PublicacaoResponse> result = service.findByOpenalexWorkId("INVALID");

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findByOpenalexWorkId("INVALID");
        }
    }

    @Nested
    @DisplayName("findByDoi Tests")
    class FindByDoiTests {

        @Test
        @DisplayName("Deve retornar publicação quando DOI existe")
        void shouldReturnPublicationWhenDoiExists() {
            // Arrange
            when(repository.findByDoi("10.1234/example.2024.001")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<PublicacaoResponse> result = service.findByDoi("10.1234/example.2024.001");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getDoi()).isEqualTo("10.1234/example.2024.001");

            verify(repository, times(1)).findByDoi("10.1234/example.2024.001");
        }
    }

    @Nested
    @DisplayName("findByAno Tests")
    class FindByAnoTests {

        @Test
        @DisplayName("Deve retornar publicações do ano especificado")
        void shouldReturnPublicationsByYear() {
            // Arrange
            List<PublicacaoEntity> entities = Arrays.asList(mockEntity);
            List<PublicacaoResponse> responses = Arrays.asList(mockResponse);

            when(repository.findByAnoPublicacao(2024)).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<PublicacaoResponse> result = service.findByAno(2024);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(pub -> pub.getAnoPublicacao() == 2024);

            verify(repository, times(1)).findByAnoPublicacao(2024);
        }
    }

    @Nested
    @DisplayName("findByTipo Tests")
    class FindByTipoTests {

        @Test
        @DisplayName("Deve retornar publicações por tipo")
        void shouldReturnPublicationsByType() {
            // Arrange
            List<PublicacaoEntity> entities = Arrays.asList(mockEntity);
            List<PublicacaoResponse> responses = Arrays.asList(mockResponse);

            when(repository.findByTipo("ARTICLE")).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<PublicacaoResponse> result = service.findByTipo("ARTICLE");

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(pub -> pub.getTipo().equals("ARTICLE"));

            verify(repository, times(1)).findByTipo("ARTICLE");
        }
    }

    @Nested
    @DisplayName("findByInstituicao Tests")
    class FindByInstituicaoTests {

        @Test
        @DisplayName("Deve retornar publicações da instituição com paginação")
        void shouldReturnPublicationsByInstitutionPaginated() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<PublicacaoEntity> entityPage = new PageImpl<>(Arrays.asList(mockEntity));

            when(repository.findByInstituicaoId(1L, pageable)).thenReturn(entityPage);
            when(mapper.toResponse(any(PublicacaoEntity.class))).thenReturn(mockResponse);

            // Act
            Page<PublicacaoResponse> result = service.findByInstituicao(1L, pageable);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(repository, times(1)).findByInstituicaoId(1L, pageable);
        }
    }

    @Nested
    @DisplayName("findByDocente Tests")
    class FindByDocenteTests {

        @Test
        @DisplayName("Deve retornar publicações do docente")
        void shouldReturnPublicationsByDocente() {
            // Arrange
            List<PublicacaoEntity> entities = Arrays.asList(mockEntity);
            List<PublicacaoResponse> responses = Arrays.asList(mockResponse);

            when(repository.findByDocenteId(1L)).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<PublicacaoResponse> result = service.findByDocente(1L);

            // Assert
            assertThat(result).isNotEmpty();
            verify(repository, times(1)).findByDocenteId(1L);
        }
    }

    @Nested
    @DisplayName("findMaisCitadas Tests")
    class FindMaisCitadasTests {

        @Test
        @DisplayName("Deve retornar publicações mais citadas ordenadas")
        void shouldReturnMostCitedPublications() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<PublicacaoEntity> entityPage = new PageImpl<>(Arrays.asList(mockEntity));

            when(repository.findByOrderByCitedByCountDesc(pageable)).thenReturn(entityPage);
            when(mapper.toResponse(any(PublicacaoEntity.class))).thenReturn(mockResponse);

            // Act
            Page<PublicacaoResponse> result = service.findMaisCitadas(pageable);

            // Assert
            assertThat(result).isNotEmpty();
            verify(repository, times(1)).findByOrderByCitedByCountDesc(pageable);
        }
    }

    @Nested
    @DisplayName("create Tests")
    class CreateTests {

        @Test
        @DisplayName("Deve criar publicação com sucesso")
        void shouldCreatePublicationSuccessfully() {
            // Arrange
            when(repository.existsByOpenalexWorkId("W1234567890")).thenReturn(false);
            when(repository.existsByDoi("10.1234/example.2024.001")).thenReturn(false);
            when(mapper.toEntity(mockRequest)).thenReturn(mockEntity);
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            PublicacaoResponse result = service.create(mockRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getOpenalexWorkId()).isEqualTo("W1234567890");
            assertThat(result.getDoi()).isEqualTo("10.1234/example.2024.001");

            verify(repository, times(1)).existsByOpenalexWorkId("W1234567890");
            verify(repository, times(1)).existsByDoi("10.1234/example.2024.001");
            verify(repository, times(1)).save(mockEntity);
        }

        @Test
        @DisplayName("Deve lançar exceção quando OpenAlex Work ID já existe")
        void shouldThrowExceptionWhenOpenalexWorkIdExists() {
            // Arrange
            when(repository.existsByOpenalexWorkId("W1234567890")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> service.create(mockRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OpenAlex Work ID");

            verify(repository, times(1)).existsByOpenalexWorkId("W1234567890");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando DOI já existe")
        void shouldThrowExceptionWhenDoiExists() {
            // Arrange
            when(repository.existsByOpenalexWorkId("W1234567890")).thenReturn(false);
            when(repository.existsByDoi("10.1234/example.2024.001")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> service.create(mockRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("DOI");

            verify(repository, times(1)).existsByOpenalexWorkId("W1234567890");
            verify(repository, times(1)).existsByDoi("10.1234/example.2024.001");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve criar publicação sem DOI")
        void shouldCreatePublicationWithoutDoi() {
            // Arrange
            mockRequest.setDoi(null);
            when(repository.existsByOpenalexWorkId("W1234567890")).thenReturn(false);
            when(mapper.toEntity(mockRequest)).thenReturn(mockEntity);
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            PublicacaoResponse result = service.create(mockRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(repository, never()).existsByDoi(any());
            verify(repository, times(1)).save(mockEntity);
        }
    }

    @Nested
    @DisplayName("update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar publicação com sucesso")
        void shouldUpdatePublicationSuccessfully() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<PublicacaoResponse> result = service.update(1L, mockRequest);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);

            verify(repository, times(1)).findById(1L);
            verify(mapper, times(1)).updateEntityFromRequest(mockRequest, mockEntity);
            verify(repository, times(1)).save(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando publicação não existe")
        void shouldReturnEmptyWhenPublicationNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<PublicacaoResponse> result = service.update(999L, mockRequest);

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findById(999L);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar publicação com sucesso")
        void shouldDeletePublicationSuccessfully() {
            // Arrange
            when(repository.existsById(1L)).thenReturn(true);
            doNothing().when(repository).deleteById(1L);

            // Act
            boolean result = service.delete(1L);

            // Assert
            assertThat(result).isTrue();
            verify(repository, times(1)).existsById(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Deve retornar false quando publicação não existe")
        void shouldReturnFalseWhenPublicationNotExists() {
            // Arrange
            when(repository.existsById(999L)).thenReturn(false);

            // Act
            boolean result = service.delete(999L);

            // Assert
            assertThat(result).isFalse();
            verify(repository, times(1)).existsById(999L);
            verify(repository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("existsByOpenalexWorkId Tests")
    class ExistsByOpenalexWorkIdTests {

        @Test
        @DisplayName("Deve retornar true quando OpenAlex Work ID existe")
        void shouldReturnTrueWhenOpenalexWorkIdExists() {
            // Arrange
            when(repository.existsByOpenalexWorkId("W1234567890")).thenReturn(true);

            // Act
            boolean result = service.existsByOpenalexWorkId("W1234567890");

            // Assert
            assertThat(result).isTrue();
            verify(repository, times(1)).existsByOpenalexWorkId("W1234567890");
        }

        @Test
        @DisplayName("Deve retornar false quando OpenAlex Work ID não existe")
        void shouldReturnFalseWhenOpenalexWorkIdNotExists() {
            // Arrange
            when(repository.existsByOpenalexWorkId("INVALID")).thenReturn(false);

            // Act
            boolean result = service.existsByOpenalexWorkId("INVALID");

            // Assert
            assertThat(result).isFalse();
            verify(repository, times(1)).existsByOpenalexWorkId("INVALID");
        }
    }

    @Nested
    @DisplayName("existsByDoi Tests")
    class ExistsByDoiTests {

        @Test
        @DisplayName("Deve retornar true quando DOI existe")
        void shouldReturnTrueWhenDoiExists() {
            // Arrange
            when(repository.existsByDoi("10.1234/example.2024.001")).thenReturn(true);

            // Act
            boolean result = service.existsByDoi("10.1234/example.2024.001");

            // Assert
            assertThat(result).isTrue();
            verify(repository, times(1)).existsByDoi("10.1234/example.2024.001");
        }

        @Test
        @DisplayName("Deve retornar false quando DOI não existe")
        void shouldReturnFalseWhenDoiNotExists() {
            // Arrange
            when(repository.existsByDoi("INVALID")).thenReturn(false);

            // Act
            boolean result = service.existsByDoi("INVALID");

            // Assert
            assertThat(result).isFalse();
            verify(repository, times(1)).existsByDoi("INVALID");
        }
    }
}
