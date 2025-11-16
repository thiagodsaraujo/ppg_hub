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
 * DTO para criação de Discente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscenteCreateRequest {

    @NotBlank(message = "Matrícula é obrigatória")
    @Size(max = 50)
    private String matricula;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255)
    private String email;

    @Size(max = 14)
    private String cpf;

    @Size(max = 20)
    private String telefone;

    private LocalDate dataNascimento;

    @NotNull(message = "ID do programa é obrigatório")
    private Long programaId;

    private Long orientadorId;

    private LocalDate dataIngresso;

    @NotBlank(message = "Nível de formação é obrigatório")
    private String nivelFormacao; // MESTRADO, DOUTORADO, DOUTORADO_DIRETO

    private String statusMatricula; // ATIVO, INATIVO, EGRESSO, TRANCADO, DESLIGADO

    @Size(max = 500)
    private String tituloTese;

    // Identificadores Acadêmicos
    @Size(max = 50)
    private String lattesId;

    @Size(max = 19)
    private String orcid;

    @Size(max = 50)
    private String openalexAuthorId;
}
