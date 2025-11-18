package br.edu.ppg.hub.academic.application.dto.membro_banca;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de um membro de banca.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembroBancaCreateDTO {

    @NotNull(message = "O ID da banca é obrigatório")
    private Long bancaId;

    /**
     * ID do docente (obrigatório para membros internos, null para externos)
     */
    private Long docenteId;

    /**
     * Nome completo (obrigatório para membros externos)
     */
    @Size(max = 255, message = "O nome completo não pode exceder 255 caracteres")
    private String nomeCompleto;

    /**
     * Instituição (obrigatório para membros externos)
     */
    @Size(max = 255, message = "A instituição não pode exceder 255 caracteres")
    private String instituicao;

    /**
     * Titulação (ex: Doutor, Mestre, Pós-Doutor)
     */
    @Size(max = 100, message = "A titulação não pode exceder 100 caracteres")
    private String titulacao;

    /**
     * Email do membro
     */
    @Email(message = "Email inválido")
    @Size(max = 255, message = "O email não pode exceder 255 caracteres")
    private String email;

    /**
     * Resumo do currículo do membro (Lattes resumido)
     */
    @Size(max = 2000, message = "O currículo resumido não pode exceder 2000 caracteres")
    private String curriculoResumo;

    /**
     * Função do membro na banca
     * Valores: Presidente, Examinador_Interno, Examinador_Externo, Suplente
     */
    @NotBlank(message = "A função é obrigatória")
    @Pattern(regexp = "^(Presidente|Examinador_Interno|Examinador_Externo|Suplente)$", message = "Função inválida")
    private String funcao;

    /**
     * Tipo do membro (Interno ou Externo)
     */
    @NotBlank(message = "O tipo é obrigatório")
    @Pattern(regexp = "^(Interno|Externo)$", message = "Tipo inválido")
    private String tipo;

    /**
     * Ordem de apresentação do membro na banca (para arguição)
     */
    @Min(value = 1, message = "A ordem de apresentação deve ser maior que zero")
    private Integer ordemApresentacao;
}
