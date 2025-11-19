package br.edu.ppg.hub.academic.infrastructure.repository;

import br.edu.ppg.hub.academic.domain.model.MetricaDocente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade MetricaDocente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Repository
public interface MetricaDocenteRepository extends JpaRepository<MetricaDocente, Long> {

    /**
     * Busca todas as métricas de um docente
     */
    List<MetricaDocente> findByDocenteId(Long docenteId);

    /**
     * Busca todas as métricas de um docente (paginado)
     */
    Page<MetricaDocente> findByDocenteId(Long docenteId, Pageable pageable);

    /**
     * Busca todas as métricas de um docente ordenadas por data de coleta (mais recente primeiro)
     */
    List<MetricaDocente> findByDocenteIdOrderByDataColetaDesc(Long docenteId);

    /**
     * Busca a métrica mais recente de um docente
     */
    @Query("SELECT m FROM MetricaDocente m WHERE m.docente.id = :docenteId ORDER BY m.dataColeta DESC LIMIT 1")
    Optional<MetricaDocente> findUltimaMetricaByDocenteId(@Param("docenteId") Long docenteId);

    /**
     * Busca métricas por fonte
     */
    List<MetricaDocente> findByFonte(String fonte);

    /**
     * Busca métricas de um docente por fonte
     */
    List<MetricaDocente> findByDocenteIdAndFonte(Long docenteId, String fonte);

    /**
     * Busca métricas de um docente por fonte ordenadas por data
     */
    List<MetricaDocente> findByDocenteIdAndFonteOrderByDataColetaDesc(Long docenteId, String fonte);

    /**
     * Busca docentes com alta produtividade (H-index >= 10) de um programa
     */
    @Query("SELECT DISTINCT m FROM MetricaDocente m " +
           "WHERE m.docente.programa.id = :programaId " +
           "AND m.hIndex >= 10 " +
           "AND m.dataColeta = (SELECT MAX(m2.dataColeta) FROM MetricaDocente m2 WHERE m2.docente.id = m.docente.id)")
    List<MetricaDocente> findDocentesAltaProdutividadePorPrograma(@Param("programaId") Long programaId);

    /**
     * Busca docentes que atendem aos critérios mínimos da CAPES
     */
    @Query("SELECT DISTINCT m FROM MetricaDocente m " +
           "WHERE m.docente.programa.id = :programaId " +
           "AND m.publicacoesUltimos5Anos >= 3 " +
           "AND m.dataColeta = (SELECT MAX(m2.dataColeta) FROM MetricaDocente m2 WHERE m2.docente.id = m.docente.id)")
    List<MetricaDocente> findDocentesQueAtendemCapesPorPrograma(@Param("programaId") Long programaId);

    /**
     * Conta métricas de um docente
     */
    long countByDocenteId(Long docenteId);
}
