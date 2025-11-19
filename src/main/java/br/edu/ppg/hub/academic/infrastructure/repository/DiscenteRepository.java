package br.edu.ppg.hub.academic.infrastructure.repository;

import br.edu.ppg.hub.academic.domain.enums.StatusDiscente;
import br.edu.ppg.hub.academic.domain.enums.TipoCurso;
import br.edu.ppg.hub.academic.domain.model.Discente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository para a entidade Discente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Repository
public interface DiscenteRepository extends JpaRepository<Discente, Long> {

    /**
     * Busca discente por ID do usuário
     */
    Optional<Discente> findByUsuarioId(Long usuarioId);

    /**
     * Busca discente por ID do usuário e ID do programa
     */
    Optional<Discente> findByUsuarioIdAndProgramaId(Long usuarioId, Long programaId);

    /**
     * Busca discente por número de matrícula
     */
    Optional<Discente> findByNumeroMatricula(String numeroMatricula);

    /**
     * Busca discente por programa e número de matrícula
     */
    Optional<Discente> findByProgramaIdAndNumeroMatricula(Long programaId, String numeroMatricula);

    /**
     * Busca todos os discentes de um programa
     */
    List<Discente> findByProgramaId(Long programaId);

    /**
     * Busca todos os discentes de um programa (paginado)
     */
    Page<Discente> findByProgramaId(Long programaId, Pageable pageable);

    /**
     * Busca discentes por orientador
     */
    List<Discente> findByOrientadorId(Long orientadorId);

    /**
     * Busca discentes por orientador (paginado)
     */
    Page<Discente> findByOrientadorId(Long orientadorId, Pageable pageable);

    /**
     * Busca discentes por tipo de curso
     */
    List<Discente> findByTipoCurso(TipoCurso tipoCurso);

    /**
     * Busca discentes por tipo de curso (paginado)
     */
    Page<Discente> findByTipoCurso(TipoCurso tipoCurso, Pageable pageable);

    /**
     * Busca discentes de um programa por tipo de curso
     */
    List<Discente> findByProgramaIdAndTipoCurso(Long programaId, TipoCurso tipoCurso);

    /**
     * Busca discentes de um programa por tipo de curso (paginado)
     */
    Page<Discente> findByProgramaIdAndTipoCurso(Long programaId, TipoCurso tipoCurso, Pageable pageable);

    /**
     * Busca discentes por status
     */
    List<Discente> findByStatus(StatusDiscente status);

    /**
     * Busca discentes por status (paginado)
     */
    Page<Discente> findByStatus(StatusDiscente status, Pageable pageable);

    /**
     * Busca discentes de um programa por status
     */
    List<Discente> findByProgramaIdAndStatus(Long programaId, StatusDiscente status);

    /**
     * Busca discentes de um programa por status (paginado)
     */
    Page<Discente> findByProgramaIdAndStatus(Long programaId, StatusDiscente status, Pageable pageable);

    /**
     * Busca discentes por linha de pesquisa
     */
    List<Discente> findByLinhaPesquisaId(Long linhaPesquisaId);

    /**
     * Busca discentes por linha de pesquisa (paginado)
     */
    Page<Discente> findByLinhaPesquisaId(Long linhaPesquisaId, Pageable pageable);

    /**
     * Busca discentes ativos de um programa
     */
    @Query("SELECT d FROM Discente d WHERE d.programa.id = :programaId " +
           "AND d.status IN ('MATRICULADO', 'CURSANDO', 'QUALIFICADO', 'DEFENDENDO')")
    List<Discente> findDiscentesAtivosPorPrograma(@Param("programaId") Long programaId);

    /**
     * Busca discentes com prazo vencido
     */
    @Query("SELECT d FROM Discente d WHERE d.dataLimiteAtual < :dataAtual " +
           "AND d.status IN ('MATRICULADO', 'CURSANDO', 'QUALIFICADO', 'DEFENDENDO')")
    List<Discente> findDiscentesComPrazoVencido(@Param("dataAtual") LocalDate dataAtual);

    /**
     * Busca discentes com prazo próximo do vencimento (30 dias)
     */
    @Query("SELECT d FROM Discente d WHERE d.dataLimiteAtual BETWEEN :dataAtual AND :dataLimite " +
           "AND d.status IN ('MATRICULADO', 'CURSANDO', 'QUALIFICADO', 'DEFENDENDO')")
    List<Discente> findDiscentesComPrazoProximoVencimento(
        @Param("dataAtual") LocalDate dataAtual,
        @Param("dataLimite") LocalDate dataLimite
    );

    /**
     * Busca discentes sem orientador
     */
    @Query("SELECT d FROM Discente d WHERE d.orientador IS NULL " +
           "AND d.status IN ('MATRICULADO', 'CURSANDO')")
    List<Discente> findDiscentesSemOrientador();

    /**
     * Busca discentes bolsistas
     */
    @Query("SELECT d FROM Discente d WHERE d.bolsista = true " +
           "AND (d.dataFimBolsa IS NULL OR d.dataFimBolsa >= CURRENT_DATE)")
    List<Discente> findDiscentesBolsistas();

    /**
     * Busca discentes bolsistas de um programa
     */
    @Query("SELECT d FROM Discente d WHERE d.programa.id = :programaId " +
           "AND d.bolsista = true " +
           "AND (d.dataFimBolsa IS NULL OR d.dataFimBolsa >= CURRENT_DATE)")
    List<Discente> findDiscentesBolsistasPorPrograma(@Param("programaId") Long programaId);

    /**
     * Busca discentes qualificados que podem defender
     */
    @Query("SELECT d FROM Discente d WHERE d.programa.id = :programaId " +
           "AND d.status = 'QUALIFICADO' " +
           "AND d.qualificacaoRealizada = true")
    List<Discente> findDiscentesProntosPararDefender(@Param("programaId") Long programaId);

    /**
     * Conta discentes por programa
     */
    long countByProgramaId(Long programaId);

    /**
     * Conta discentes por programa e status
     */
    long countByProgramaIdAndStatus(Long programaId, StatusDiscente status);

    /**
     * Conta discentes por programa e tipo de curso
     */
    long countByProgramaIdAndTipoCurso(Long programaId, TipoCurso tipoCurso);

    /**
     * Conta discentes por orientador
     */
    long countByOrientadorId(Long orientadorId);

    /**
     * Verifica se já existe discente com o mesmo número de matrícula no programa
     */
    boolean existsByProgramaIdAndNumeroMatricula(Long programaId, String numeroMatricula);

    /**
     * Estatísticas de discentes por programa
     */
    @Query("SELECT new map(" +
           "COUNT(d) as total, " +
           "SUM(CASE WHEN d.tipoCurso = 'MESTRADO' THEN 1 ELSE 0 END) as totalMestrado, " +
           "SUM(CASE WHEN d.tipoCurso = 'DOUTORADO' THEN 1 ELSE 0 END) as totalDoutorado, " +
           "SUM(CASE WHEN d.status = 'TITULADO' THEN 1 ELSE 0 END) as totalTitulados, " +
           "SUM(CASE WHEN d.bolsista = true THEN 1 ELSE 0 END) as totalBolsistas" +
           ") FROM Discente d WHERE d.programa.id = :programaId")
    Map<String, Long> getEstatisticasPorPrograma(@Param("programaId") Long programaId);

    /**
     * Busca discentes por turma
     */
    List<Discente> findByProgramaIdAndTurma(Long programaId, Integer turma);

    /**
     * Busca discentes por semestre de ingresso
     */
    List<Discente> findByProgramaIdAndSemestreIngresso(Long programaId, String semestreIngresso);
}
