package com.ppghub.domain.service;

import com.ppghub.application.dto.request.DocenteCreateRequest;
import com.ppghub.application.dto.response.DocenteResponse;
import com.ppghub.application.mapper.DocenteMapper;
import com.ppghub.infrastructure.persistence.entity.DocenteEntity;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.repository.JpaDocenteRepository;
import com.ppghub.infrastructure.persistence.repository.JpaInstituicaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para DocenteService.
 * Segue padrão AAA (Arrange, Act, Assert) e usa AssertJ para assertions fluentes.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DocenteService Tests")
class DocenteServiceTest {

    @Mock
    private JpaDocenteRepository repository;

    @Mock
    private JpaInstituicaoRepository instituicaoRepository;

    @Mock
    private DocenteMapper mapper;

    @InjectMocks
    private DocenteService service;

    private DocenteEntity mockEntity;
    private DocenteResponse mockResponse;
    private DocenteCreateRequest mockRequest;
    private InstituicaoEntity mockInstituicao;

    @BeforeEach
    void setUp() {
        // Arrange - Preparar dados mock
        mockInstituicao = InstituicaoEntity.builder()
                .id(1L)
                .nome("Universidade Federal de São Paulo")
                .sigla("UNIFESP")
                .build();

        mockEntity = DocenteEntity.builder()
                .id(1L)
                .instituicao(mockInstituicao)
                .nomeCompleto("Dr. João Silva Santos")
                .nomeCitacao("SILVA, J. S.")
                .cpf("12345678901")
                .email("joao.silva@unifesp.br")
                .telefone("(11) 99999-9999")
                .lattesId("1234567890123456")
                .orcid("0000-0001-2345-6789")
                .openalexAuthorId("A1234567890")
                .scopusId("57123456789")
                .researcherId("ABC-1234-2021")
                .titulacao("DOUTORADO")
                .areaAtuacao("Ciência da Computação")
                .tipoVinculo("EFETIVO")
                .regimeTrabalho("DEDICACAO_EXCLUSIVA")
                .dataIngresso(LocalDate.of(2015, 3, 1))
                .ativo(true)
                .openalexWorksCount(150)
                .openalexCitedByCount(1200)
                .openalexHIndex(18)
                .openalexLastSyncAt(LocalDateTime.now())
                .build();

        mockResponse = DocenteResponse.builder()
                .id(1L)
                .instituicaoId(1L)
                .instituicaoNome("Universidade Federal de São Paulo")
                .instituicaoSigla("UNIFESP")
                .nomeCompleto("Dr. João Silva Santos")
                .nomeCitacao("SILVA, J. S.")
                .cpf("12345678901")
                .email("joao.silva@unifesp.br")
                .telefone("(11) 99999-9999")
                .lattesId("1234567890123456")
                .orcid("0000-0001-2345-6789")
                .openalexAuthorId("A1234567890")
                .scopusId("57123456789")
                .researcherId("ABC-1234-2021")
                .titulacao("DOUTORADO")
                .areaAtuacao("Ciência da Computação")
                .tipoVinculo("EFETIVO")
                .regimeTrabalho("DEDICACAO_EXCLUSIVA")
                .dataIngresso(LocalDate.of(2015, 3, 1))
                .ativo(true)
                .openalexWorksCount(150)
                .openalexCitedByCount(1200)
                .openalexHIndex(18)
                .build();

        mockRequest = DocenteCreateRequest.builder()
                .instituicaoId(1L)
                .nomeCompleto("Dr. João Silva Santos")
                .nomeCitacao("SILVA, J. S.")
                .cpf("12345678901")
                .email("joao.silva@unifesp.br")
                .telefone("(11) 99999-9999")
                .lattesId("1234567890123456")
                .orcid("0000-0001-2345-6789")
                .openalexAuthorId("A1234567890")
                .scopusId("57123456789")
                .researcherId("ABC-1234-2021")
                .titulacao("DOUTORADO")
                .areaAtuacao("Ciência da Computação")
                .tipoVinculo("EFETIVO")
                .regimeTrabalho("DEDICACAO_EXCLUSIVA")
                .dataIngresso(LocalDate.of(2015, 3, 1))
                .ativo(true)
                .build();
    }

    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar lista de todos os docentes")
        void shouldReturnAllDocentes() {
            // Arrange
            List<DocenteEntity> entities = Arrays.asList(mockEntity);
            List<DocenteResponse> responses = Arrays.asList(mockResponse);

            when(repository.findAll()).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<DocenteResponse> result = service.findAll();

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNomeCompleto()).isEqualTo("Dr. João Silva Santos");
            assertThat(result.get(0).getOrcid()).isEqualTo("0000-0001-2345-6789");

