package com.ppghub.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de Professor Externo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorExternoUpdateRequest {

    @Size(max = 255)
    private String nome;

    @Email(message = "Email inválido")
    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String instituicaoOrigem;

    @Size(max = 100)
    private String titulacao;

    @Size(max = 255)
    private String especialidade;

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

    private String observacoes;

    private Boolean validado;

    private Boolean ativo;
}
