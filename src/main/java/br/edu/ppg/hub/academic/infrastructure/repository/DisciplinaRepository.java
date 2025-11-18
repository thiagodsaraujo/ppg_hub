package br.edu.ppg.hub.academic.infrastructure.repository;

import br.edu.ppg.hub.academic.domain.enums.StatusDisciplina;
import br.edu.ppg.hub.academic.domain.enums.TipoDisciplina;
import br.edu.ppg.hub.academic.domain.model.Disciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Disciplina
 *
 * @author PPG Hub
 * @since 1.0
 */
@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    /**
     * Busca disciplina por código e programa
     */
    Optional<Disciplina> findByProgramaIdAndCodigo(Long programaId, String codigo);

    /**
     * Busca todas as disciplinas de um programa
     */
    List<Disciplina> findByProgramaId(Long programaId);

    /**
     * Busca todas as disciplinas de um programa (paginado)
     */
    Page<Disciplina> findByProgramaId(Long programaId, Pageable pageable);

    /**
     * Busca disciplinas por código (partial match)
     */
    List<Disciplina> findByCodigoContainingIgnoreCase(String codigo);

    /**
     * Busca disciplinas por nome (partial match)
     */
    List<Disciplina> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca disciplinas de um programa por tipo
     */
    List<Disciplina> findByProgramaIdAndTipo(Long programaId, TipoDisciplina tipo);

    /**
     * Busca disciplinas de um programa por tipo (paginado)
     */
    Page<Disciplina> findByProgramaIdAndTipo(Long programaId, TipoDisciplina tipo, Pageable pageable);

    /**
     * Busca disciplinas de um programa por status
     */
    List<Disciplina> findByProgramaIdAndStatus(Long programaId, StatusDisciplina status);

    /**
     * Busca disciplinas de um programa por status (paginado)
     */
    Page<Disciplina> findByProgramaIdAndStatus(Long programaId, StatusDisciplina status, Pageable pageable);

    /**
     * Busca disciplinas ativas de um programa
     */
    @Query("SELECT d FROM Disciplina d WHERE d.programa.id = :programaId AND d.status = 'ATIVA'")
    List<Disciplina> findAtivasByPrograma(@Param("programaId") Long programaId);

    /**
     * Busca disciplinas ativas de um programa (paginado)
     */
    @Query("SELECT d FROM Disciplina d WHERE d.programa.id = :programaId AND d.status = 'ATIVA'")
    Page<Disciplina> findAtivasByPrograma(@Param("programaId") Long programaId, Pageable pageable);

    /**
     * Busca disciplinas de uma linha de pesquisa
     */
    List<Disciplina> findByLinhaPesquisaId(Long linhaPesquisaId);

    /**
     * Busca disciplinas de uma linha de pesquisa (paginado)
     */
    Page<Disciplina> findByLinhaPesquisaId(Long linhaPesquisaId, Pageable pageable);

    /**
     * Busca disciplinas por nível
     */
    List<Disciplina> findByNivel(String nivel);

    /**
     * Busca disciplinas de um programa por nível
     */
    List<Disciplina> findByProgramaIdAndNivel(Long programaId, String nivel);

    /**
     * Busca disciplinas obrigatórias de um programa
     */
    @Query("SELECT d FROM Disciplina d WHERE d.programa.id = :programaId " +
           "AND d.tipo = 'OBRIGATORIA' AND d.status = 'ATIVA' ORDER BY d.codigo")
    List<Disciplina> findObrigatoriasAtivas(@Param("programaId") Long programaId);

    /**
     * Busca disciplinas eletivas ativas de um programa
     */
    @Query("SELECT d FROM Disciplina d WHERE d.programa.id = :programaId " +
           "AND d.tipo = 'ELETIVA' AND d.status = 'ATIVA' ORDER BY d.nome")
    List<Disciplina> findEletivasAtivas(@Param("programaId") Long programaId);

    /**
     * Busca disciplinas por modalidade
     */
    List<Disciplina> findByModalidade(String modalidade);

    /**
     * Busca disciplinas de um programa por modalidade
     */
    List<Disciplina> findByProgramaIdAndModalidade(Long programaId, String modalidade);

    /**
     * Busca disciplinas com carga horária mínima
     */
    @Query("SELECT d FROM Disciplina d WHERE d.cargaHorariaTotal >= :cargaHorariaMinima")
    List<Disciplina> findByCargaHorariaMinima(@Param("cargaHorariaMinima") Integer cargaHorariaMinima);

    /**
     * Busca disciplinas por área (usando linha de pesquisa)
     */
    @Query("SELECT d FROM Disciplina d JOIN d.linhaPesquisa lp " +
           "WHERE lp.areaConcentracao LIKE %:area% AND d.status = 'ATIVA'")
    List<Disciplina> findByArea(@Param("area") String area);

    /**
     * Conta disciplinas por programa
     */
    long countByProgramaId(Long programaId);

    /**
     * Conta disciplinas por programa e status
     */
    long countByProgramaIdAndStatus(Long programaId, StatusDisciplina status);

    /**
     * Conta disciplinas por programa e tipo
     */
    long countByProgramaIdAndTipo(Long programaId, TipoDisciplina tipo);

    /**
     * Verifica se já existe disciplina com o mesmo código no programa
     */
    boolean existsByProgramaIdAndCodigo(Long programaId, String codigo);

    /**
     * Busca disciplinas mais oferecidas (com mais ofertas ativas)
     */
    @Query("SELECT d FROM Disciplina d WHERE d.programa.id = :programaId " +
           "AND d.status = 'ATIVA' " +
           "ORDER BY d.nome")
    Page<Disciplina> findMaisOferecidas(@Param("programaId") Long programaId, Pageable pageable);

    /**
     * Busca disciplinas sem ofertas recentes
     */
    @Query("SELECT d FROM Disciplina d WHERE d.programa.id = :programaId " +
           "AND d.status = 'ATIVA' " +
           "AND NOT EXISTS (SELECT o FROM OfertaDisciplina o WHERE o.disciplina = d " +
           "AND o.ano >= :anoMinimo)")
    List<Disciplina> findSemOfertasRecentes(@Param("programaId") Long programaId, @Param("anoMinimo") Integer anoMinimo);
}
