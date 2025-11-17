package com.ppghub.application.dto.request;

import com.ppghub.domain.validation.ValidCPF;
import com.ppghub.domain.validation.ValidLattesId;
import com.ppghub.domain.validation.ValidORCID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para atualização de Discente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscenteUpdateRequest {

    @Size(max = 255)
    private String nome;

    @Email(message = "Email inválido")
    @Size(max = 255)
    private String email;

    @ValidCPF
    @Size(max = 14)
    private String cpf;

    @Size(max = 20)
    private String telefone;

    private LocalDate dataNascimento;

    private Long orientadorId;

    private LocalDate dataIngresso;

    private LocalDate dataConclusao;

    private String statusMatricula; // ATIVO, INATIVO, EGRESSO, TRANCADO, DESLIGADO

    @Size(max = 500)
    private String tituloTese;

    private LocalDate dataDefesa;

    // Identificadores Acadêmicos
    @ValidLattesId
    @Size(max = 50)
    private String lattesId;

    @ValidORCID
    @Size(max = 19)
    private String orcid;

    @Size(max = 50)
    private String openalexAuthorId;
}
