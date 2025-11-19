package br.edu.ppg.hub.integration.reports.service;

import br.edu.ppg.hub.academic.domain.enums.TipoCurso;
import br.edu.ppg.hub.integration.reports.dto.DashboardResponseDTO;
import br.edu.ppg.hub.integration.reports.dto.EvasaoConclusaoDTO;
import br.edu.ppg.hub.integration.reports.dto.ProducaoDocenteDTO;
import br.edu.ppg.hub.integration.reports.dto.ProgramaStatsDTO;
import br.edu.ppg.hub.integration.reports.repository.EvasaoConclusaoRepository;
import br.edu.ppg.hub.integration.reports.repository.ProducaoDocenteRepository;
import br.edu.ppg.hub.integration.reports.repository.ProgramaStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço orquestrador de relatórios e dashboards.
 * <p>
 * Responsável por:
 * - Buscar dados das views materializadas
 * - Converter Maps para DTOs
 * - Orquestrar geração de relatórios em diferentes formatos
 * - Atualizar views materializadas
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ProgramaStatsRepository programaStatsRepository;
    private final ProducaoDocenteRepository producaoDocenteRepository;
    private final EvasaoConclusaoRepository evasaoConclusaoRepository;
    private final PdfReportService pdfReportService;
    private final ExcelReportService excelReportService;
    private final CsvReportService csvReportService;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Obtém dashboard completo de um programa.
     *
     * @param programaId ID do programa
     * @return Dashboard com estatísticas, top docentes e análise de evasão
     */
    public DashboardResponseDTO getDashboardPrograma(Long programaId) {
        log.info("Obtendo dashboard para programa ID: {}", programaId);

        try {
            // Buscar estatísticas gerais
            ProgramaStatsDTO stats = getProgramaStats(programaId);

            // Buscar top 10 docentes por H-index
            List<ProducaoDocenteDTO> topDocentes = getTopDocentes(programaId, 10);

            // Buscar análise de evasão dos últimos 5 anos
            int anoAtual = java.time.Year.now().getValue();
            List<EvasaoConclusaoDTO> evasaoPorAno = getEvasaoPorPeriodo(programaId, anoAtual - 5, anoAtual);

            // Preparar dados para gráficos
            Map<String, Object> graficos = prepararDadosGraficos(stats, topDocentes, evasaoPorAno);

            return DashboardResponseDTO.builder()
                    .estatisticas(stats)
                    .topDocentes(topDocentes)
                    .evasaoPorAno(evasaoPorAno)
                    .graficos(graficos)
                    .build();

        } catch (Exception e) {
            log.error("Erro ao obter dashboard do programa {}: {}", programaId, e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar dashboard", e);
        }
    }

    /**
     * Obtém estatísticas de um programa.
     *
     * @param programaId ID do programa
     * @return Estatísticas do programa
     */
    public ProgramaStatsDTO getProgramaStats(Long programaId) {
        log.debug("Buscando estatísticas do programa ID: {}", programaId);

        Optional<Map<String, Object>> optionalData = programaStatsRepository.findByProgramaId(programaId);

        if (optionalData.isEmpty()) {
            log.warn("Nenhuma estatística encontrada para programa ID: {}", programaId);
            return null;
        }

        return mapToProgramaStatsDTO(optionalData.get());
    }

    /**
     * Obtém top N docentes por produtividade (H-index).
     *
     * @param programaId ID do programa
     * @param limit Número máximo de resultados
     * @return Lista de docentes ordenados por H-index
     */
    public List<ProducaoDocenteDTO> getTopDocentes(Long programaId, int limit) {
        log.debug("Buscando top {} docentes do programa ID: {}", limit, programaId);

        List<Map<String, Object>> data = producaoDocenteRepository.findTopDocentesByHIndex(programaId, limit);

        return data.stream()
                .map(this::mapToProducaoDocenteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtém análise de evasão/conclusão de um programa.
     *
     * @param programaId ID do programa
     * @return Lista de análises por coorte
     */
    public List<EvasaoConclusaoDTO> getEvasaoConclusao(Long programaId) {
        log.debug("Buscando análise de evasão/conclusão do programa ID: {}", programaId);

        List<Map<String, Object>> data = evasaoConclusaoRepository.findByProgramaId(programaId);

        return data.stream()
                .map(this::mapToEvasaoConclusaoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtém análise de evasão/conclusão de um período.
     *
     * @param programaId ID do programa
     * @param anoInicio Ano inicial
     * @param anoFim Ano final
     * @return Lista de análises do período
     */
    public List<EvasaoConclusaoDTO> getEvasaoPorPeriodo(Long programaId, int anoInicio, int anoFim) {
        log.debug("Buscando análise de evasão/conclusão do programa ID: {} - período {}-{}",
                programaId, anoInicio, anoFim);

        List<Map<String, Object>> data = evasaoConclusaoRepository.findByProgramaIdAndPeriodo(
                programaId, anoInicio, anoFim);

        return data.stream()
                .map(this::mapToEvasaoConclusaoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Exporta estatísticas do programa em PDF.
     *
     * @param programaId ID do programa
     * @param outputStream Stream de saída
     * @throws IOException Em caso de erro
     */
    public void exportProgramaStatsPDF(Long programaId, OutputStream outputStream) throws IOException {
        log.info("Exportando estatísticas do programa {} em PDF", programaId);

        ProgramaStatsDTO stats = getProgramaStats(programaId);
        if (stats == null) {
            throw new IllegalArgumentException("Programa não encontrado: " + programaId);
        }

        pdfReportService.generateProgramaStatsPDF(stats, outputStream);
    }

    /**
     * Exporta produção docente em Excel.
     *
     * @param programaId ID do programa
     * @param outputStream Stream de saída
     * @throws IOException Em caso de erro
     */
    public void exportProducaoDocenteExcel(Long programaId, OutputStream outputStream) throws IOException {
        log.info("Exportando produção docente do programa {} em Excel", programaId);

        ProgramaStatsDTO stats = getProgramaStats(programaId);
        if (stats == null) {
            throw new IllegalArgumentException("Programa não encontrado: " + programaId);
        }

        List<Map<String, Object>> data = producaoDocenteRepository.findByProgramaId(programaId);
        List<ProducaoDocenteDTO> producoes = data.stream()
                .map(this::mapToProducaoDocenteDTO)
                .collect(Collectors.toList());

        excelReportService.generateProducaoDocenteExcel(producoes, stats.getProgramaNome(), outputStream);
    }

    /**
     * Exporta análise de evasão em CSV.
     *
     * @param programaId ID do programa
     * @param outputStream Stream de saída
     * @throws IOException Em caso de erro
     */
    public void exportEvasaoCSV(Long programaId, OutputStream outputStream) throws IOException {
        log.info("Exportando análise de evasão do programa {} em CSV", programaId);

        ProgramaStatsDTO stats = getProgramaStats(programaId);
        if (stats == null) {
            throw new IllegalArgumentException("Programa não encontrado: " + programaId);
        }

        List<EvasaoConclusaoDTO> evasoes = getEvasaoConclusao(programaId);

        csvReportService.generateEvasaoConclusaoCSV(evasoes, stats.getProgramaNome(), outputStream);
    }

    /**
     * Exporta dashboard completo em PDF.
     *
     * @param programaId ID do programa
     * @param outputStream Stream de saída
     * @throws IOException Em caso de erro
     */
    public void exportDashboardPDF(Long programaId, OutputStream outputStream) throws IOException {
        log.info("Exportando dashboard completo do programa {} em PDF", programaId);

        DashboardResponseDTO dashboard = getDashboardPrograma(programaId);

        if (dashboard.getEstatisticas() == null) {
            throw new IllegalArgumentException("Programa não encontrado: " + programaId);
        }

        pdfReportService.generateDashboardPDF(
                dashboard.getEstatisticas(),
                dashboard.getTopDocentes(),
                dashboard.getEvasaoPorAno(),
                outputStream
        );
    }

    /**
     * Atualiza todas as views materializadas.
     * <p>
     * Executa a função PostgreSQL que faz refresh concorrente de todas as views.
     * </p>
     */
    @Transactional
    public void refreshMaterializedViews() {
        log.info("Iniciando refresh das views materializadas");

        try {
            jdbcTemplate.execute("SELECT academic.refresh_materialized_views()");
            log.info("Views materializadas atualizadas com sucesso");

        } catch (Exception e) {
            log.error("Erro ao atualizar views materializadas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar views materializadas", e);
        }
    }

    // ===== Métodos auxiliares de conversão =====

    /**
     * Converte Map para ProgramaStatsDTO.
     */
    private ProgramaStatsDTO mapToProgramaStatsDTO(Map<String, Object> data) {
        return ProgramaStatsDTO.builder()
                .programaId(getLong(data, "programa_id"))
                .programaNome(getString(data, "programa_nome"))
                .programaSigla(getString(data, "programa_sigla"))
                .totalDocentes(getInteger(data, "total_docentes"))
                .docentesPermanentes(getInteger(data, "docentes_permanentes"))
                .totalDiscentes(getInteger(data, "total_discentes"))
                .mestrandos(getInteger(data, "mestrandos"))
                .doutorandos(getInteger(data, "doutorandos"))
                .discentesAtivos(getInteger(data, "discentes_ativos"))
                .titulados(getInteger(data, "titulados"))
                .totalDisciplinas(getInteger(data, "total_disciplinas"))
                .ofertasAtivas(getInteger(data, "ofertas_ativas"))
                .mediaNotas(getBigDecimal(data, "media_notas"))
                .build();
    }

    /**
     * Converte Map para ProducaoDocenteDTO.
     */
    private ProducaoDocenteDTO mapToProducaoDocenteDTO(Map<String, Object> data) {
        return ProducaoDocenteDTO.builder()
                .docenteId(getLong(data, "docente_id"))
                .programaId(getLong(data, "programa_id"))
                .docenteNome(getString(data, "docente_nome"))
                .docenteEmail(getString(data, "docente_email"))
                .docenteCategoria(getString(data, "docente_categoria"))
                .totalOrientandos(getInteger(data, "total_orientandos"))
                .orientandosAtivos(getInteger(data, "orientandos_ativos"))
                .orientandosTitulados(getInteger(data, "orientandos_titulados"))
                .orientandosEvadidos(getInteger(data, "orientandos_evadidos"))
                .disciplinasMinistradas(getInteger(data, "disciplinas_ministradas"))
                .bancasParticipadas(getInteger(data, "bancas_participadas"))
                .bancasQualificacao(getInteger(data, "bancas_qualificacao"))
                .bancasDefesa(getInteger(data, "bancas_defesa"))
                .totalPublicacoes(getInteger(data, "total_publicacoes"))
                .totalCitacoes(getInteger(data, "total_citacoes"))
                .hIndex(getInteger(data, "h_index"))
                .i10Index(getInteger(data, "i10_index"))
                .build();
    }

    /**
     * Converte Map para EvasaoConclusaoDTO.
     */
    private EvasaoConclusaoDTO mapToEvasaoConclusaoDTO(Map<String, Object> data) {
        return EvasaoConclusaoDTO.builder()
                .programaId(getLong(data, "programa_id"))
                .programaNome(getString(data, "programa_nome"))
                .programaSigla(getString(data, "programa_sigla"))
                .tipoCurso(TipoCurso.fromString(getString(data, "tipo_curso")))
                .anoIngresso(getInteger(data, "ano_ingresso"))
                .totalIngressantes(getInteger(data, "total_ingressantes"))
                .totalTitulados(getInteger(data, "total_titulados"))
                .totalEvadidos(getInteger(data, "total_evadidos"))
                .totalCursando(getInteger(data, "total_cursando"))
                .totalTrancados(getInteger(data, "total_trancados"))
                .taxaConclusao(getBigDecimal(data, "taxa_conclusao"))
                .taxaEvasao(getBigDecimal(data, "taxa_evasao"))
                .taxaCursando(getBigDecimal(data, "taxa_cursando"))
                .tempoMedioTitulacao(getBigDecimal(data, "tempo_medio_titulacao"))
                .build();
    }

    /**
     * Prepara dados estruturados para gráficos no dashboard.
     */
    private Map<String, Object> prepararDadosGraficos(ProgramaStatsDTO stats,
                                                       List<ProducaoDocenteDTO> topDocentes,
                                                       List<EvasaoConclusaoDTO> evasaoPorAno) {
        Map<String, Object> graficos = new HashMap<>();

        // Gráfico de pizza: Distribuição de discentes
        graficos.put("distribuicaoDiscentes", Map.of(
                "labels", List.of("Mestrado", "Doutorado"),
                "values", List.of(stats.getMestrandos(), stats.getDoutorandos())
        ));

        // Gráfico de barras: Top 5 docentes por H-index
        List<ProducaoDocenteDTO> top5 = topDocentes.stream().limit(5).toList();
        graficos.put("topDocentesHIndex", Map.of(
                "labels", top5.stream().map(ProducaoDocenteDTO::getDocenteNome).toList(),
                "values", top5.stream().map(ProducaoDocenteDTO::getHIndex).toList()
        ));

        // Gráfico de linha: Evolução de evasão por ano
        graficos.put("evolucaoEvasao", Map.of(
                "anos", evasaoPorAno.stream().map(EvasaoConclusaoDTO::getAnoIngresso).distinct().sorted().toList(),
                "taxasEvasao", evasaoPorAno.stream()
                        .collect(Collectors.groupingBy(EvasaoConclusaoDTO::getAnoIngresso,
                                Collectors.averagingDouble(e -> e.getTaxaEvasao().doubleValue())))
        ));

        return graficos;
    }

    // ===== Métodos utilitários de extração de dados do Map =====

    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.valueOf(value.toString());
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.valueOf(value.toString());
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        return new BigDecimal(value.toString());
    }
}
