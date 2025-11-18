package br.edu.ppg.hub.academic.application.dto.trabalho_conclusao;

import br.edu.ppg.hub.academic.domain.enums.TipoTrabalho;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para criação de um trabalho de conclusão.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrabalhoConclusaoCreateDTO {

    @NotNull(message = "O ID do discente é obrigatório")
    private Long discenteId;

    @NotNull(message = "O ID do orientador é obrigatório")
    private Long orientadorId;

    private Long coorientadorId;

    @NotNull(message = "O tipo do trabalho é obrigatório")
    private TipoTrabalho tipo;

    @NotBlank(message = "O título em português é obrigatório")
    @Size(max = 1000, message = "O título não pode exceder 1000 caracteres")
    private String tituloPortugues;

    @Size(max = 1000, message = "O título em inglês não pode exceder 1000 caracteres")
    private String tituloIngles;

    @Size(max = 500, message = "O subtítulo não pode exceder 500 caracteres")
    private String subtitulo;

    @NotBlank(message = "O resumo em português é obrigatório")
    @Size(max = 5000, message = "O resumo não pode exceder 5000 caracteres")
    private String resumoPortugues;

    @Size(max = 5000, message = "O resumo em inglês não pode exceder 5000 caracteres")
    private String resumoIngles;

    @Size(max = 5000, message = "O abstract não pode exceder 5000 caracteres")
    private String abstractText;

    @Size(max = 500, message = "As palavras-chave não podem exceder 500 caracteres")
    private String palavrasChavePortugues;

    @Size(max = 500, message = "As palavras-chave em inglês não podem exceder 500 caracteres")
    private String palavrasChaveIngles;

    @Size(max = 500, message = "As keywords não podem exceder 500 caracteres")
    private String keywords;

    @Size(max = 200, message = "A área CNPq não pode exceder 200 caracteres")
    private String areaCnpq;

    @Size(max = 200, message = "A área de concentração não pode exceder 200 caracteres")
    private String areaConcentracao;

    @Size(max = 200, message = "A linha de pesquisa não pode exceder 200 caracteres")
    private String linhaPesquisa;

    private LocalDate dataDefesa;

    @Min(value = 1900, message = "O ano de defesa deve ser maior que 1900")
    @Max(value = 2100, message = "O ano de defesa deve ser menor que 2100")
    private Integer anoDefesa;

    @Min(value = 1, message = "O semestre deve ser 1 ou 2")
    @Max(value = 2, message = "O semestre deve ser 1 ou 2")
    private Integer semestreDefesa;

    @Size(max = 500, message = "O local da defesa não pode exceder 500 caracteres")
    private String localDefesa;

    @Min(value = 1, message = "O número de páginas deve ser maior que zero")
    private Integer numeroPaginas;

    @Pattern(regexp = "^[a-z]{2}$", message = "O idioma deve ter 2 letras minúsculas (ISO 639-1)")
    private String idioma;

    @Pattern(regexp = "^10\\.\\d{4,}/.+$", message = "DOI inválido")
    private String doi;

    private String isbn;

    @Size(max = 2000, message = "Os prêmios e reconhecimentos não podem exceder 2000 caracteres")
    private String premiosReconhecimentos;
}
