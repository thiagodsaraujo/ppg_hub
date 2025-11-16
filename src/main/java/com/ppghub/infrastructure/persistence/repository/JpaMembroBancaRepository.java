package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.MembroBancaEntity;
import com.ppghub.infrastructure.persistence.entity.MembroBancaEntity.StatusConvite;
import com.ppghub.infrastructure.persistence.entity.MembroBancaEntity.TipoMembro;
import com.ppghub.infrastructure.persistence.entity.MembroBancaEntity.Funcao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para MembroBanca.
 */
@Repository
public interface JpaMembroBancaRepository extends JpaRepository<MembroBancaEntity, Long> {

    /**
     * Busca membros de uma banca específica
     */
    List<MembroBancaEntity> findByBancaId(Long bancaId);

    /**
     * Busca membros de uma banca ordenados por ordem de apresentação
     */
    @Query("SELECT m FROM MembroBancaEntity m WHERE m.banca.id = :bancaId ORDER BY m.ordemApresentacao")
    List<MembroBancaEntity> findByBancaIdOrderByOrdem(@Param("bancaId") Long bancaId);

    /**
     * Busca membros titulares de uma banca
     */
    @Query("SELECT m FROM MembroBancaEntity m WHERE m.banca.id = :bancaId AND m.tipoMembro = 'TITULAR'")
    List<MembroBancaEntity> findTitularesByBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros suplentes de uma banca
     */
    @Query("SELECT m FROM MembroBancaEntity m WHERE m.banca.id = :bancaId AND m.tipoMembro = 'SUPLENTE'")
    List<MembroBancaEntity> findSuplentesByBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca participações de um docente em bancas
     */
    List<MembroBancaEntity> findByDocenteId(Long docenteId);

    /**
     * Busca participações de um professor externo em bancas
     */
    List<MembroBancaEntity> findByProfessorExternoId(Long professorExternoId);

    /**
     * Busca membros por status de convite
     */
    List<MembroBancaEntity> findByStatusConvite(StatusConvite statusConvite);

    /**
     * Busca membros de banca com convites pendentes
     */
    @Query("SELECT m FROM MembroBancaEntity m WHERE m.banca.id = :bancaId " +
           "AND m.statusConvite IN ('PENDENTE', 'ENVIADO')")
    List<MembroBancaEntity> findConvitesPendentesByBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros confirmados de uma banca
     */
    @Query("SELECT m FROM MembroBancaEntity m WHERE m.banca.id = :bancaId " +
           "AND m.statusConvite = 'CONFIRMADO'")
    List<MembroBancaEntity> findMembrosConfirmadosByBanca(@Param("bancaId") Long bancaId);

    /**
     * Conta membros titulares de uma banca
     */
    @Query("SELECT COUNT(m) FROM MembroBancaEntity m WHERE m.banca.id = :bancaId " +
           "AND m.tipoMembro = 'TITULAR'")
    Long countTitularesByBanca(@Param("bancaId") Long bancaId);

    /**
     * Conta membros externos de uma banca
     */
    @Query("SELECT COUNT(m) FROM MembroBancaEntity m WHERE m.banca.id = :bancaId " +
           "AND m.professorExterno IS NOT NULL")
    Long countExternosByBanca(@Param("bancaId") Long bancaId);

