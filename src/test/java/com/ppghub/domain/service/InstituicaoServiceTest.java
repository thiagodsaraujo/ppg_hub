package com.ppghub.domain.service;

import com.ppghub.application.dto.request.InstituicaoCreateRequest;
import com.ppghub.application.dto.response.InstituicaoResponse;
import com.ppghub.application.mapper.InstituicaoMapper;
import com.ppghub.domain.exception.DuplicateEntityException;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.repository.JpaInstituicaoRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para InstituicaoService.
 * Segue padrão AAA (Arrange, Act, Assert) e usa AssertJ para assertions fluentes.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InstituicaoService Tests")
class InstituicaoServiceTest {

    @Mock
    private JpaInstituicaoRepository repository;

    @Mock
    private InstituicaoMapper mapper;

    @InjectMocks
    private InstituicaoService service;

    private InstituicaoEntity mockEntity;
    private InstituicaoResponse mockResponse;
    private InstituicaoCreateRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Arrange - Preparar dados mock
        mockEntity = InstituicaoEntity.builder()
                .id(1L)
                .nome("Universidade Federal de São Paulo")
                .sigla("UNIFESP")
                .tipo("PUBLICA")
                .cidade("São Paulo")
                .estado("SP")
                .ativo(true)
                .build();

        mockResponse = InstituicaoResponse.builder()
                .id(1L)
                .nome("Universidade Federal de São Paulo")
                .sigla("UNIFESP")
                .tipo("PUBLICA")
                .cidade("São Paulo")
                .estado("SP")
                .ativo(true)
                .build();

