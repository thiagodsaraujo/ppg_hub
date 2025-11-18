package br.edu.ppg.hub.core.domain.model;

import br.edu.ppg.hub.auth.domain.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma Linha de Pesquisa de um Programa.
 *
 * Cada linha de pesquisa está vinculada a um programa específico
 * e representa uma área temática de pesquisa.
 *
 * Segue schema: core.linhas_pesquisa
 */
@Entity
@Table(
        name = "linhas_pesquisa",
        schema = "core",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"programa_id", "nome"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinhaPesquisa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Programa ao qual a linha de pesquisa pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    @NotNull(message = "Programa é obrigatório")
    private Programa programa;

    /**
     * Nome da linha de pesquisa.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String nome;

    /**
     * Descrição detalhada da linha de pesquisa.
     */
    @Column(columnDefinition = "TEXT")
    private String descricao;

    /**
     * Palavras-chave da linha de pesquisa (separadas por vírgula ou ponto-e-vírgula).
     */
    @Column(name = "palavras_chave", columnDefinition = "TEXT")
    private String palavrasChave;

    /**
     * Coordenador da linha de pesquisa (geralmente um docente).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordenador_id")
    private Usuario coordenador;

    /**
     * Indica se a linha de pesquisa está ativa.
     */
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ativa = true;

    /**
     * Data de criação do registro.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data da última atualização do registro.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Verifica se a linha de pesquisa está ativa.
     */
    public boolean isAtiva() {
        return Boolean.TRUE.equals(this.ativa);
    }

    /**
     * Ativa a linha de pesquisa.
     */
    public void ativar() {
        this.ativa = true;
    }

    /**
     * Desativa a linha de pesquisa.
     */
    public void desativar() {
        this.ativa = false;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
