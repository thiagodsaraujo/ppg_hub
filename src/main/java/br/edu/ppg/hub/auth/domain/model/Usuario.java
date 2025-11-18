package br.edu.ppg.hub.auth.domain.model;

import br.edu.ppg.hub.auth.domain.enums.StatusUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Entidade Usuario.
 *
 * Representa todos os usuários do sistema (docentes, discentes, coordenadores, etc).
 * Implementa UserDetails para integração com Spring Security.
 *
 * Tabela: auth.usuarios
 */
@Entity
@Table(name = "usuarios", schema = "auth")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    /**
     * ID único sequencial.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * UUID único para identificação externa.
     * Gerado automaticamente pelo banco de dados.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    /**
     * Nome completo do usuário.
     */
    @NotBlank(message = "Nome completo é obrigatório")
    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    /**
     * Nome preferido/social (opcional).
     */
    @Column(name = "nome_preferido", length = 100)
    private String nomePreferido;

    /**
     * Email principal (usado para login).
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Email alternativo (opcional).
     */
    @Email(message = "Email alternativo deve ser válido")
    @Column(name = "email_alternativo")
    private String emailAlternativo;

    /**
     * Telefone de contato.
     */
    @Column(length = 20)
    private String telefone;

    /**
     * CPF (único, obrigatório para brasileiros).
     */
    @Column(length = 14, unique = true)
    private String cpf;

    /**
     * RG (opcional).
     */
    @Column(length = 20)
    private String rg;

    /**
     * Passaporte (obrigatório para estrangeiros).
     */
    @Column(length = 50)
    private String passaporte;

    /**
     * Hash BCrypt da senha.
     * Nunca armazenar senha em texto plano!
     */
    @NotBlank(message = "Senha é obrigatória")
    @Column(name = "password_hash", nullable = false, length = 500)
    private String passwordHash;

    /**
     * Indica se o email foi verificado.
     */
    @Builder.Default
    @Column(name = "email_verificado", nullable = false)
    private Boolean emailVerificado = false;

    /**
     * Data/hora da verificação do email.
     */
    @Column(name = "email_verificado_em")
    private LocalDateTime emailVerificadoEm;

    /**
     * Data de nascimento.
     */
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    /**
     * Gênero (opcional).
     */
    @Column(length = 20)
    private String genero;

    /**
     * Nacionalidade.
     */
    @Builder.Default
    @Column(length = 50)
    private String nacionalidade = "Brasileira";

    /**
     * Naturalidade (cidade/estado de nascimento).
     */
    @Column(length = 100)
    private String naturalidade;

    /**
     * Endereço completo em formato JSONB.
     *
     * Exemplo:
     * {
     *   "logradouro": "Rua Exemplo",
     *   "numero": "123",
     *   "complemento": "Apto 45",
     *   "bairro": "Centro",
     *   "cidade": "Campina Grande",
     *   "estado": "PB",
     *   "cep": "58400-000"
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String endereco;

    /**
     * ORCID (Open Researcher and Contributor ID).
     * Identificador único para pesquisadores.
     */
    @Column(length = 100, unique = true)
    private String orcid;

    /**
     * ID do currículo Lattes.
     */
    @Column(name = "lattes_id", length = 100)
    private String lattesId;

    /**
     * ID do Google Scholar.
     */
    @Column(name = "google_scholar_id", length = 100)
    private String googleScholarId;

    /**
     * ID do ResearchGate.
     */
    @Column(name = "researchgate_id", length = 100)
    private String researchgateId;

    /**
     * Perfil do LinkedIn.
     */
    @Column(length = 100)
    private String linkedin;

    /**
     * ID do autor no OpenAlex.
     */
    @Column(name = "openalex_author_id")
    private String openalexAuthorId;

    /**
     * Data/hora da última sincronização com OpenAlex.
     */
    @Column(name = "ultimo_sync_openalex")
    private LocalDateTime ultimoSyncOpenalex;

    /**
     * Configurações do usuário em formato JSONB.
     *
     * Exemplo:
     * {
     *   "notifications": {
     *     "email": true,
     *     "push": false
     *   },
     *   "privacy": {
     *     "show_email": false,
     *     "show_phone": true
     *   }
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(columnDefinition = "jsonb")
    private String configuracoes = "{}";

    /**
     * Preferências do usuário em formato JSONB.
     *
     * Exemplo:
     * {
     *   "language": "pt-BR",
     *   "theme": "light",
     *   "timezone": "America/Sao_Paulo"
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(columnDefinition = "jsonb")
    private String preferencias = "{}";

    /**
     * URL do avatar do usuário.
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * Biografia/descrição do usuário.
     */
    @Column(columnDefinition = "TEXT")
    private String biografia;

    /**
     * Data/hora do último login.
     */
    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    /**
     * Contador de tentativas de login falhadas.
     * Bloqueio após 5 tentativas.
     */
    @Builder.Default
    @Column(name = "tentativas_login", nullable = false)
    private Integer tentativasLogin = 0;

    /**
     * Indica se a conta está bloqueada.
     */
    @Builder.Default
    @Column(name = "conta_bloqueada", nullable = false)
    private Boolean contaBloqueada = false;

    /**
     * Data/hora até quando a conta está bloqueada.
     */
    @Column(name = "bloqueada_ate")
    private LocalDateTime bloqueadaAte;

    /**
     * Token para reset de senha.
     */
    @Column(name = "reset_token")
    private String resetToken;

    /**
     * Data/hora de expiração do token de reset.
     */
    @Column(name = "reset_token_expira")
    private LocalDateTime resetTokenExpira;

    /**
     * Indica se o usuário está ativo.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    /**
     * Data de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data da última atualização.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * ID do usuário que criou este registro.
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * ID do usuário que fez a última atualização.
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    // ========================================
    // Implementação de UserDetails (Spring Security)
    // ========================================

    /**
     * Retorna as authorities (roles/permissões) do usuário.
     *
     * Por enquanto, retorna uma lista vazia.
     * Será implementado quando criarmos o relacionamento com roles.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: Implementar quando adicionar relacionamento com roles
        // Por enquanto, retorna ROLE_USER para todos
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Retorna a senha (hash) do usuário.
     */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /**
     * Retorna o username (email) do usuário.
     * No nosso sistema, usamos email como username.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indica se a conta não está expirada.
     * No nosso sistema, contas não expiram automaticamente.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se a conta não está bloqueada.
     * Verifica se contaBloqueada é false E se bloqueadaAte já passou.
     */
    @Override
    public boolean isAccountNonLocked() {
        if (contaBloqueada != null && contaBloqueada) {
            if (bloqueadaAte != null && bloqueadaAte.isAfter(LocalDateTime.now())) {
                return false; // Ainda bloqueada
            }
            // Bloqueio expirou, mas flag ainda está true (será limpa no próximo login)
            return bloqueadaAte == null || bloqueadaAte.isBefore(LocalDateTime.now());
        }
        return true;
    }

    /**
     * Indica se as credenciais não estão expiradas.
     * No nosso sistema, senhas não expiram automaticamente.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está habilitado.
     * Verifica se ativo é true.
     */
    @Override
    public boolean isEnabled() {
        return ativo != null && ativo;
    }

    // ========================================
    // Métodos auxiliares
    // ========================================

    /**
     * Retorna o nome a ser exibido (preferido ou completo).
     */
    @Transient
    public String getNomeExibicao() {
        return (nomePreferido != null && !nomePreferido.isBlank())
                ? nomePreferido
                : nomeCompleto;
    }

    /**
     * Verifica se o email foi verificado.
     */
    @Transient
    public boolean isEmailVerificado() {
        return emailVerificado != null && emailVerificado;
    }

    /**
     * Incrementa o contador de tentativas de login.
     */
    public void incrementarTentativasLogin() {
        this.tentativasLogin = (this.tentativasLogin == null ? 0 : this.tentativasLogin) + 1;
    }

    /**
     * Reseta o contador de tentativas de login.
     */
    public void resetarTentativasLogin() {
        this.tentativasLogin = 0;
        this.contaBloqueada = false;
        this.bloqueadaAte = null;
    }

    /**
     * Bloqueia a conta por um período específico.
     *
     * @param minutos Minutos de bloqueio
     */
    public void bloquearConta(int minutos) {
        this.contaBloqueada = true;
        this.bloqueadaAte = LocalDateTime.now().plusMinutes(minutos);
    }

    /**
     * Gera e armazena um token de reset de senha.
     *
     * @param token Token gerado
     * @param expiracaoMinutos Minutos até expiração
     */
    public void setResetToken(String token, int expiracaoMinutos) {
        this.resetToken = token;
        this.resetTokenExpira = LocalDateTime.now().plusMinutes(expiracaoMinutos);
    }

    /**
     * Verifica se o token de reset ainda é válido.
     */
    @Transient
    public boolean isResetTokenValido() {
        return resetToken != null
                && !resetToken.isBlank()
                && resetTokenExpira != null
                && resetTokenExpira.isAfter(LocalDateTime.now());
    }

    /**
     * Limpa o token de reset.
     */
    public void limparResetToken() {
        this.resetToken = null;
        this.resetTokenExpira = null;
    }
}
