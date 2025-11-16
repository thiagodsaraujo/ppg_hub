package com.ppghub.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppghub.IntegrationTestBase;
import com.ppghub.application.dto.request.InstituicaoCreateRequest;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.repository.JpaInstituicaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração API para InstituicaoController.
 * Testa endpoints REST com contexto Spring completo e banco de dados real via TestContainers.
 */
@AutoConfigureMockMvc
@DisplayName("InstituicaoController API Integration Tests")
class InstituicaoControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaInstituicaoRepository repository;

    private InstituicaoEntity unifesp;
    private InstituicaoEntity usp;

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
                .build();

        repository.save(unifesp);
        repository.save(usp);
    }

    @Nested
    @DisplayName("GET /v1/instituicoes")
    class ListarTodasTests {

        @Test
        @DisplayName("Deve retornar lista de todas as instituições")
        void shouldReturnAllInstitutions() throws Exception {
            mockMvc.perform(get("/v1/instituicoes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].sigla", anyOf(is("UNIFESP"), is("USP"))))
                    .andExpect(jsonPath("$[1].sigla", anyOf(is("UNIFESP"), is("USP"))));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há instituições")
        void shouldReturnEmptyListWhenNoInstitutions() throws Exception {
            // Arrange
            repository.deleteAll();

            // Act & Assert
            mockMvc.perform(get("/v1/instituicoes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /v1/instituicoes/{id}")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve retornar instituição quando ID existe")
        void shouldReturnInstitutionWhenIdExists() throws Exception {
            mockMvc.perform(get("/v1/instituicoes/{id}", unifesp.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(unifesp.getId().intValue())))
                    .andExpect(jsonPath("$.nome", is("Universidade Federal de São Paulo")))
                    .andExpect(jsonPath("$.sigla", is("UNIFESP")))
                    .andExpect(jsonPath("$.cnpj", is("60428831000155")))
                    .andExpect(jsonPath("$.tipo", is("PUBLICA")))
                    .andExpect(jsonPath("$.cidade", is("São Paulo")))
                    .andExpect(jsonPath("$.estado", is("SP")))
                    .andExpect(jsonPath("$.ativo", is(true)));
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturn404WhenIdNotExists() throws Exception {
            mockMvc.perform(get("/v1/instituicoes/{id}", 99999L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /v1/instituicoes/sigla/{sigla}")
    class BuscarPorSiglaTests {

        @Test
        @DisplayName("Deve retornar instituição quando sigla existe")
        void shouldReturnInstitutionWhenSiglaExists() throws Exception {
            mockMvc.perform(get("/v1/instituicoes/sigla/{sigla}", "UNIFESP"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.sigla", is("UNIFESP")))
                    .andExpect(jsonPath("$.nome", is("Universidade Federal de São Paulo")));
        }

        @Test
        @DisplayName("Deve retornar 404 quando sigla não existe")
        void shouldReturn404WhenSiglaNotExists() throws Exception {
            mockMvc.perform(get("/v1/instituicoes/sigla/{sigla}", "INEXISTENTE"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /v1/instituicoes/openalex/{openalexId}")
    class BuscarPorOpenAlexIdTests {

        @Test
        @DisplayName("Deve retornar instituição quando OpenAlex ID existe")
        void shouldReturnInstitutionWhenOpenAlexIdExists() throws Exception {
            mockMvc.perform(get("/v1/instituicoes/openalex/{openalexId}", "I123456789"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.openalexInstitutionId", is("I123456789")))
                    .andExpect(jsonPath("$.sigla", is("UNIFESP")));
        }

        @Test
        @DisplayName("Deve retornar 404 quando OpenAlex ID não existe")
        void shouldReturn404WhenOpenAlexIdNotExists() throws Exception {
            mockMvc.perform(get("/v1/instituicoes/openalex/{openalexId}", "INVALID"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /v1/instituicoes/ror/{rorId}")
    class BuscarPorRorIdTests {

        @Test
        @DisplayName("Deve retornar instituição quando ROR ID existe")
        void shouldReturnInstitutionWhenRorIdExists() throws Exception {
            mockMvc.perform(get("/v1/instituicoes/ror/{rorId}", "https://ror.org/04wffgt70"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.rorId", is("https://ror.org/04wffgt70")))
                    .andExpect(jsonPath("$.sigla", is("UNIFESP")));
        }
    }

    @Nested
    @DisplayName("GET /v1/instituicoes/search")
    class BuscarPorNomeTests {

        @Test
        @DisplayName("Deve buscar instituições por nome parcial")
        void shouldSearchInstitutionsByPartialName() throws Exception {
            mockMvc.perform(get("/v1/instituicoes/search")
                            .param("nome", "Federal"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].sigla", is("UNIFESP")));
        }

        @Test
        @DisplayName("Deve respeitar paginação")
        void shouldRespectPagination() throws Exception {
            mockMvc.perform(get("/v1/instituicoes/search")
                            .param("nome", "São Paulo")
                            .param("page", "0")
                            .param("size", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(2)));
        }
    }

    @Nested
    @DisplayName("GET /v1/instituicoes/ativas")
    class ListarAtivasTests {

        @Test
        @DisplayName("Deve retornar apenas instituições ativas")
        void shouldReturnOnlyActiveInstitutions() throws Exception {
            // Arrange - Adicionar uma instituição inativa
            InstituicaoEntity inativa = repository.save(
                    InstituicaoEntity.builder()
                            .nome("Instituição Inativa")
                            .sigla("INATIVA")
                            .tipo("PRIVADA")
                            .cidade("São Paulo")
                            .estado("SP")
                            .ativo(false)
                            .build()
            );

            // Act & Assert
            mockMvc.perform(get("/v1/instituicoes/ativas"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].ativo", everyItem(is(true))))
                    .andExpect(jsonPath("$[*].sigla", not(hasItem("INATIVA"))));
        }
    }

    @Nested
    @DisplayName("POST /v1/instituicoes")
    class CriarTests {

        @Test
        @DisplayName("Deve criar nova instituição com sucesso")
        void shouldCreateInstitutionSuccessfully() throws Exception {
            // Arrange
            InstituicaoCreateRequest request = InstituicaoCreateRequest.builder()
                    .nome("Pontifícia Universidade Católica de São Paulo")
                    .sigla("PUC-SP")
                    .cnpj("60897557000198")
                    .tipo("PRIVADA")
                    .cidade("São Paulo")
                    .estado("SP")
                    .pais("Brasil")
                    .build();

            // Act & Assert
            mockMvc.perform(post("/v1/instituicoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.nome", is("Pontifícia Universidade Católica de São Paulo")))
                    .andExpect(jsonPath("$.sigla", is("PUC-SP")))
                    .andExpect(jsonPath("$.cnpj", is("60897557000198")))
                    .andExpect(jsonPath("$.tipo", is("PRIVADA")));
        }

        @Test
        @DisplayName("Deve retornar 400 quando dados inválidos")
        void shouldReturn400WhenInvalidData() throws Exception {
            // Arrange - Request sem nome (campo obrigatório)
            InstituicaoCreateRequest request = InstituicaoCreateRequest.builder()
                    .sigla("INVALID")
                    .tipo("PUBLICA")
                    .build();

            // Act & Assert
            mockMvc.perform(post("/v1/instituicoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 409 quando CNPJ já existe")
        void shouldReturn409WhenCnpjExists() throws Exception {
            // Arrange - Usar mesmo CNPJ da UNIFESP
            InstituicaoCreateRequest request = InstituicaoCreateRequest.builder()
                    .nome("Nova Instituição")
                    .sigla("NOVA")
                    .cnpj("60428831000155") // CNPJ da UNIFESP
                    .tipo("PRIVADA")
                    .cidade("São Paulo")
                    .estado("SP")
                    .build();

            // Act & Assert
            mockMvc.perform(post("/v1/instituicoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("CNPJ")));
        }

        @Test
        @DisplayName("Deve retornar 409 quando sigla já existe")
        void shouldReturn409WhenSiglaExists() throws Exception {
            // Arrange - Usar mesma sigla da UNIFESP
            InstituicaoCreateRequest request = InstituicaoCreateRequest.builder()
                    .nome("Nova Instituição")
                    .sigla("UNIFESP") // Sigla já existe
                    .tipo("PRIVADA")
                    .cidade("São Paulo")
                    .estado("SP")
                    .build();

            // Act & Assert
            mockMvc.perform(post("/v1/instituicoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("sigla")));
        }
    }

    @Nested
    @DisplayName("PUT /v1/instituicoes/{id}")
    class AtualizarTests {

        @Test
        @DisplayName("Deve atualizar instituição com sucesso")
        void shouldUpdateInstitutionSuccessfully() throws Exception {
            // Arrange
            InstituicaoCreateRequest request = InstituicaoCreateRequest.builder()
                    .nome("Universidade Federal de São Paulo - ATUALIZADA")
                    .sigla("UNIFESP")
                    .cnpj("60428831000155")
                    .tipo("PUBLICA")
                    .cidade("São Paulo")
                    .estado("SP")
                    .pais("Brasil")
                    .build();

            // Act & Assert
            mockMvc.perform(put("/v1/instituicoes/{id}", unifesp.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(unifesp.getId().intValue())))
                    .andExpect(jsonPath("$.nome", is("Universidade Federal de São Paulo - ATUALIZADA")));
        }

        @Test
        @DisplayName("Deve retornar 404 quando instituição não existe")
        void shouldReturn404WhenInstitutionNotExists() throws Exception {
            // Arrange
            InstituicaoCreateRequest request = InstituicaoCreateRequest.builder()
                    .nome("Instituição Inexistente")
                    .sigla("INEX")
                    .tipo("PUBLICA")
                    .build();

            // Act & Assert
            mockMvc.perform(put("/v1/instituicoes/{id}", 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /v1/instituicoes/{id}")
    class DeletarTests {

        @Test
        @DisplayName("Deve deletar instituição com sucesso")
        void shouldDeleteInstitutionSuccessfully() throws Exception {
            mockMvc.perform(delete("/v1/instituicoes/{id}", unifesp.getId()))
                    .andExpect(status().isNoContent());

            // Verificar que foi deletada
            mockMvc.perform(get("/v1/instituicoes/{id}", unifesp.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 404 quando instituição não existe")
        void shouldReturn404WhenInstitutionNotExists() throws Exception {
            mockMvc.perform(delete("/v1/instituicoes/{id}", 99999L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /v1/instituicoes/{id}/desativar")
    class DesativarTests {

        @Test
        @DisplayName("Deve desativar instituição com sucesso")
        void shouldDeactivateInstitutionSuccessfully() throws Exception {
            mockMvc.perform(patch("/v1/instituicoes/{id}/desativar", unifesp.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(unifesp.getId().intValue())))
                    .andExpect(jsonPath("$.ativo", is(false)));
        }

        @Test
        @DisplayName("Deve retornar 404 quando instituição não existe")
        void shouldReturn404WhenInstitutionNotExists() throws Exception {
            mockMvc.perform(patch("/v1/instituicoes/{id}/desativar", 99999L))
                    .andExpect(status().isNotFound());
        }
    }
}
