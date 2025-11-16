package com.ppghub.domain.service;

import com.ppghub.application.dto.request.ProgramaCreateRequest;
import com.ppghub.application.dto.response.ProgramaResponse;
import com.ppghub.application.mapper.ProgramaMapper;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.entity.ProgramaEntity;
import com.ppghub.infrastructure.persistence.repository.JpaInstituicaoRepository;
import com.ppghub.infrastructure.persistence.repository.JpaProgramaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProgramaService.
 * Segue padrão AAA (Arrange, Act, Assert) e usa AssertJ para assertions fluentes.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProgramaService Tests")
class ProgramaServiceTest {

    @Mock
    private JpaProgramaRepository repository;

    @Mock
    private JpaInstituicaoRepository instituicaoRepository;

    @Mock
    private ProgramaMapper mapper;

    @InjectMocks
    private ProgramaService service;

    private ProgramaEntity mockEntity;
    private ProgramaResponse mockResponse;
    private ProgramaCreateRequest mockRequest;
    private InstituicaoEntity mockInstituicao;

    @BeforeEach
    void setUp() {
        // Arrange - Preparar dados mock
        mockInstituicao = InstituicaoEntity.builder()
                .id(1L)
                .nome("Universidade Federal de São Paulo")
                .sigla("UNIFESP")
                .build();

        mockEntity = ProgramaEntity.builder()
                .id(1L)
                .instituicao(mockInstituicao)
                .nome("Programa de Pós-Graduação em Ciência da Computação")
                .sigla("PPGCC")
                .codigoCapes("33002010191P0")
                .areaConhecimento("Ciência da Computação")
                .areaAvaliacao("Ciência da Computação")
                .modalidade("ACADEMICO")
                .nivel("MESTRADO_DOUTORADO")
                .conceitoCapes(6)
                .anoAvaliacao(2021)
                .coordenador("Prof. Dr. João Silva")
                .email("ppgcc@unifesp.br")
                .telefone("(11) 5576-4848")
                .website("https://www.unifesp.br/ppgcc")
                .status("ATIVO")
                .dataInicio(LocalDate.of(2010, 3, 1))
                .build();

        mockResponse = ProgramaResponse.builder()
                .id(1L)
                .instituicaoId(1L)
                .instituicaoNome("Universidade Federal de São Paulo")
                .instituicaoSigla("UNIFESP")
                .nome("Programa de Pós-Graduação em Ciência da Computação")
                .sigla("PPGCC")
                .codigoCapes("33002010191P0")
                .areaConhecimento("Ciência da Computação")
                .areaAvaliacao("Ciência da Computação")
                .modalidade("ACADEMICO")
                .nivel("MESTRADO_DOUTORADO")
                .conceitoCapes(6)
                .anoAvaliacao(2021)
                .coordenador("Prof. Dr. João Silva")
                .email("ppgcc@unifesp.br")
                .telefone("(11) 5576-4848")
                .website("https://www.unifesp.br/ppgcc")
                .status("ATIVO")
                .dataInicio(LocalDate.of(2010, 3, 1))
                .build();

        mockRequest = ProgramaCreateRequest.builder()
                .instituicaoId(1L)
                .nome("Programa de Pós-Graduação em Ciência da Computação")
                .sigla("PPGCC")
                .codigoCapes("33002010191P0")
                .areaConhecimento("Ciência da Computação")
                .areaAvaliacao("Ciência da Computação")
                .modalidade("ACADEMICO")
                .nivel("MESTRADO_DOUTORADO")
                .conceitoCapes(6)
                .anoAvaliacao(2021)
                .coordenador("Prof. Dr. João Silva")
                .email("ppgcc@unifesp.br")
                .telefone("(11) 5576-4848")
                .website("https://www.unifesp.br/ppgcc")
                .status("ATIVO")
                .dataInicio(LocalDate.of(2010, 3, 1))
                .build();
    }

    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar lista de todos os programas")
        void shouldReturnAllPrograms() {
            // Arrange
            List<ProgramaEntity> entities = Arrays.asList(mockEntity);
            List<ProgramaResponse> responses = Arrays.asList(mockResponse);

            when(repository.findAll()).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<ProgramaResponse> result = service.findAll();

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNome()).isEqualTo("Programa de Pós-Graduação em Ciência da Computação");
            assertThat(result.get(0).getCodigoCapes()).isEqualTo("33002010191P0");

