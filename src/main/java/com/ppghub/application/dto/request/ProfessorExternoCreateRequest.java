package com.ppghub.application.dto.request;

import com.ppghub.domain.validation.ValidLattesId;
import com.ppghub.domain.validation.ValidORCID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de Professor Externo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorExternoCreateRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
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
    @ValidLattesId
    @Size(max = 50)
    private String lattesId;

    @ValidORCID
    @Size(max = 19)
    private String orcid;

    @Size(max = 50)
    private String openalexAuthorId;

    @Size(max = 50)
    private String scopusId;

    private String observacoes;

    private Boolean ativo;
}
