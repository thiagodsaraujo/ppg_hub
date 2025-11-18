package br.edu.ppg.hub.auth.infrastructure.repository;

import br.edu.ppg.hub.auth.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para acesso aos dados de Role.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca uma role pelo nome.
     *
     * @param nome Nome da role (ex: "ROLE_ADMIN")
     * @return Optional com a role encontrada
     */
    Optional<Role> findByNome(String nome);

    /**
     * Verifica se existe uma role com o nome especificado.
     *
     * @param nome Nome da role
     * @return true se existe
     */
    boolean existsByNome(String nome);

    /**
     * Busca todas as roles ativas.
     *
     * @return Lista de roles ativas
     */
    @Query("SELECT r FROM Role r WHERE r.ativo = true ORDER BY r.nivelAcesso DESC, r.nome ASC")
    List<Role> findAllAtivas();

    /**
     * Busca roles com nível de acesso maior ou igual ao especificado.
     *
     * @param nivelMinimo Nível mínimo
     * @return Lista de roles
     */
    @Query("SELECT r FROM Role r WHERE r.nivelAcesso >= :nivelMinimo AND r.ativo = true ORDER BY r.nivelAcesso DESC")
    List<Role> findByNivelAcessoGreaterThanEqual(Integer nivelMinimo);

    /**
     * Busca roles administrativas (nível >= 3).
     *
     * @return Lista de roles administrativas
     */
    @Query("SELECT r FROM Role r WHERE r.nivelAcesso >= 3 AND r.ativo = true ORDER BY r.nivelAcesso DESC")
    List<Role> findRolesAdministrativas();
}
