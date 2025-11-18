package br.edu.ppg.hub.auth.infrastructure.repository;

import br.edu.ppg.hub.auth.domain.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para AuditLog.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Busca logs por usuário.
     */
    Page<AuditLog> findByUsuarioId(Long usuarioId, Pageable pageable);

    /**
     * Busca logs por ação.
     */
    Page<AuditLog> findByAcao(String acao, Pageable pageable);

    /**
     * Busca logs por entidade.
     */
    Page<AuditLog> findByEntidade(String entidade, Pageable pageable);

    /**
     * Busca logs por entidade e ID.
     */
    Page<AuditLog> findByEntidadeAndEntidadeId(String entidade, Long entidadeId, Pageable pageable);

    /**
     * Busca logs por período.
     */
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :inicio AND :fim ORDER BY a.createdAt DESC")
    Page<AuditLog> findByPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            Pageable pageable
    );

    /**
     * Busca logs recentes de um usuário.
     */
    @Query("SELECT a FROM AuditLog a WHERE a.usuario.id = :usuarioId ORDER BY a.createdAt DESC")
    Page<AuditLog> findRecentByUsuario(@Param("usuarioId") Long usuarioId, Pageable pageable);

    /**
     * Busca logs recentes de uma entidade.
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entidade = :entidade AND a.entidadeId = :entidadeId ORDER BY a.createdAt DESC")
    List<AuditLog> findRecentByEntidade(
            @Param("entidade") String entidade,
            @Param("entidadeId") Long entidadeId,
            Pageable pageable
    );

    /**
     * Busca logs de ações críticas (CREATE, UPDATE, DELETE).
     */
    @Query("SELECT a FROM AuditLog a WHERE a.acao IN ('CREATE', 'UPDATE', 'DELETE') ORDER BY a.createdAt DESC")
    Page<AuditLog> findCriticalActions(Pageable pageable);

    /**
     * Conta logs por usuário.
     */
    long countByUsuarioId(Long usuarioId);

    /**
     * Conta logs por ação.
     */
    long countByAcao(String acao);

    /**
     * Deleta logs antigos (para manutenção).
     */
    void deleteByCreatedAtBefore(LocalDateTime dataLimite);
}