            verify(repository, times(1)).findAll();
            verify(mapper, times(1)).toResponseList(entities);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver programas")
        void shouldReturnEmptyListWhenNoPrograms() {
            // Arrange
            when(repository.findAll()).thenReturn(Arrays.asList());
            when(mapper.toResponseList(anyList())).thenReturn(Arrays.asList());

            // Act
            List<ProgramaResponse> result = service.findAll();

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar programa quando ID existe")
        void shouldReturnProgramWhenIdExists() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<ProgramaResponse> result = service.findById(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            assertThat(result.get().getSigla()).isEqualTo("PPGCC");
            assertThat(result.get().getConceitoCapes()).isEqualTo(6);

            verify(repository, times(1)).findById(1L);
            verify(mapper, times(1)).toResponse(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando ID não existe")
        void shouldReturnEmptyWhenIdNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<ProgramaResponse> result = service.findById(999L);

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findById(999L);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("findByCodigoCapes Tests")
    class FindByCodigoCapesTests {

        @Test
        @DisplayName("Deve retornar programa quando código CAPES existe")
        void shouldReturnProgramWhenCodigoCapesExists() {
            // Arrange
            when(repository.findByCodigoCapes("33002010191P0")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<ProgramaResponse> result = service.findByCodigoCapes("33002010191P0");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getCodigoCapes()).isEqualTo("33002010191P0");

            verify(repository, times(1)).findByCodigoCapes("33002010191P0");
            verify(mapper, times(1)).toResponse(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando código CAPES não existe")
        void shouldReturnEmptyWhenCodigoCapesNotExists() {
            // Arrange
            when(repository.findByCodigoCapes("INVALID")).thenReturn(Optional.empty());

            // Act
            Optional<ProgramaResponse> result = service.findByCodigoCapes("INVALID");

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findByCodigoCapes("INVALID");
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("findByInstituicao Tests")
    class FindByInstituicaoTests {

        @Test
        @DisplayName("Deve retornar programas da instituição")
        void shouldReturnProgramsByInstitution() {
            // Arrange
            List<ProgramaEntity> entities = Arrays.asList(mockEntity);
            List<ProgramaResponse> responses = Arrays.asList(mockResponse);

            when(repository.findByInstituicaoId(1L)).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<ProgramaResponse> result = service.findByInstituicao(1L);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getInstituicaoId()).isEqualTo(1L);

            verify(repository, times(1)).findByInstituicaoId(1L);
            verify(mapper, times(1)).toResponseList(entities);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando instituição não tem programas")
        void shouldReturnEmptyListWhenInstitutionHasNoPrograms() {
            // Arrange
            when(repository.findByInstituicaoId(999L)).thenReturn(Arrays.asList());
            when(mapper.toResponseList(anyList())).thenReturn(Arrays.asList());

            // Act
            List<ProgramaResponse> result = service.findByInstituicao(999L);

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findByInstituicaoId(999L);
        }
    }

    @Nested
    @DisplayName("findByAreaConhecimento Tests")
    class FindByAreaConhecimentoTests {

        @Test
        @DisplayName("Deve retornar programas por área de conhecimento")
        void shouldReturnProgramsByKnowledgeArea() {
            // Arrange
            List<ProgramaEntity> entities = Arrays.asList(mockEntity);
            List<ProgramaResponse> responses = Arrays.asList(mockResponse);

            when(repository.findByAreaConhecimento("Ciência da Computação")).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<ProgramaResponse> result = service.findByAreaConhecimento("Ciência da Computação");

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAreaConhecimento()).isEqualTo("Ciência da Computação");

            verify(repository, times(1)).findByAreaConhecimento("Ciência da Computação");
            verify(mapper, times(1)).toResponseList(entities);
        }
    }

    @Nested
    @DisplayName("findByStatus Tests")
    class FindByStatusTests {

        @Test
        @DisplayName("Deve retornar programas por status")
        void shouldReturnProgramsByStatus() {
            // Arrange
            List<ProgramaEntity> entities = Arrays.asList(mockEntity);
            List<ProgramaResponse> responses = Arrays.asList(mockResponse);

            when(repository.findByStatus("ATIVO")).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<ProgramaResponse> result = service.findByStatus("ATIVO");

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo("ATIVO");

            verify(repository, times(1)).findByStatus("ATIVO");
            verify(mapper, times(1)).toResponseList(entities);
        }
    }

    @Nested
    @DisplayName("findProgramasAtivosByInstituicao Tests")
    class FindProgramasAtivosByInstituicaoTests {

        @Test
        @DisplayName("Deve retornar apenas programas ativos da instituição")
        void shouldReturnOnlyActiveProgramsByInstitution() {
            // Arrange
            List<ProgramaEntity> entities = Arrays.asList(mockEntity);
            List<ProgramaResponse> responses = Arrays.asList(mockResponse);

            when(repository.findProgramasAtivosByInstituicao(1L)).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<ProgramaResponse> result = service.findProgramasAtivosByInstituicao(1L);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(prog -> prog.getStatus().equals("ATIVO"));
            assertThat(result.get(0).getInstituicaoId()).isEqualTo(1L);

            verify(repository, times(1)).findProgramasAtivosByInstituicao(1L);
            verify(mapper, times(1)).toResponseList(entities);
        }
    }

    @Nested
    @DisplayName("create Tests")
    class CreateTests {

        @Test
        @DisplayName("Deve criar programa com sucesso")
        void shouldCreateProgramSuccessfully() {
            // Arrange
            when(instituicaoRepository.findById(1L)).thenReturn(Optional.of(mockInstituicao));
            when(repository.findByCodigoCapes("33002010191P0")).thenReturn(Optional.empty());
            when(mapper.toEntity(mockRequest)).thenReturn(mockEntity);
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            ProgramaResponse result = service.create(mockRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getSigla()).isEqualTo("PPGCC");
            assertThat(result.getCodigoCapes()).isEqualTo("33002010191P0");

            verify(instituicaoRepository, times(1)).findById(1L);
            verify(repository, times(1)).findByCodigoCapes("33002010191P0");
            verify(repository, times(1)).save(mockEntity);
            verify(mapper, times(1)).toEntity(mockRequest);
            verify(mapper, times(1)).toResponse(mockEntity);
        }

        @Test
        @DisplayName("Deve lançar exceção quando instituição não existe")
        void shouldThrowExceptionWhenInstitutionNotFound() {
            // Arrange
            when(instituicaoRepository.findById(999L)).thenReturn(Optional.empty());
            mockRequest.setInstituicaoId(999L);

            // Act & Assert
            assertThatThrownBy(() -> service.create(mockRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Instituição não encontrada");

            verify(instituicaoRepository, times(1)).findById(999L);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando código CAPES já existe")
        void shouldThrowExceptionWhenCodigoCapesExists() {
            // Arrange
            when(instituicaoRepository.findById(1L)).thenReturn(Optional.of(mockInstituicao));
            when(repository.findByCodigoCapes("33002010191P0")).thenReturn(Optional.of(mockEntity));

            // Act & Assert
            assertThatThrownBy(() -> service.create(mockRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("código CAPES");

            verify(instituicaoRepository, times(1)).findById(1L);
            verify(repository, times(1)).findByCodigoCapes("33002010191P0");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve criar programa sem código CAPES")
        void shouldCreateProgramWithoutCodigoCapes() {
            // Arrange
            mockRequest.setCodigoCapes(null);
            when(instituicaoRepository.findById(1L)).thenReturn(Optional.of(mockInstituicao));
            when(mapper.toEntity(mockRequest)).thenReturn(mockEntity);
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            ProgramaResponse result = service.create(mockRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(repository, never()).findByCodigoCapes(any());
            verify(repository, times(1)).save(mockEntity);
        }
    }

    @Nested
    @DisplayName("update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar programa com sucesso")
        void shouldUpdateProgramSuccessfully() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<ProgramaResponse> result = service.update(1L, mockRequest);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);

            verify(repository, times(1)).findById(1L);
            verify(mapper, times(1)).updateEntityFromRequest(mockRequest, mockEntity);
            verify(repository, times(1)).save(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando programa não existe")
        void shouldReturnEmptyWhenProgramNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<ProgramaResponse> result = service.update(999L, mockRequest);

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
        @DisplayName("Deve deletar programa com sucesso")
        void shouldDeleteProgramSuccessfully() {
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
        @DisplayName("Deve retornar false quando programa não existe")
        void shouldReturnFalseWhenProgramNotExists() {
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
    @DisplayName("inativar Tests")
    class InativarTests {

        @Test
        @DisplayName("Deve inativar programa com sucesso")
        void shouldInactivateProgramSuccessfully() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<ProgramaResponse> result = service.inativar(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(mockEntity.getStatus()).isEqualTo("INATIVO");
            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).save(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando programa não existe")
        void shouldReturnEmptyWhenProgramNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<ProgramaResponse> result = service.inativar(999L);

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findById(999L);
            verify(repository, never()).save(any());
        }
    }
}
