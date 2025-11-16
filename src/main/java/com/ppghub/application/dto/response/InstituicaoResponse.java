package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Instituição.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstituicaoResponse {

    private Long id;
    private String nome;
    private String sigla;
    private String tipo;
    private String categoria;
    private String cnpj;

    // Endereço
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String pais;

    // Contato
    private String telefone;
    private String email;
    private String website;

    // OpenAlex Integration
    private String openalexInstitutionId;
    private String rorId;
    private String openalexDisplayName;
    private LocalDateTime openalexLastSyncAt;
    private Integer openalexWorksCount;
    private Integer openalexCitedByCount;

    // Status
    private Boolean ativo;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
