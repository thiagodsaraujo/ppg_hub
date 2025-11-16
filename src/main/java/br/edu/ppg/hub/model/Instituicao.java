package br.edu.ppg.hub.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entidade JPA para Instituição de Ensino.
 *
 * Representa uma instituição de ensino superior vinculada a programas de pós-graduação.
 * Contém informações cadastrais, contatos, endereço e configurações.
 *
 * @author PPG Team
 * @version 0.1.0
 */
@Entity
@Table(name = "instituicoes",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_instituicao_codigo", columnNames = "codigo"),
           @UniqueConstraint(name = "uk_instituicao_cnpj", columnNames = "cnpj")
       },
       indexes = {
           @Index(name = "idx_instituicao_tipo", columnList = "tipo"),
           @Index(name = "idx_instituicao_ativo", columnList = "ativo"),
           @Index(name = "idx_instituicao_nome_abreviado", columnList = "nome_abreviado")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"configuracoes", "endereco", "contatos", "redesSociais"})
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código único da instituição (ex: UEPB, UFCG)
     */
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Código é obrigatório")
    @Size(min = 2, max = 20, message = "Código deve ter entre 2 e 20 caracteres")
    private String codigo;

    /**
     * Nome oficial completo da instituição
     */
    @Column(name = "nome_completo", nullable = false, length = 500)
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 5, max = 500, message = "Nome completo deve ter entre 5 e 500 caracteres")
    private String nomeCompleto;

    /**
     * Nome abreviado para exibição
     */
    @Column(name = "nome_abreviado", nullable = false, length = 50)
    @NotBlank(message = "Nome abreviado é obrigatório")
    @Size(min = 2, max = 50, message = "Nome abreviado deve ter entre 2 e 50 caracteres")
    private String nomeAbreviado;

    /**
     * Sigla oficial da instituição
     */
    @Column(name = "sigla", nullable = false, length = 10)
    @NotBlank(message = "Sigla é obrigatória")
    @Size(min = 2, max = 10, message = "Sigla deve ter entre 2 e 10 caracteres")
    private String sigla;

    /**
     * Tipo da instituição (Federal, Estadual, Municipal, Privada)
     */
    @Column(name = "tipo", nullable = false, length = 20)
    @NotBlank(message = "Tipo é obrigatório")
    private String tipo;

    /**
     * CNPJ da instituição
     */
    @Column(name = "cnpj", unique = true, length = 18)
    private String cnpj;

    /**
     * Natureza jurídica detalhada
     */
    @Column(name = "natureza_juridica", length = 100)
    private String naturezaJuridica;

    /**
     * Dados de endereço em formato JSON
     */
    @Type(JsonType.class)
    @Column(name = "endereco", columnDefinition = "jsonb")
    private Map<String, Object> endereco;

    /**
     * Telefones e emails em formato JSON
     */
    @Type(JsonType.class)
    @Column(name = "contatos", columnDefinition = "jsonb")
    private Map<String, Object> contatos;

    /**
     * URLs das redes sociais
     */
    @Type(JsonType.class)
    @Column(name = "redes_sociais", columnDefinition = "jsonb")
    private Map<String, Object> redesSociais;

    /**
     * URL do logotipo da instituição
     */
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    /**
     * Site oficial da instituição
     */
    @Column(name = "website", length = 500)
    private String website;

    /**
     * Data de fundação da instituição
     */
    @Column(name = "fundacao")
    private LocalDateTime fundacao;

    /**
     * ID da instituição na base OpenAlex
     */
    @Column(name = "openalex_institution_id", length = 50)
    private String openalexInstitutionId;

    /**
     * Research Organization Registry ID
     */
    @Column(name = "ror_id", length = 100)
    private String rorId;

    /**
     * Se a instituição está ativa no sistema
     */
    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    /**
     * Configurações específicas da instituição em formato JSON
     */
    @Type(JsonType.class)
    @Column(name = "configuracoes", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> configuracoes = Map.of();

    /**
     * Data de criação do registro
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data da última atualização
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Callback executado antes da persistência para garantir valores padrão
     */
    @PrePersist
    protected void onCreate() {
        if (ativo == null) {
            ativo = true;
        }
        if (configuracoes == null) {
            configuracoes = Map.of();
        }
        // Converte código para maiúsculo
        if (codigo != null) {
            codigo = codigo.toUpperCase();
        }
    }

    /**
     * Callback executado antes da atualização
     */
    @PreUpdate
    protected void onUpdate() {
        // Converte código para maiúsculo se alterado
        if (codigo != null) {
            codigo = codigo.toUpperCase();
        }
    }

    /**
     * Retorna o endereço completo formatado
     */
    public String getEnderecoCompleto() {
        if (endereco == null || endereco.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        if (endereco.containsKey("logradouro")) {
            sb.append(endereco.get("logradouro"));
        }

        if (endereco.containsKey("cidade")) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(endereco.get("cidade"));
        }

        if (endereco.containsKey("uf")) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(endereco.get("uf"));
        }

        return sb.toString();
    }

    /**
     * Retorna o contato principal (email ou telefone)
     */
    public String getContatoPrincipal() {
        if (contatos == null || contatos.isEmpty()) {
            return "";
        }

        if (contatos.containsKey("email_principal")) {
            return (String) contatos.get("email_principal");
        }

        if (contatos.containsKey("telefone_principal")) {
            return (String) contatos.get("telefone_principal");
        }

        return "";
    }
}