        mockRequest = InstituicaoCreateRequest.builder()
                .nome("Universidade Federal de São Paulo")
                .sigla("UNIFESP")
                .tipo("PUBLICA")
                .cidade("São Paulo")
                .estado("SP")
                .build();
    }

    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar lista de todas as instituições")
        void shouldReturnAllInstitutions() {
            // Arrange
            List<InstituicaoEntity> entities = Arrays.asList(mockEntity);
            List<InstituicaoResponse> responses = Arrays.asList(mockResponse);

            when(repository.findAll()).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<InstituicaoResponse> result = service.findAll();

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNome()).isEqualTo("Universidade Federal de São Paulo");

            verify(repository, times(1)).findAll();
            verify(mapper, times(1)).toResponseList(entities);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver instituições")
        void shouldReturnEmptyListWhenNoInstitutions() {
            // Arrange
            when(repository.findAll()).thenReturn(Arrays.asList());
            when(mapper.toResponseList(anyList())).thenReturn(Arrays.asList());

            // Act
            List<InstituicaoResponse> result = service.findAll();

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar instituição quando ID existe")
        void shouldReturnInstitutionWhenIdExists() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<InstituicaoResponse> result = service.findById(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            assertThat(result.get().getSigla()).isEqualTo("UNIFESP");

            verify(repository, times(1)).findById(1L);
            verify(mapper, times(1)).toResponse(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando ID não existe")
        void shouldReturnEmptyWhenIdNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<InstituicaoResponse> result = service.findById(999L);

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findById(999L);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("create Tests")
    class CreateTests {

        @Test
        @DisplayName("Deve criar instituição com sucesso")
        void shouldCreateInstitutionSuccessfully() {
            // Arrange
            when(repository.existsByCnpj(anyString())).thenReturn(false);
            when(repository.existsBySigla(anyString())).thenReturn(false);
            when(mapper.toEntity(mockRequest)).thenReturn(mockEntity);
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            InstituicaoResponse result = service.create(mockRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getSigla()).isEqualTo("UNIFESP");

            verify(repository, times(1)).existsBySigla("UNIFESP");
            verify(repository, times(1)).save(mockEntity);
            verify(mapper, times(1)).toEntity(mockRequest);
            verify(mapper, times(1)).toResponse(mockEntity);
        }

        @Test
        @DisplayName("Deve lançar exceção quando CNPJ já existe")
        void shouldThrowExceptionWhenCnpjExists() {
            // Arrange
            mockRequest.setCnpj("12345678000190");
            when(repository.existsByCnpj("12345678000190")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> service.create(mockRequest))
                    .isInstanceOf(DuplicateEntityException.class)
                    .hasMessageContaining("CNPJ");

            verify(repository, times(1)).existsByCnpj("12345678000190");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando sigla já existe")
        void shouldThrowExceptionWhenSiglaExists() {
            // Arrange
            when(repository.existsBySigla("UNIFESP")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> service.create(mockRequest))
                    .isInstanceOf(DuplicateEntityException.class)
                    .hasMessageContaining("sigla");

            verify(repository, times(1)).existsBySigla("UNIFESP");
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar instituição com sucesso")
        void shouldUpdateInstitutionSuccessfully() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<InstituicaoResponse> result = service.update(1L, mockRequest);

            // Assert
            assertThat(result).isPresent();
            verify(repository, times(1)).findById(1L);
            verify(mapper, times(1)).updateEntityFromRequest(mockRequest, mockEntity);
            verify(repository, times(1)).save(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando instituição não existe")
        void shouldReturnEmptyWhenInstitutionNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<InstituicaoResponse> result = service.update(999L, mockRequest);

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
        @DisplayName("Deve deletar instituição com sucesso")
        void shouldDeleteInstitutionSuccessfully() {
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
        @DisplayName("Deve retornar false quando instituição não existe")
        void shouldReturnFalseWhenInstitutionNotExists() {
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
    @DisplayName("desativar Tests")
    class DesativarTests {

        @Test
        @DisplayName("Deve desativar instituição com sucesso")
        void shouldDeactivateInstitutionSuccessfully() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<InstituicaoResponse> result = service.desativar(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(mockEntity.getAtivo()).isFalse();
            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).save(mockEntity);
        }
    }

    @Nested
    @DisplayName("searchByNome Tests")
    class SearchByNomeTests {

        @Test
        @DisplayName("Deve buscar instituições por nome com paginação")
        void shouldSearchInstitutionsByName() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<InstituicaoEntity> entityPage = new PageImpl<>(Arrays.asList(mockEntity));
            Page<InstituicaoResponse> responsePage = entityPage.map(mapper::toResponse);

            when(repository.searchByNome("Federal", pageable)).thenReturn(entityPage);
            when(mapper.toResponse(any(InstituicaoEntity.class))).thenReturn(mockResponse);

            // Act
            Page<InstituicaoResponse> result = service.searchByNome("Federal", pageable);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(repository, times(1)).searchByNome("Federal", pageable);
        }
    }

    @Nested
    @DisplayName("findAtivas Tests")
    class FindAtivasTests {

        @Test
        @DisplayName("Deve retornar apenas instituições ativas")
        void shouldReturnOnlyActiveInstitutions() {
            // Arrange
            when(repository.findByAtivoTrue()).thenReturn(Arrays.asList(mockEntity));
            when(mapper.toResponseList(anyList())).thenReturn(Arrays.asList(mockResponse));

            // Act
            List<InstituicaoResponse> result = service.findAtivas();

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(inst -> inst.getAtivo() == true);
            verify(repository, times(1)).findByAtivoTrue();
        }
    }

    @Nested
    @DisplayName("Buscas por identificadores Tests")
    class FindByIdentifiersTests {

        @Test
        @DisplayName("Deve buscar por sigla")
        void shouldFindBySigla() {
            // Arrange
            when(repository.findBySigla("UNIFESP")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<InstituicaoResponse> result = service.findBySigla("UNIFESP");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getSigla()).isEqualTo("UNIFESP");
        }

        @Test
        @DisplayName("Deve buscar por OpenAlex ID")
        void shouldFindByOpenalexId() {
            // Arrange
            when(repository.findByOpenalexInstitutionId("I123456")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<InstituicaoResponse> result = service.findByOpenalexId("I123456");

            // Assert
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("Deve buscar por ROR ID")
        void shouldFindByRorId() {
            // Arrange
            when(repository.findByRorId("ror:123456")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<InstituicaoResponse> result = service.findByRorId("ror:123456");

            // Assert
            assertThat(result).isPresent();
        }
    }
}