    /**
     * Verifica se um docente já está na banca
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MembroBancaEntity m " +
           "WHERE m.banca.id = :bancaId AND m.docente.id = :docenteId")
    boolean existsByBancaAndDocente(
        @Param("bancaId") Long bancaId,
        @Param("docenteId") Long docenteId
    );

    /**
     * Verifica se um professor externo já está na banca
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MembroBancaEntity m " +
           "WHERE m.banca.id = :bancaId AND m.professorExterno.id = :professorExternoId")
    boolean existsByBancaAndProfessorExterno(
        @Param("bancaId") Long bancaId,
        @Param("professorExternoId") Long professorExternoId
    );

    /**
     * Busca membro presidente de uma banca
     */
    @Query("SELECT m FROM MembroBancaEntity m WHERE m.banca.id = :bancaId AND m.funcao = 'PRESIDENTE'")
    Optional<MembroBancaEntity> findPresidenteByBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca bancas futuras de um docente
     */
    @Query("SELECT m FROM MembroBancaEntity m " +
           "WHERE m.docente.id = :docenteId " +
           "AND m.banca.dataHora >= :dataAtual " +
           "AND m.banca.statusBanca IN ('AGENDADA', 'CONFIRMADA') " +
           "ORDER BY m.banca.dataHora")
    List<MembroBancaEntity> findProximasBancasDocente(
        @Param("docenteId") Long docenteId,
        @Param("dataAtual") java.time.LocalDateTime dataAtual
    );

    /**
     * Busca bancas futuras de um professor externo
     */
    @Query("SELECT m FROM MembroBancaEntity m " +
           "WHERE m.professorExterno.id = :professorExternoId " +
           "AND m.banca.dataHora >= :dataAtual " +
           "AND m.banca.statusBanca IN ('AGENDADA', 'CONFIRMADA') " +
           "ORDER BY m.banca.dataHora")
    List<MembroBancaEntity> findProximasBancasProfessorExterno(
        @Param("professorExternoId") Long professorExternoId,
        @Param("dataAtual") java.time.LocalDateTime dataAtual
    );

    /**
     * Conta participações de um docente em bancas
     */
    @Query("SELECT COUNT(m) FROM MembroBancaEntity m WHERE m.docente.id = :docenteId")
    Long countParticipacoesDocente(@Param("docenteId") Long docenteId);

    /**
     * Conta participações de um professor externo em bancas
     */
    @Query("SELECT COUNT(m) FROM MembroBancaEntity m WHERE m.professorExterno.id = :professorExternoId")
    Long countParticipacoesProfessorExterno(@Param("professorExternoId") Long professorExternoId);

    /**
     * Busca membros por função
     */
    @Query("SELECT m FROM MembroBancaEntity m WHERE m.banca.id = :bancaId AND m.funcao = :funcao")
    List<MembroBancaEntity> findByBancaAndFuncao(
        @Param("bancaId") Long bancaId,
        @Param("funcao") Funcao funcao
    );

    /**
     * Busca membros internos de uma banca (docentes da mesma instituição)
     */
    @Query("SELECT m FROM MembroBancaEntity m " +
           "WHERE m.banca.id = :bancaId " +
           "AND m.funcao IN ('MEMBRO_INTERNO', 'PRESIDENTE', 'ORIENTADOR')")
    List<MembroBancaEntity> findMembrosInternosByBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros externos de uma banca
     */
    @Query("SELECT m FROM MembroBancaEntity m " +
           "WHERE m.banca.id = :bancaId " +
           "AND (m.funcao = 'MEMBRO_EXTERNO' OR m.professorExterno IS NOT NULL)")
    List<MembroBancaEntity> findMembrosExternosByBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca convites recusados de uma banca
     */
    @Query("SELECT m FROM MembroBancaEntity m WHERE m.banca.id = :bancaId AND m.statusConvite = 'RECUSADO'")
    List<MembroBancaEntity> findConvitesRecusadosByBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca docentes mais participantes de bancas (ranking)
     */
    @Query("SELECT m.docente, COUNT(m) as total FROM MembroBancaEntity m " +
           "WHERE m.docente IS NOT NULL " +
           "GROUP BY m.docente " +
           "ORDER BY COUNT(m) DESC")
    List<Object[]> findDocentesMaisParticipantes();

    /**
     * Busca professores externos mais participantes de bancas (ranking)
     */
    @Query("SELECT m.professorExterno, COUNT(m) as total FROM MembroBancaEntity m " +
           "WHERE m.professorExterno IS NOT NULL " +
           "GROUP BY m.professorExterno " +
           "ORDER BY COUNT(m) DESC")
    List<Object[]> findProfessoresExternosMaisParticipantes();
}
