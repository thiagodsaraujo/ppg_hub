package br.edu.ppg.hub.integration.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository para acesso à view materializada de produção docente.
 * <p>
 * Utiliza queries nativas para acessar a view {@code academic.mv_producao_docente}
 * que consolida métricas de orientações, disciplinas, bancas e publicações.
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Repository
public interface ProducaoDocenteRepository extends JpaRepository<Object, Long> {

    /**
     * Busca métricas de produção de um docente específico.
     *
     * @param docenteId ID do docente
     * @return Map com as métricas do docente
     */
    @Query(value = """
        SELECT
            docente_id,
            programa_id,
            docente_nome,
            docente_email,
            docente_categoria,
            total_orientandos,
            orientandos_ativos,
            orientandos_titulados,
            orientandos_evadidos,
            disciplinas_ministradas,
            bancas_participadas,
            bancas_qualificacao,
            bancas_defesa,
            total_publicacoes,
            total_citacoes,
            h_index,
            i10_index
        FROM academic.mv_producao_docente
        WHERE docente_id = :docenteId
        """, nativeQuery = true)
    Optional<Map<String, Object>> findByDocenteId(@Param("docenteId") Long docenteId);

    /**
     * Lista produção de todos os docentes de um programa.
     *
     * @param programaId ID do programa
     * @return Lista de Maps com métricas de cada docente
     */
    @Query(value = """
        SELECT
            docente_id,
            programa_id,
            docente_nome,
            docente_email,
            docente_categoria,
            total_orientandos,
            orientandos_ativos,
            orientandos_titulados,
            orientandos_evadidos,
            disciplinas_ministradas,
            bancas_participadas,
            bancas_qualificacao,
            bancas_defesa,
            total_publicacoes,
            total_citacoes,
            h_index,
            i10_index
        FROM academic.mv_producao_docente
        WHERE programa_id = :programaId
        ORDER BY h_index DESC NULLS LAST, total_publicacoes DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findByProgramaId(@Param("programaId") Long programaId);

    /**
     * Retorna top N docentes por H-index.
     *
     * @param programaId ID do programa
     * @param limit Número máximo de resultados
     * @return Lista de Maps ordenados por H-index
     */
    @Query(value = """
        SELECT
            docente_id,
            programa_id,
            docente_nome,
            docente_email,
            docente_categoria,
            total_orientandos,
            orientandos_ativos,
            orientandos_titulados,
            orientandos_evadidos,
            disciplinas_ministradas,
            bancas_participadas,
            bancas_qualificacao,
            bancas_defesa,
            total_publicacoes,
            total_citacoes,
            h_index,
            i10_index
        FROM academic.mv_producao_docente
        WHERE programa_id = :programaId
        ORDER BY h_index DESC NULLS LAST, total_publicacoes DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Map<String, Object>> findTopDocentesByHIndex(
            @Param("programaId") Long programaId,
            @Param("limit") int limit
    );

    /**
     * Retorna docentes por categoria.
     *
     * @param programaId ID do programa
     * @param categoria Categoria do docente (PERMANENTE, COLABORADOR, etc)
     * @return Lista de Maps filtrados por categoria
     */
    @Query(value = """
        SELECT
            docente_id,
            programa_id,
            docente_nome,
            docente_email,
            docente_categoria,
            total_orientandos,
            orientandos_ativos,
            orientandos_titulados,
            orientandos_evadidos,
            disciplinas_ministradas,
            bancas_participadas,
            bancas_qualificacao,
            bancas_defesa,
            total_publicacoes,
            total_citacoes,
            h_index,
            i10_index
        FROM academic.mv_producao_docente
        WHERE programa_id = :programaId
          AND docente_categoria = :categoria
        ORDER BY h_index DESC NULLS LAST
        """, nativeQuery = true)
    List<Map<String, Object>> findByProgramaIdAndCategoria(
            @Param("programaId") Long programaId,
            @Param("categoria") String categoria
    );

    /**
     * Retorna docentes com maior número de orientandos ativos.
     *
     * @param programaId ID do programa
     * @param limit Número máximo de resultados
     * @return Lista de Maps ordenados por orientandos ativos
     */
    @Query(value = """
        SELECT
            docente_id,
            programa_id,
            docente_nome,
            docente_email,
            docente_categoria,
            total_orientandos,
            orientandos_ativos,
            orientandos_titulados,
            orientandos_evadidos,
            disciplinas_ministradas,
            bancas_participadas,
            bancas_qualificacao,
            bancas_defesa,
            total_publicacoes,
            total_citacoes,
            h_index,
            i10_index
        FROM academic.mv_producao_docente
        WHERE programa_id = :programaId
          AND orientandos_ativos > 0
        ORDER BY orientandos_ativos DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Map<String, Object>> findTopDocentesByOrientandosAtivos(
            @Param("programaId") Long programaId,
            @Param("limit") int limit
    );
}
