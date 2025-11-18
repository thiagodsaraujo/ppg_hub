package br.edu.ppg.hub.academic.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade que representa métricas acadêmicas de um docente em um determinado momento
 * Permite acompanhar a evolução histórica das métricas (OpenAlex, Lattes, etc)
 *
 * @author PPG Hub
 * @since 1.0
 */
@Entity
@Table(
    name = "metricas_docentes",
    schema = "academic",
    indexes = {
        @Index(name = "idx_metrica_docente", columnList = "docente_id"),
        @Index(name = "idx_metrica_data_coleta", columnList = "data_coleta")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricaDocente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Docente ao qual as métricas pertencem
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id", nullable = false, foreignKey = @ForeignKey(name = "fk_metrica_docente"))
    @NotNull(message = "Docente é obrigatório")
    private Docente docente;

    /**
     * Índice H (H-index) - medida de produtividade e impacto científico
     */
    @Column(name = "h_index")
    @Min(value = 0, message = "H-index não pode ser negativo")
    private Integer hIndex;

    /**
     * Total de publicações do docente
     */
    @Column(name = "total_publicacoes")
    @Min(value = 0, message = "Total de publicações não pode ser negativo")
    private Integer totalPublicacoes;

    /**
     * Total de citações recebidas
     */
    @Column(name = "total_citacoes")
    @Min(value = 0, message = "Total de citações não pode ser negativo")
    private Integer totalCitacoes;

    /**
     * Número de publicações nos últimos 5 anos (importante para CAPES)
     */
    @Column(name = "publicacoes_ultimos_5_anos")
    @Min(value = 0, message = "Publicações não pode ser negativo")
    private Integer publicacoesUltimos5Anos;

    /**
     * Fonte dos dados (OpenAlex, Scopus, Web of Science, Lattes, etc)
     */
    @Column(name = "fonte", length = 50)
    private String fonte;

    /**
     * Data e hora da coleta dos dados
     */
    @Column(name = "data_coleta", nullable = false)
    @Builder.Default
    private LocalDateTime dataColeta = LocalDateTime.now();

    // ===========================
    // Métodos auxiliares
    // ===========================

    /**
     * Calcula a média de citações por publicação
     */
    public double getMediaCitacoesPorPublicacao() {
        if (totalPublicacoes == null || totalPublicacoes == 0) {
            return 0.0;
        }
        return totalCitacoes != null ? (double) totalCitacoes / totalPublicacoes : 0.0;
    }

    /**
     * Verifica se o docente tem alta produtividade (H-index >= 10)
     */
    public boolean temAltaProdutividade() {
        return hIndex != null && hIndex >= 10;
    }

    /**
     * Verifica se atende aos critérios mínimos da CAPES
     * (pelo menos 3 publicações nos últimos 5 anos)
     */
    public boolean atendeMinimoCapes() {
        return publicacoesUltimos5Anos != null && publicacoesUltimos5Anos >= 3;
    }
}
