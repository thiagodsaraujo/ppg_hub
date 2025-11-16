package com.ppghub.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para criação de Programa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaCreateRequest {

    @NotNull(message = "ID da instituição é obrigatório")
    private Long instituicaoId;

    @NotBlank(message = "Nome do programa é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;

    @Size(max = 50)
    private String sigla;

    @Size(max = 20)
    private String codigoCapes;

    // Classificação
    @Size(max = 100)
    private String areaConhecimento;

    @Size(max = 100)
    private String areaAvaliacao;

    @Size(max = 50)
    private String modalidade; // ACADEMICO, PROFISSIONAL

    @Size(max = 50)
    private String nivel; // MESTRADO, DOUTORADO, MESTRADO_DOUTORADO

    // Avaliação CAPES
    @Min(value = 1, message = "Conceito CAPES deve ser entre 1 e 7")
    @Max(value = 7, message = "Conceito CAPES deve ser entre 1 e 7")
    private Integer conceitoCapes;

    private Integer anoAvaliacao;

    // Contato
    @Size(max = 255)
    private String coordenador;

    @Email(message = "Email inválido")
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String telefone;

    @Size(max = 500)
    private String website;

    // Status
    @Size(max = 50)
    private String status; // ATIVO, INATIVO, EM_IMPLANTACAO

    private LocalDate dataInicio;

    private LocalDate dataFim;
}
