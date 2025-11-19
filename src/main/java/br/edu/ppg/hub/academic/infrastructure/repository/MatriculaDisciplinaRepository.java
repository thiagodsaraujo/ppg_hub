package br.edu.ppg.hub.academic.infrastructure.repository;

import br.edu.ppg.hub.academic.domain.enums.StatusMatricula;
import br.edu.ppg.hub.academic.domain.model.MatriculaDisciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade MatriculaDisciplina
 *
 * @author PPG Hub
 * @since 1.0
 */
@Repository
public interface MatriculaDisciplinaRepository extends JpaRepository<MatriculaDisciplina, Long> {

    /**
     * Busca todas as matrículas de um discente
     */
    List<MatriculaDisciplina> findByDiscenteId(Long discenteId);

    /**
     * Busca todas as matrículas de um discente (paginado)
     */
    Page<MatriculaDisciplina> findByDiscenteId(Long discenteId, Pageable pageable);

    /**
     * Busca todas as matrículas de uma oferta
     */
    List<MatriculaDisciplina> findByOfertaDisciplinaId(Long ofertaId);

    /**
     * Busca todas as matrículas de uma oferta (paginado)
     */
    Page<MatriculaDisciplina> findByOfertaDisciplinaId(Long ofertaId, Pageable pageable);

    /**
     * Busca matrícula específica de discente em oferta
     */
    Optional<MatriculaDisciplina> findByDiscenteIdAndOfertaDisciplinaId(Long discenteId, Long ofertaId);

    /**
     * Busca matrículas de um discente em um período específico
     */
    @Query("SELECT m FROM MatriculaDisciplina m WHERE m.discente.id = :discenteId " +
           "AND m.ofertaDisciplina.periodo = :periodo")
    List<MatriculaDisciplina> findByDiscenteIdAndPeriodo(@Param("discenteId") Long discenteId,
                                                          @Param("periodo") String periodo);

    /**
     * Busca matrículas de um discente por semestre
     */
    @Query("SELECT m FROM MatriculaDisciplina m WHERE m.discente.id = :discenteId " +
           "AND m.ofertaDisciplina.ano = :ano AND m.ofertaDisciplina.semestre = :semestre")
    List<MatriculaDisciplina> findByDiscenteIdAndSemestre(@Param("discenteId") Long discenteId,
                                                           @Param("ano") Integer ano,
                                                           @Param("semestre") Integer semestre);

    /**
     * Busca matrículas por status
     */
    List<MatriculaDisciplina> findBySituacao(StatusMatricula situacao);

    /**
     * Busca matrículas por status (paginado)
     */
    Page<MatriculaDisciplina> findBySituacao(StatusMatricula situacao, Pageable pageable);

    /**
     * Busca matrículas de um discente por status
     */
    List<MatriculaDisciplina> findByDiscenteIdAndSituacao(Long discenteId, StatusMatricula situacao);

    /**
     * Busca matrículas de uma oferta por status
     */
    List<MatriculaDisciplina> findByOfertaDisciplinaIdAndSituacao(Long ofertaId, StatusMatricula situacao);

    /**
     * Verifica se discente já está matriculado na oferta
     */
    boolean existsByDiscenteIdAndOfertaDisciplinaId(Long discenteId, Long ofertaId);

    /**
     * Busca histórico completo de matrículas de um discente ordenado por período
     */
    @Query("SELECT m FROM MatriculaDisciplina m WHERE m.discente.id = :discenteId " +
           "ORDER BY m.ofertaDisciplina.ano DESC, m.ofertaDisciplina.semestre DESC, " +
           "m.ofertaDisciplina.disciplina.nome")
    List<MatriculaDisciplina> findHistoricoByDiscenteId(@Param("discenteId") Long discenteId);

    /**
     * Busca matrículas ativas de um discente (status MATRICULADO)
     */
    @Query("SELECT m FROM MatriculaDisciplina m WHERE m.discente.id = :discenteId " +
           "AND m.situacao = 'MATRICULADO' " +
           "ORDER BY m.ofertaDisciplina.disciplina.nome")
    List<MatriculaDisciplina> findMatriculasAtivasByDiscente(@Param("discenteId") Long discenteId);

    /**
     * Busca matrículas concluídas de um discente (aprovadas ou reprovadas)
     */
    @Query("SELECT m FROM MatriculaDisciplina m WHERE m.discente.id = :discenteId " +
           "AND m.situacao IN ('APROVADO', 'REPROVADO') " +
           "ORDER BY m.ofertaDisciplina.ano DESC, m.ofertaDisciplina.semestre DESC")
    List<MatriculaDisciplina> findMatriculasConcluidasByDiscente(@Param("discenteId") Long discenteId);

