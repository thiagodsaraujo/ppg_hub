package com.ppghub.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para criação de Docente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocenteCreateRequest {

    @NotNull(message = "ID da instituição é obrigatório")
    private Long instituicaoId;

    // Dados Pessoais
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 255)
    private String nomeCompleto;

    @Size(max = 255)
    private String nomeCitacao;

    @Size(max = 14)
    private String cpf;

    @Email(message = "Email inválido")
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String telefone;

    // Identificadores Acadêmicos
    @Size(max = 50)
    private String lattesId;

    @Size(max = 19)
    private String orcid;

    @Size(max = 50)
    private String openalexAuthorId;

    @Size(max = 50)
    private String scopusId;

    @Size(max = 50)
    private String researcherId;

    // Formação
    @Size(max = 50)
    private String titulacao;

    @Size(max = 255)
    private String areaAtuacao;

    // Vínculo
    @Size(max = 50)
    private String tipoVinculo;

    @Size(max = 50)
    private String regimeTrabalho;

    private LocalDate dataIngresso;

    private LocalDate dataSaida;

    private Boolean ativo;
}
