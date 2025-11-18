package br.edu.ppg.hub.auth.infrastructure.repository;

import br.edu.ppg.hub.auth.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para acesso aos dados de Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário
     * @return Optional com o usuário encontrado
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca um usuário pelo UUID.
     *
     * @param uuid UUID do usuário
     * @return Optional com o usuário encontrado
     */
    Optional<Usuario> findByUuid(UUID uuid);

    /**
     * Busca um usuário pelo CPF.
     *
     * @param cpf CPF do usuário
     * @return Optional com o usuário encontrado
     */
    Optional<Usuario> findByCpf(String cpf);

    /**
     * Busca um usuário pelo ORCID.
     *
     * @param orcid ORCID do usuário
     * @return Optional com o usuário encontrado
     */
    Optional<Usuario> findByOrcid(String orcid);

    /**
     * Busca um usuário pelo token de reset de senha.
     *
     * @param resetToken Token de reset
     * @return Optional com o usuário encontrado
     */
    Optional<Usuario> findByResetToken(String resetToken);

    /**
     * Verifica se existe um usuário com o email especificado.
     *
     * @param email Email a verificar
     * @return true se existe
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se existe um usuário com o CPF especificado.
     *
     * @param cpf CPF a verificar
     * @return true se existe
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica se existe um usuário com o ORCID especificado.
     *
     * @param orcid ORCID a verificar
     * @return true se existe
     */
    boolean existsByOrcid(String orcid);

    /**
     * Busca todos os usuários ativos.
     *
     * @return Lista de usuários ativos
     */
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true ORDER BY u.nomeCompleto ASC")
    List<Usuario> findAllAtivos();

    /**
     * Busca usuários com email não verificado.
     *
     * @return Lista de usuários com email não verificado
     */
    @Query("SELECT u FROM Usuario u WHERE u.emailVerificado = false AND u.ativo = true")
    List<Usuario> findUsuariosComEmailNaoVerificado();

    /**
     * Busca usuários bloqueados.
     *
     * @return Lista de usuários bloqueados
     */
    @Query("SELECT u FROM Usuario u WHERE u.contaBloqueada = true")
    List<Usuario> findUsuariosBloqueados();

    /**
     * Busca usuários com bloqueio expirado.
     *
     * @param agora Data/hora atual
     * @return Lista de usuários com bloqueio expirado
     */
    @Query("SELECT u FROM Usuario u WHERE u.contaBloqueada = true AND u.bloqueadaAte IS NOT NULL AND u.bloqueadaAte < :agora")
    List<Usuario> findUsuariosComBloqueioExpirado(LocalDateTime agora);

    /**
     * Busca usuários pelo nome (busca parcial).
     *
     * @param nome Nome a buscar
     * @return Lista de usuários encontrados
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%')) OR LOWER(u.nomePreferido) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Usuario> findByNomeContaining(String nome);

    /**
     * Conta usuários ativos.
     *
     * @return Quantidade de usuários ativos
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.ativo = true")
    Long countAtivos();

    /**
     * Conta usuários com email verificado.
     *
     * @return Quantidade de usuários com email verificado
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.emailVerificado = true")
    Long countEmailVerificado();
}
