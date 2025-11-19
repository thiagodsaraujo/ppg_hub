package br.edu.ppg.hub.integration.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository para acesso à view materializada de evasão e conclusão.
 * <p>
 * Utiliza queries nativas para acessar a view {@code academic.mv_evasao_conclusao}
 * que analisa taxas de evasão e conclusão por coorte de ingresso.
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Repository
public interface EvasaoConclusaoRepository extends JpaRepository<Object, Long> {

    /**
     * Lista análise de evasão/conclusão de um programa.
     *
     * @param programaId ID do programa
     * @return Lista de Maps com dados por coorte
     */
    @Query(value = """
        SELECT
            programa_id,
            programa_nome,
            programa_sigla,
            tipo_curso,
            ano_ingresso,
            total_ingressantes,
            total_titulados,
            total_evadidos,
            total_cursando,
            total_trancados,
            taxa_conclusao,
            taxa_evasao,
            taxa_cursando,
            tempo_medio_titulacao
        FROM academic.mv_evasao_conclusao
        WHERE programa_id = :programaId
        ORDER BY ano_ingresso DESC, tipo_curso
        """, nativeQuery = true)
    List<Map<String, Object>> findByProgramaId(@Param("programaId") Long programaId);

    /**
     * Lista análise por programa e tipo de curso.
     *
     * @param programaId ID do programa
     * @param tipoCurso Tipo de curso (MESTRADO/DOUTORADO)
     * @return Lista de Maps filtrados por tipo de curso
     */
    @Query(value = """
        SELECT
            programa_id,
            programa_nome,
            programa_sigla,
            tipo_curso,
            ano_ingresso,
            total_ingressantes,
            total_titulados,
            total_evadidos,
            total_cursando,
            total_trancados,
            taxa_conclusao,
            taxa_evasao,
            taxa_cursando,
            tempo_medio_titulacao
        FROM academic.mv_evasao_conclusao
        WHERE programa_id = :programaId
          AND tipo_curso = :tipoCurso
        ORDER BY ano_ingresso DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findByProgramaIdAndTipoCurso(
            @Param("programaId") Long programaId,
            @Param("tipoCurso") String tipoCurso
    );

    /**
     * Lista análise de um período específico.
     *
     * @param programaId ID do programa
     * @param anoInicio Ano inicial do período
     * @param anoFim Ano final do período
     * @return Lista de Maps do período
     */
    @Query(value = """
        SELECT
            programa_id,
            programa_nome,
            programa_sigla,
            tipo_curso,
            ano_ingresso,
            total_ingressantes,
            total_titulados,
            total_evadidos,
            total_cursando,
            total_trancados,
            taxa_conclusao,
            taxa_evasao,
            taxa_cursando,
            tempo_medio_titulacao
        FROM academic.mv_evasao_conclusao
        WHERE programa_id = :programaId
          AND ano_ingresso BETWEEN :anoInicio AND :anoFim
        ORDER BY ano_ingresso DESC, tipo_curso
        """, nativeQuery = true)
    List<Map<String, Object>> findByProgramaIdAndPeriodo(
            @Param("programaId") Long programaId,
            @Param("anoInicio") Integer anoInicio,
            @Param("anoFim") Integer anoFim
    );

    /**
     * Retorna coortes com taxa de evasão crítica (> 20%).
     *
     * @param programaId ID do programa
     * @return Lista de Maps com coortes críticas
     */
    @Query(value = """
        SELECT
            programa_id,
            programa_nome,
            programa_sigla,
            tipo_curso,
            ano_ingresso,
            total_ingressantes,
            total_titulados,
            total_evadidos,
            total_cursando,
            total_trancados,
            taxa_conclusao,
            taxa_evasao,
            taxa_cursando,
            tempo_medio_titulacao
        FROM academic.mv_evasao_conclusao
        WHERE programa_id = :programaId
          AND taxa_evasao > 20.0
        ORDER BY taxa_evasao DESC, ano_ingresso DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findCoortesComEvasaoCritica(@Param("programaId") Long programaId);

    /**
     * Retorna coortes com taxa de conclusão baixa (< 60%).
     *
     * @param programaId ID do programa
     * @return Lista de Maps com coortes com baixa conclusão
     */
    @Query(value = """
        SELECT
            programa_id,
            programa_nome,
            programa_sigla,
            tipo_curso,
            ano_ingresso,
            total_ingressantes,
            total_titulados,
            total_evadidos,
            total_cursando,
            total_trancados,
            taxa_conclusao,
            taxa_evasao,
            taxa_cursando,
            tempo_medio_titulacao
        FROM academic.mv_evasao_conclusao
        WHERE programa_id = :programaId
          AND taxa_conclusao < 60.0
          AND total_cursando = 0  -- Coortes que já deveriam ter finalizado
        ORDER BY taxa_conclusao ASC, ano_ingresso DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findCoortesComConclusaoBaixa(@Param("programaId") Long programaId);

    /**
     * Calcula estatísticas agregadas por programa.
     *
     * @param programaId ID do programa
     * @return Map com médias e totais
     */
    @Query(value = """
        SELECT
            AVG(taxa_conclusao) as media_taxa_conclusao,
            AVG(taxa_evasao) as media_taxa_evasao,
            AVG(tempo_medio_titulacao) as media_tempo_titulacao,
            SUM(total_ingressantes) as total_ingressantes_geral,
            SUM(total_titulados) as total_titulados_geral,
            SUM(total_evadidos) as total_evadidos_geral
        FROM academic.mv_evasao_conclusao
        WHERE programa_id = :programaId
        """, nativeQuery = true)
    Map<String, Object> findEstatisticasAgregadas(@Param("programaId") Long programaId);
}
