package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import com.ppghub.infrastructure.persistence.entity.BancaEntity.StatusBanca;
import com.ppghub.infrastructure.persistence.entity.BancaEntity.TipoBanca;
import com.ppghub.infrastructure.persistence.entity.BancaEntity.ResultadoBanca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para Banca.
 */
@Repository
public interface JpaBancaRepository extends JpaRepository<BancaEntity, Long> {

    /**
     * Busca bancas por discente
     */
    List<BancaEntity> findByDiscenteId(Long discenteId);

    /**
     * Busca bancas por discente ordenadas por data
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.discente.id = :discenteId ORDER BY b.dataHora DESC")
    List<BancaEntity> findByDiscenteIdOrderByDataHora(@Param("discenteId") Long discenteId);

    /**
     * Busca bancas por programa
     */
    List<BancaEntity> findByProgramaId(Long programaId);

    /**
     * Busca bancas por programa com paginação
     */
    Page<BancaEntity> findByProgramaId(Long programaId, Pageable pageable);

    /**
     * Busca bancas por tipo
     */
    List<BancaEntity> findByTipoBanca(TipoBanca tipoBanca);

    /**
     * Busca bancas por status
     */
    List<BancaEntity> findByStatusBanca(StatusBanca statusBanca);

    /**
     * Busca bancas por resultado
     */
    List<BancaEntity> findByResultadoBanca(ResultadoBanca resultadoBanca);

    /**
     * Busca bancas agendadas por período
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.dataHora BETWEEN :dataInicio AND :dataFim " +
           "AND b.statusBanca IN ('AGENDADA', 'CONFIRMADA') ORDER BY b.dataHora")
    List<BancaEntity> findBancasAgendadasPorPeriodo(
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );

    /**
     * Busca bancas realizadas por período
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.dataRealizacao BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY b.dataRealizacao DESC")
    List<BancaEntity> findBancasRealizadasPorPeriodo(
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );

    /**
     * Busca próximas bancas (agendadas ou confirmadas) ordenadas por data
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.statusBanca IN ('AGENDADA', 'CONFIRMADA') " +
           "AND b.dataHora >= :dataAtual ORDER BY b.dataHora ASC")
    List<BancaEntity> findProximasBancas(@Param("dataAtual") LocalDateTime dataAtual);

    /**
     * Busca próximas bancas com limite
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.statusBanca IN ('AGENDADA', 'CONFIRMADA') " +
           "AND b.dataHora >= :dataAtual ORDER BY b.dataHora ASC")
    Page<BancaEntity> findProximasBancasPage(@Param("dataAtual") LocalDateTime dataAtual, Pageable pageable);

    /**
     * Busca bancas por programa e tipo
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.programa.id = :programaId AND b.tipoBanca = :tipo")
    List<BancaEntity> findByProgramaAndTipo(
        @Param("programaId") Long programaId,
        @Param("tipo") TipoBanca tipo
    );

    /**
     * Busca bancas por programa e status
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.programa.id = :programaId AND b.statusBanca = :status")
    List<BancaEntity> findByProgramaAndStatus(
        @Param("programaId") Long programaId,
        @Param("status") StatusBanca status
    );

    /**
     * Busca bancas remotas agendadas
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.defensaRemota = true " +
           "AND b.statusBanca IN ('AGENDADA', 'CONFIRMADA') ORDER BY b.dataHora")
    List<BancaEntity> findBancasRemotasAgendadas();

    /**
     * Busca bancas sem ata (realizadas mas sem documento)
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.statusBanca = 'REALIZADA' AND b.documentoAta IS NULL")
    List<BancaEntity> findBancasSemAta();

    /**
     * Conta bancas por programa e tipo
     */
    @Query("SELECT COUNT(b) FROM BancaEntity b WHERE b.programa.id = :programaId AND b.tipoBanca = :tipo")
    Long countByProgramaAndTipo(
        @Param("programaId") Long programaId,
        @Param("tipo") TipoBanca tipo
    );

    /**
     * Conta bancas realizadas por programa
     */
    @Query("SELECT COUNT(b) FROM BancaEntity b WHERE b.programa.id = :programaId AND b.statusBanca = 'REALIZADA'")
    Long countBancasRealizadasByPrograma(@Param("programaId") Long programaId);

    /**
     * Busca bancas com conflito de horário para um discente
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.discente.id = :discenteId " +
           "AND b.dataHora BETWEEN :dataInicio AND :dataFim " +
           "AND b.statusBanca NOT IN ('CANCELADA', 'REALIZADA')")
    List<BancaEntity> findBancasComConflitoHorarioDiscente(
        @Param("discenteId") Long discenteId,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );

    /**
     * Busca bancas por ano de realização
     */
    @Query("SELECT b FROM BancaEntity b WHERE YEAR(b.dataHora) = :ano ORDER BY b.dataHora DESC")
    List<BancaEntity> findByAno(@Param("ano") int ano);

    /**
     * Estatísticas de aprovação por programa
     */
    @Query("SELECT b.resultadoBanca, COUNT(b) FROM BancaEntity b " +
           "WHERE b.programa.id = :programaId AND b.statusBanca = 'REALIZADA' " +
           "GROUP BY b.resultadoBanca")
    List<Object[]> getEstatisticasAprovacaoByPrograma(@Param("programaId") Long programaId);

    /**
     * Busca bancas que precisam de confirmação (próximas 30 dias sem confirmar)
     */
    @Query("SELECT b FROM BancaEntity b WHERE b.statusBanca = 'AGENDADA' " +
           "AND b.dataHora BETWEEN :dataAtual AND :dataLimite ORDER BY b.dataHora")
    List<BancaEntity> findBancasPrecisandoConfirmacao(
        @Param("dataAtual") LocalDateTime dataAtual,
        @Param("dataLimite") LocalDateTime dataLimite
    );

    /**
     * Verifica se existe banca para discente em determinado período
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BancaEntity b " +
           "WHERE b.discente.id = :discenteId AND b.tipoBanca = :tipo " +
           "AND b.dataHora BETWEEN :dataInicio AND :dataFim " +
           "AND b.statusBanca NOT IN ('CANCELADA')")
    boolean existsBancaDiscenteNoPeriodo(
        @Param("discenteId") Long discenteId,
        @Param("tipo") TipoBanca tipo,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );
}
