package br.edu.ppg.hub.auth.infrastructure.repository;

import br.edu.ppg.hub.auth.domain.enums.StatusVinculacao;
import br.edu.ppg.hub.auth.domain.model.UsuarioProgramaRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para UsuarioProgramaRole.
 */
@Repository
public interface UsuarioProgramaRoleRepository extends JpaRepository<UsuarioProgramaRole, Long> {

    /**
     * Busca vinculações por usuário.
     */
    Page<UsuarioProgramaRole> findByUsuarioId(Long usuarioId, Pageable pageable);

    /**
     * Busca vinculações ativas por usuário.
     */
    List<UsuarioProgramaRole> findByUsuarioIdAndStatus(Long usuarioId, StatusVinculacao status);

    /**
     * Busca vinculações por programa.
     */
    Page<UsuarioProgramaRole> findByProgramaId(Long programaId, Pageable pageable);

    /**
     * Busca vinculações ativas por programa.
     */
    List<UsuarioProgramaRole> findByProgramaIdAndStatus(Long programaId, StatusVinculacao status);

    /**
     * Busca vinculação específica de usuário, programa e role.
     */
    Optional<UsuarioProgramaRole> findByUsuarioIdAndProgramaIdAndRoleId(Long usuarioId, Long programaId, Long roleId);

    /**
     * Verifica se existe vinculação ativa.
     */
    boolean existsByUsuarioIdAndProgramaIdAndRoleIdAndStatus(Long usuarioId, Long programaId, Long roleId, StatusVinculacao status);

    /**
     * Busca vinculações por role.
     */
    List<UsuarioProgramaRole> findByRoleId(Long roleId);

    /**
     * Busca vinculações de um usuário em um programa (qualquer role).
     */
    List<UsuarioProgramaRole> findByUsuarioIdAndProgramaId(Long usuarioId, Long programaId);

    /**
     * Busca vinculações ativas e vigentes por usuário e programa.
     */
    @Query("SELECT upr FROM UsuarioProgramaRole upr WHERE upr.usuario.id = :usuarioId " +
           "AND upr.programa.id = :programaId " +
           "AND upr.status = 'ATIVO' " +
           "AND (upr.dataDesvinculacao IS NULL OR upr.dataDesvinculacao >= CURRENT_DATE)")
    List<UsuarioProgramaRole> findVinculacoesVigentesByUsuarioAndPrograma(
            @Param("usuarioId") Long usuarioId,
            @Param("programaId") Long programaId
    );

    /**
     * Verifica se usuário tem role específica em programa.
     */
    @Query("SELECT COUNT(upr) > 0 FROM UsuarioProgramaRole upr " +
           "WHERE upr.usuario.id = :usuarioId " +
           "AND upr.programa.id = :programaId " +
           "AND upr.role.nome = :nomeRole " +
           "AND upr.status = 'ATIVO' " +
           "AND (upr.dataDesvinculacao IS NULL OR upr.dataDesvinculacao >= CURRENT_DATE)")
    boolean usuarioTemRoleNoPrograma(
            @Param("usuarioId") Long usuarioId,
            @Param("programaId") Long programaId,
            @Param("nomeRole") String nomeRole
    );

    /**
     * Conta vinculações por programa.
     */
    long countByProgramaId(Long programaId);

    /**
     * Conta vinculações ativas por programa.
     */
    long countByProgramaIdAndStatus(Long programaId, StatusVinculacao status);
}
