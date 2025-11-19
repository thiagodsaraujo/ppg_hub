package br.edu.ppg.hub.academic.infrastructure.repository;

import br.edu.ppg.hub.academic.domain.enums.StatusOferta;
import br.edu.ppg.hub.academic.domain.model.OfertaDisciplina;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade OfertaDisciplina
 *
 * @author PPG Hub
 * @since 1.0
 */
@Repository
public interface OfertaDisciplinaRepository extends JpaRepository<OfertaDisciplina, Long> {

    /**
     * Busca oferta por ID com LOCK PESSIMISTA para controle de concorrência
     * Usado especialmente para matricular alunos e evitar race condition nas vagas
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.id = :id")
    Optional<OfertaDisciplina> findByIdForUpdate(@Param("id") Long id);

    /**
     * Busca todas as ofertas de uma disciplina
     */
    List<OfertaDisciplina> findByDisciplinaId(Long disciplinaId);

    /**
     * Busca todas as ofertas de uma disciplina (paginado)
     */
    Page<OfertaDisciplina> findByDisciplinaId(Long disciplinaId, Pageable pageable);

    /**
     * Busca ofertas por semestre
     */
    List<OfertaDisciplina> findByAnoAndSemestre(Integer ano, Integer semestre);

    /**
     * Busca ofertas por semestre (paginado)
     */
    Page<OfertaDisciplina> findByAnoAndSemestre(Integer ano, Integer semestre, Pageable pageable);

    /**
     * Busca ofertas por período
     */
    List<OfertaDisciplina> findByPeriodo(String periodo);

    /**
     * Busca ofertas por período (paginado)
     */
    Page<OfertaDisciplina> findByPeriodo(String periodo, Pageable pageable);

    /**
     * Busca ofertas por docente responsável
     */
    List<OfertaDisciplina> findByDocenteResponsavelId(Long docenteId);

    /**
     * Busca ofertas por docente responsável (paginado)
     */
    Page<OfertaDisciplina> findByDocenteResponsavelId(Long docenteId, Pageable pageable);

    /**
     * Busca ofertas por docente colaborador
     */
    List<OfertaDisciplina> findByDocenteColaboradorId(Long docenteId);

    /**
     * Busca ofertas por status
     */
    List<OfertaDisciplina> findByStatus(StatusOferta status);

    /**
     * Busca ofertas por status (paginado)
     */
    Page<OfertaDisciplina> findByStatus(StatusOferta status, Pageable pageable);

    /**
     * Busca ofertas com vagas disponíveis
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.vagasOcupadas < o.vagasOferecidas " +
           "AND o.status = 'ABERTA'")
    List<OfertaDisciplina> findComVagasDisponiveis();

    /**
     * Busca ofertas com vagas disponíveis (paginado)
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.vagasOcupadas < o.vagasOferecidas " +
           "AND o.status = 'ABERTA'")
    Page<OfertaDisciplina> findComVagasDisponiveis(Pageable pageable);

    /**
     * Busca ofertas ativas (abertas, fechadas ou em curso)
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.status IN ('ABERTA', 'FECHADA', 'EM_CURSO') " +
           "ORDER BY o.periodo DESC, o.disciplina.nome")
    List<OfertaDisciplina> findOfertasAtivas();

    /**
     * Busca ofertas ativas (paginado)
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.status IN ('ABERTA', 'FECHADA', 'EM_CURSO') " +
           "ORDER BY o.periodo DESC, o.disciplina.nome")
    Page<OfertaDisciplina> findOfertasAtivas(Pageable pageable);

    /**
     * Busca ofertas de um período por programa
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.disciplina.programa.id = :programaId " +
           "AND o.periodo = :periodo ORDER BY o.disciplina.codigo")
    List<OfertaDisciplina> findByProgramaAndPeriodo(@Param("programaId") Long programaId,
                                                     @Param("periodo") String periodo);

    /**
     * Busca ofertas de um docente em um período
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.docenteResponsavel.id = :docenteId " +
           "AND o.ano = :ano AND o.semestre = :semestre")
    List<OfertaDisciplina> findByDocenteAndPeriodo(@Param("docenteId") Long docenteId,
                                                     @Param("ano") Integer ano,
                                                     @Param("semestre") Integer semestre);

    /**
     * Busca ofertas com inscrições abertas
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.status = 'ABERTA' " +
           "AND o.vagasOcupadas < o.vagasOferecidas " +
           "AND o.dataInicio > :dataAtual " +
           "ORDER BY o.dataInicio")
    List<OfertaDisciplina> findInscricoesAbertas(@Param("dataAtual") LocalDate dataAtual);

    /**
     * Busca ofertas em andamento
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.status = 'EM_CURSO' " +
           "AND o.dataInicio <= :dataAtual AND o.dataFim >= :dataAtual " +
           "ORDER BY o.disciplina.nome")
    List<OfertaDisciplina> findEmAndamento(@Param("dataAtual") LocalDate dataAtual);

    /**
     * Busca ofertas que precisam ser iniciadas
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.status = 'FECHADA' " +
           "AND o.dataInicio <= :dataAtual " +
           "ORDER BY o.dataInicio")
    List<OfertaDisciplina> findParaIniciar(@Param("dataAtual") LocalDate dataAtual);

    /**
     * Busca ofertas que precisam ser concluídas
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.status = 'EM_CURSO' " +
           "AND o.dataFim <= :dataAtual " +
           "ORDER BY o.dataFim")
    List<OfertaDisciplina> findParaConcluir(@Param("dataAtual") LocalDate dataAtual);

    /**
     * Busca ofertas por modalidade
     */
    List<OfertaDisciplina> findByModalidade(String modalidade);

    /**
     * Busca ofertas de uma disciplina específica em um período
     */
    Optional<OfertaDisciplina> findByDisciplinaIdAndAnoAndSemestreAndTurma(
        Long disciplinaId, Integer ano, Integer semestre, String turma
    );

    /**
     * Conta ofertas por disciplina
     */
    long countByDisciplinaId(Long disciplinaId);

    /**
     * Conta ofertas por docente
     */
    long countByDocenteResponsavelId(Long docenteId);

    /**
     * Conta ofertas por período
     */
    long countByPeriodo(String periodo);

    /**
     * Conta ofertas por status
     */
    long countByStatus(StatusOferta status);

    /**
     * Verifica se já existe oferta para disciplina no período e turma
     */
    boolean existsByDisciplinaIdAndAnoAndSemestreAndTurma(
        Long disciplinaId, Integer ano, Integer semestre, String turma
    );

    /**
     * Busca ofertas com maior taxa de ocupação
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.periodo = :periodo " +
           "ORDER BY (CAST(o.vagasOcupadas AS float) / o.vagasOferecidas) DESC")
    Page<OfertaDisciplina> findMaiorTaxaOcupacao(@Param("periodo") String periodo, Pageable pageable);

    /**
     * Busca ofertas com vagas ociosas
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.status IN ('ABERTA', 'EM_CURSO') " +
           "AND o.vagasOcupadas < (o.vagasOferecidas * 0.5) " +
           "ORDER BY o.vagasOcupadas")
    List<OfertaDisciplina> findComVagasOciosas();

    /**
     * Busca últimas ofertas de uma disciplina
     */
    @Query("SELECT o FROM OfertaDisciplina o WHERE o.disciplina.id = :disciplinaId " +
           "ORDER BY o.ano DESC, o.semestre DESC")
    Page<OfertaDisciplina> findUltimasOfertasDisciplina(@Param("disciplinaId") Long disciplinaId, Pageable pageable);
}
