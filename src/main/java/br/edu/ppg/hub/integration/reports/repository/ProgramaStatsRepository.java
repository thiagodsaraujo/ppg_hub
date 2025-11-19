package br.edu.ppg.hub.integration.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository para acesso à view materializada de estatísticas do programa.
 * <p>
 * Utiliza queries nativas para acessar a view {@code academic.mv_programa_stats}
 * que consolida métricas agregadas sobre docentes, discentes e disciplinas.
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Repository
public interface ProgramaStatsRepository extends JpaRepository<Object, Long> {

    /**
     * Busca estatísticas de um programa específico.
     *
     * @param programaId ID do programa
     * @return Map com as estatísticas do programa
     */
    @Query(value = """
        SELECT
            programa_id,
            programa_nome,
            programa_sigla,
            total_docentes,
            docentes_permanentes,
            total_discentes,
            mestrandos,
            doutorandos,
            discentes_ativos,
            titulados,
            total_disciplinas,
            ofertas_ativas,
            media_notas
        FROM academic.mv_programa_stats
        WHERE programa_id = :programaId
        """, nativeQuery = true)
    Optional<Map<String, Object>> findByProgramaId(@Param("programaId") Long programaId);

    /**
     * Lista estatísticas de todos os programas.
     *
     * @return Lista de Maps com estatísticas de cada programa
     */
    @Query(value = """
        SELECT
            programa_id,
            programa_nome,
            programa_sigla,
            total_docentes,
            docentes_permanentes,
            total_discentes,
            mestrandos,
            doutorandos,
            discentes_ativos,
            titulados,
            total_disciplinas,
            ofertas_ativas,
            media_notas
        FROM academic.mv_programa_stats
        ORDER BY programa_nome
        """, nativeQuery = true)
    List<Map<String, Object>> findAllStats();

    /**
     * Busca estatísticas por sigla do programa.
     *
     * @param sigla Sigla do programa
     * @return Map com as estatísticas do programa
     */
    @Query(value = """
        SELECT
            programa_id,
            programa_nome,
            programa_sigla,
            total_docentes,
            docentes_permanentes,
            total_discentes,
            mestrandos,
            doutorandos,
            discentes_ativos,
            titulados,
            total_disciplinas,
            ofertas_ativas,
            media_notas
        FROM academic.mv_programa_stats
        WHERE programa_sigla = :sigla
        """, nativeQuery = true)
    Optional<Map<String, Object>> findBySigla(@Param("sigla") String sigla);

    /**
     * Retorna programas com maior número de discentes ativos.
     *
     * @param limit Número máximo de resultados
     * @return Lista de Maps ordenados por discentes ativos
     */
    @Query(value = """
        SELECT
            programa_id,
            programa_nome,
            programa_sigla,
            total_docentes,
            docentes_permanentes,
            total_discentes,
            mestrandos,
            doutorandos,
            discentes_ativos,
            titulados,
            total_disciplinas,
            ofertas_ativas,
            media_notas
        FROM academic.mv_programa_stats
        WHERE discentes_ativos > 0
        ORDER BY discentes_ativos DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Map<String, Object>> findTopProgramasByDiscentesAtivos(@Param("limit") int limit);
}