            verify(repository, times(1)).findAll();
            verify(mapper, times(1)).toResponseList(entities);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver docentes")
        void shouldReturnEmptyListWhenNoDocentes() {
            // Arrange
            when(repository.findAll()).thenReturn(Arrays.asList());
            when(mapper.toResponseList(anyList())).thenReturn(Arrays.asList());

            // Act
            List<DocenteResponse> result = service.findAll();

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar docente quando ID existe")
        void shouldReturnDocenteWhenIdExists() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<DocenteResponse> result = service.findById(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            assertThat(result.get().getNomeCompleto()).isEqualTo("Dr. João Silva Santos");

            verify(repository, times(1)).findById(1L);
            verify(mapper, times(1)).toResponse(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando ID não existe")
        void shouldReturnEmptyWhenIdNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<DocenteResponse> result = service.findById(999L);

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findById(999L);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("findByCpf Tests")
    class FindByCpfTests {

        @Test
        @DisplayName("Deve retornar docente quando CPF existe")
        void shouldReturnDocenteWhenCpfExists() {
            // Arrange
            when(repository.findByCpf("12345678901")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<DocenteResponse> result = service.findByCpf("12345678901");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getCpf()).isEqualTo("12345678901");

            verify(repository, times(1)).findByCpf("12345678901");
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando CPF não existe")
        void shouldReturnEmptyWhenCpfNotExists() {
            // Arrange
            when(repository.findByCpf("99999999999")).thenReturn(Optional.empty());

            // Act
            Optional<DocenteResponse> result = service.findByCpf("99999999999");

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findByCpf("99999999999");
        }
    }

    @Nested
    @DisplayName("findByLattesId Tests")
    class FindByLattesIdTests {

        @Test
        @DisplayName("Deve retornar docente quando Lattes ID existe")
        void shouldReturnDocenteWhenLattesIdExists() {
            // Arrange
            when(repository.findByLattesId("1234567890123456")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<DocenteResponse> result = service.findByLattesId("1234567890123456");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getLattesId()).isEqualTo("1234567890123456");

            verify(repository, times(1)).findByLattesId("1234567890123456");
        }
    }

    @Nested
    @DisplayName("findByOrcid Tests")
    class FindByOrcidTests {

        @Test
        @DisplayName("Deve retornar docente quando ORCID existe")
        void shouldReturnDocenteWhenOrcidExists() {
            // Arrange
            when(repository.findByOrcid("0000-0001-2345-6789")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<DocenteResponse> result = service.findByOrcid("0000-0001-2345-6789");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getOrcid()).isEqualTo("0000-0001-2345-6789");

            verify(repository, times(1)).findByOrcid("0000-0001-2345-6789");
        }
    }

    @Nested
    @DisplayName("findByOpenalexAuthorId Tests")
    class FindByOpenalexAuthorIdTests {

        @Test
        @DisplayName("Deve retornar docente quando OpenAlex Author ID existe")
        void shouldReturnDocenteWhenOpenalexAuthorIdExists() {
            // Arrange
            when(repository.findByOpenalexAuthorId("A1234567890")).thenReturn(Optional.of(mockEntity));
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<DocenteResponse> result = service.findByOpenalexAuthorId("A1234567890");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getOpenalexAuthorId()).isEqualTo("A1234567890");

            verify(repository, times(1)).findByOpenalexAuthorId("A1234567890");
        }
    }

    @Nested
    @DisplayName("findByInstituicao Tests")
    class FindByInstituicaoTests {

        @Test
        @DisplayName("Deve retornar docentes da instituição")
        void shouldReturnDocentesByInstitution() {
            // Arrange
            List<DocenteEntity> entities = Arrays.asList(mockEntity);
            List<DocenteResponse> responses = Arrays.asList(mockResponse);

            when(repository.findByInstituicaoId(1L)).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<DocenteResponse> result = service.findByInstituicao(1L);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getInstituicaoId()).isEqualTo(1L);

            verify(repository, times(1)).findByInstituicaoId(1L);
        }
    }

    @Nested
    @DisplayName("findAtivosByInstituicao Tests")
    class FindAtivosByInstituicaoTests {

        @Test
        @DisplayName("Deve retornar apenas docentes ativos da instituição")
        void shouldReturnOnlyActiveDocentesByInstitution() {
            // Arrange
            List<DocenteEntity> entities = Arrays.asList(mockEntity);
            List<DocenteResponse> responses = Arrays.asList(mockResponse);

            when(repository.findDocentesAtivosByInstituicao(1L)).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<DocenteResponse> result = service.findAtivosByInstituicao(1L);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(doc -> doc.getAtivo() == true);
            assertThat(result.get(0).getInstituicaoId()).isEqualTo(1L);

            verify(repository, times(1)).findDocentesAtivosByInstituicao(1L);
        }
    }

    @Nested
    @DisplayName("findDocentesNeedingSync Tests")
    class FindDocentesNeedingSyncTests {

        @Test
        @DisplayName("Deve retornar docentes que precisam de sincronização")
        void shouldReturnDocentesNeedingSync() {
            // Arrange
            List<DocenteEntity> entities = Arrays.asList(mockEntity);
            List<DocenteResponse> responses = Arrays.asList(mockResponse);

            when(repository.findDocentesNeedingSync(any(LocalDateTime.class))).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<DocenteResponse> result = service.findDocentesNeedingSync(30);

            // Assert
            assertThat(result).isNotEmpty();
            verify(repository, times(1)).findDocentesNeedingSync(any(LocalDateTime.class));
        }
    }

    @Nested
    @DisplayName("findDocentesSemOpenAlexId Tests")
    class FindDocentesSemOpenAlexIdTests {

        @Test
        @DisplayName("Deve retornar docentes sem OpenAlex ID")
        void shouldReturnDocentesWithoutOpenAlexId() {
            // Arrange
            DocenteEntity entitySemOpenAlex = DocenteEntity.builder()
                    .id(2L)
                    .nomeCompleto("Prof. Maria Souza")
                    .build();

            List<DocenteEntity> entities = Arrays.asList(entitySemOpenAlex);
            List<DocenteResponse> responses = Arrays.asList(DocenteResponse.builder()
                    .id(2L)
                    .nomeCompleto("Prof. Maria Souza")
                    .openalexAuthorId(null)
                    .build());

            when(repository.findDocentesSemOpenAlexId()).thenReturn(entities);
            when(mapper.toResponseList(entities)).thenReturn(responses);

            // Act
            List<DocenteResponse> result = service.findDocentesSemOpenAlexId();

            // Assert
            assertThat(result).isNotEmpty();
            verify(repository, times(1)).findDocentesSemOpenAlexId();
        }
    }

    @Nested
    @DisplayName("create Tests")
    class CreateTests {

        @Test
        @DisplayName("Deve criar docente com sucesso")
        void shouldCreateDocenteSuccessfully() {
            // Arrange
            when(instituicaoRepository.findById(1L)).thenReturn(Optional.of(mockInstituicao));
            when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());
            when(repository.findByLattesId("1234567890123456")).thenReturn(Optional.empty());
            when(repository.findByOrcid("0000-0001-2345-6789")).thenReturn(Optional.empty());
            when(mapper.toEntity(mockRequest)).thenReturn(mockEntity);
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            DocenteResponse result = service.create(mockRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getNomeCompleto()).isEqualTo("Dr. João Silva Santos");
            assertThat(result.getCpf()).isEqualTo("12345678901");

            verify(instituicaoRepository, times(1)).findById(1L);
            verify(repository, times(1)).findByCpf("12345678901");
            verify(repository, times(1)).findByLattesId("1234567890123456");
            verify(repository, times(1)).findByOrcid("0000-0001-2345-6789");
            verify(repository, times(1)).save(mockEntity);
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
        @DisplayName("Deve lançar exceção quando CPF já existe")
        void shouldThrowExceptionWhenCpfExists() {
            // Arrange
            when(instituicaoRepository.findById(1L)).thenReturn(Optional.of(mockInstituicao));
            when(repository.findByCpf("12345678901")).thenReturn(Optional.of(mockEntity));

            // Act & Assert
            assertThatThrownBy(() -> service.create(mockRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF");

            verify(repository, times(1)).findByCpf("12345678901");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando Lattes ID já existe")
        void shouldThrowExceptionWhenLattesIdExists() {
            // Arrange
            when(instituicaoRepository.findById(1L)).thenReturn(Optional.of(mockInstituicao));
            when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());
            when(repository.findByLattesId("1234567890123456")).thenReturn(Optional.of(mockEntity));

            // Act & Assert
            assertThatThrownBy(() -> service.create(mockRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Lattes ID");

            verify(repository, times(1)).findByLattesId("1234567890123456");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando ORCID já existe")
        void shouldThrowExceptionWhenOrcidExists() {
            // Arrange
            when(instituicaoRepository.findById(1L)).thenReturn(Optional.of(mockInstituicao));
            when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());
            when(repository.findByLattesId("1234567890123456")).thenReturn(Optional.empty());
            when(repository.findByOrcid("0000-0001-2345-6789")).thenReturn(Optional.of(mockEntity));

            // Act & Assert
            assertThatThrownBy(() -> service.create(mockRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ORCID");

            verify(repository, times(1)).findByOrcid("0000-0001-2345-6789");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve criar docente sem validar campos null")
        void shouldCreateDocenteWithoutValidatingNullFields() {
            // Arrange
            mockRequest.setCpf(null);
            mockRequest.setLattesId(null);
            mockRequest.setOrcid(null);

            when(instituicaoRepository.findById(1L)).thenReturn(Optional.of(mockInstituicao));
            when(mapper.toEntity(mockRequest)).thenReturn(mockEntity);
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            DocenteResponse result = service.create(mockRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(repository, never()).findByCpf(any());
            verify(repository, never()).findByLattesId(any());
            verify(repository, never()).findByOrcid(any());
            verify(repository, times(1)).save(mockEntity);
        }
    }

    @Nested
    @DisplayName("update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar docente com sucesso")
        void shouldUpdateDocenteSuccessfully() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<DocenteResponse> result = service.update(1L, mockRequest);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);

            verify(repository, times(1)).findById(1L);
            verify(mapper, times(1)).updateEntityFromRequest(mockRequest, mockEntity);
            verify(repository, times(1)).save(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando docente não existe")
        void shouldReturnEmptyWhenDocenteNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<DocenteResponse> result = service.update(999L, mockRequest);

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
        @DisplayName("Deve deletar docente com sucesso")
        void shouldDeleteDocenteSuccessfully() {
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
        @DisplayName("Deve retornar false quando docente não existe")
        void shouldReturnFalseWhenDocenteNotExists() {
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
        @DisplayName("Deve desativar docente com sucesso")
        void shouldDeactivateDocenteSuccessfully() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
            when(repository.save(mockEntity)).thenReturn(mockEntity);
            when(mapper.toResponse(mockEntity)).thenReturn(mockResponse);

            // Act
            Optional<DocenteResponse> result = service.desativar(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(mockEntity.getAtivo()).isFalse();
            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).save(mockEntity);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando docente não existe")
        void shouldReturnEmptyWhenDocenteNotExists() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<DocenteResponse> result = service.desativar(999L);

            // Assert
            assertThat(result).isEmpty();
            verify(repository, times(1)).findById(999L);
            verify(repository, never()).save(any());
        }
    }
}