    /**
     * Calcula estatísticas de uma oferta (aprovados, reprovados, média geral)
     */
    @Query("SELECT " +
           "COUNT(m) as total, " +
           "SUM(CASE WHEN m.situacao = 'APROVADO' THEN 1 ELSE 0 END) as aprovados, " +
           "SUM(CASE WHEN m.situacao = 'REPROVADO' THEN 1 ELSE 0 END) as reprovados, " +
           "SUM(CASE WHEN m.situacao = 'TRANCADO' THEN 1 ELSE 0 END) as trancados, " +
           "AVG(m.notaFinal) as mediaGeral, " +
           "AVG(m.frequenciaPercentual) as mediaFrequencia " +
           "FROM MatriculaDisciplina m WHERE m.ofertaDisciplina.id = :ofertaId")
    Object[] calcularEstatisticasOferta(@Param("ofertaId") Long ofertaId);

    /**
     * Calcula estatísticas gerais de um discente
     */
    @Query("SELECT " +
           "COUNT(m) as totalDisciplinas, " +
           "SUM(CASE WHEN m.situacao = 'APROVADO' THEN 1 ELSE 0 END) as aprovadas, " +
           "SUM(CASE WHEN m.situacao = 'REPROVADO' THEN 1 ELSE 0 END) as reprovadas, " +
           "SUM(CASE WHEN m.situacao = 'APROVADO' THEN m.ofertaDisciplina.disciplina.creditos ELSE 0 END) as creditosObtidos, " +
           "AVG(CASE WHEN m.situacao = 'APROVADO' THEN m.notaFinal ELSE NULL END) as mediaGeral " +
           "FROM MatriculaDisciplina m WHERE m.discente.id = :discenteId")
    Object[] calcularEstatisticasDiscente(@Param("discenteId") Long discenteId);

    /**
     * Busca matrículas com notas pendentes (em curso mas sem nota final)
     */
    @Query("SELECT m FROM MatriculaDisciplina m WHERE m.ofertaDisciplina.id = :ofertaId " +
           "AND m.situacao = 'MATRICULADO' " +
           "AND m.ofertaDisciplina.status = 'EM_CURSO' " +
           "AND m.notaFinal IS NULL")
    List<MatriculaDisciplina> findComNotasPendentes(@Param("ofertaId") Long ofertaId);

    /**
     * Busca matrículas com frequência abaixo da mínima
     */
    @Query("SELECT m FROM MatriculaDisciplina m WHERE m.ofertaDisciplina.id = :ofertaId " +
           "AND m.situacao = 'MATRICULADO' " +
           "AND m.frequenciaPercentual < 75.0")
    List<MatriculaDisciplina> findComFrequenciaBaixa(@Param("ofertaId") Long ofertaId);

    /**
     * Busca top N discentes com melhores médias em um período
     */
    @Query("SELECT m.discente.id, m.discente.usuario.nomeCompleto, AVG(m.notaFinal) as media " +
           "FROM MatriculaDisciplina m " +
           "WHERE m.ofertaDisciplina.periodo = :periodo " +
           "AND m.situacao = 'APROVADO' " +
           "GROUP BY m.discente.id, m.discente.usuario.nomeCompleto " +
           "ORDER BY media DESC")
    Page<Object[]> findTopDiscentesPorMedia(@Param("periodo") String periodo, Pageable pageable);

    /**
     * Conta matrículas por discente
     */
    long countByDiscenteId(Long discenteId);

    /**
     * Conta matrículas por oferta
     */
    long countByOfertaDisciplinaId(Long ofertaId);

    /**
     * Conta matrículas por discente e situação
     */
    long countByDiscenteIdAndSituacao(Long discenteId, StatusMatricula situacao);

    /**
     * Conta matrículas por oferta e situação
     */
    long countByOfertaDisciplinaIdAndSituacao(Long ofertaId, StatusMatricula situacao);

    /**
     * Busca matrículas para lançamento de resultado
     */
    @Query("SELECT m FROM MatriculaDisciplina m WHERE m.ofertaDisciplina.id = :ofertaId " +
           "AND m.situacao = 'MATRICULADO' " +
           "AND m.notaFinal IS NOT NULL " +
           "AND m.frequenciaPercentual IS NOT NULL " +
           "AND m.statusFinal IS NULL")
    List<MatriculaDisciplina> findParaLancamentoResultado(@Param("ofertaId") Long ofertaId);

    /**
     * Busca matrículas de um discente em disciplinas de um programa
     */
    @Query("SELECT m FROM MatriculaDisciplina m " +
           "WHERE m.discente.id = :discenteId " +
           "AND m.ofertaDisciplina.disciplina.programa.id = :programaId " +
           "ORDER BY m.ofertaDisciplina.ano DESC, m.ofertaDisciplina.semestre DESC")
    List<MatriculaDisciplina> findByDiscenteAndPrograma(@Param("discenteId") Long discenteId,
                                                          @Param("programaId") Long programaId);

    /**
     * Busca disciplinas já cursadas por um discente (para validação de pré-requisitos)
     */
    @Query("SELECT m.ofertaDisciplina.disciplina.id FROM MatriculaDisciplina m " +
           "WHERE m.discente.id = :discenteId AND m.situacao = 'APROVADO'")
    List<Long> findDisciplinasCursadasAprovadas(@Param("discenteId") Long discenteId);
}
