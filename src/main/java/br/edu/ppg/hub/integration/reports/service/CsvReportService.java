package br.edu.ppg.hub.integration.reports.service;

import br.edu.ppg.hub.integration.reports.dto.EvasaoConclusaoDTO;
import br.edu.ppg.hub.integration.reports.dto.ProducaoDocenteDTO;
import br.edu.ppg.hub.integration.reports.dto.ProgramaStatsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Serviço responsável pela geração de relatórios em formato CSV.
 * <p>
 * Gera arquivos CSV padrão RFC 4180 com encoding UTF-8 e BOM para compatibilidade
 * com Excel e outras ferramentas de planilha.
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CsvReportService {

    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_LINE_BREAK = "\r\n";
    private static final char CSV_QUOTE = '"';

    /**
     * Gera CSV de estatísticas do programa.
     *
     * @param stats Estatísticas do programa
     * @param outputStream Stream de saída para escrita do CSV
     * @throws IOException Em caso de erro na geração do CSV
     */
    public void generateProgramaStatsCSV(ProgramaStatsDTO stats, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório CSV de estatísticas para programa: {}", stats.getProgramaSigla());

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            // BOM para UTF-8 (compatibilidade Excel)
            writer.write('\ufeff');

            // Cabeçalho
            writer.write("RELATÓRIO DE ESTATÍSTICAS DO PROGRAMA");
            writer.write(CSV_LINE_BREAK);
            writer.write("Programa: " + escapeCsv(stats.getProgramaNome()));
            writer.write(CSV_LINE_BREAK);
            writer.write(CSV_LINE_BREAK);

            // Dados em formato chave-valor
            writer.write("Métrica,Valor");
            writer.write(CSV_LINE_BREAK);

            writeCsvRow(writer, "Sigla", stats.getProgramaSigla());
            writeCsvRow(writer, "Total de Docentes", String.valueOf(stats.getTotalDocentes()));
            writeCsvRow(writer, "Docentes Permanentes", String.valueOf(stats.getDocentesPermanentes()));
            writeCsvRow(writer, "Taxa de Permanentes (%)",
                    String.format("%.2f", stats.calcularTaxaDocentesPermanentes()));
            writeCsvRow(writer, "Total de Discentes", String.valueOf(stats.getTotalDiscentes()));
            writeCsvRow(writer, "Mestrandos", String.valueOf(stats.getMestrandos()));
            writeCsvRow(writer, "Doutorandos", String.valueOf(stats.getDoutorandos()));
            writeCsvRow(writer, "Discentes Ativos", String.valueOf(stats.getDiscentesAtivos()));
            writeCsvRow(writer, "Titulados", String.valueOf(stats.getTitulados()));
            writeCsvRow(writer, "Total de Disciplinas", String.valueOf(stats.getTotalDisciplinas()));
            writeCsvRow(writer, "Ofertas Ativas", String.valueOf(stats.getOfertasAtivas()));
            writeCsvRow(writer, "Média de Notas",
                    stats.getMediaNotas() != null ? String.format("%.2f", stats.getMediaNotas()) : "N/A");

            writer.flush();
            log.info("Relatório CSV de estatísticas gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório CSV de estatísticas: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório CSV", e);
        }
    }

    /**
     * Gera CSV de produção docente.
     *
     * @param producoes Lista de produções docentes
     * @param programaNome Nome do programa
     * @param outputStream Stream de saída para escrita do CSV
     * @throws IOException Em caso de erro na geração do CSV
     */
    public void generateProducaoDocenteCSV(List<ProducaoDocenteDTO> producoes, String programaNome, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório CSV de produção docente para: {}", programaNome);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            // BOM para UTF-8
            writer.write('\ufeff');

            // Cabeçalho
            writer.write("RELATÓRIO DE PRODUÇÃO DOCENTE - " + escapeCsv(programaNome));
            writer.write(CSV_LINE_BREAK);
            writer.write(CSV_LINE_BREAK);

            // Headers da tabela
            String[] headers = {
                "Docente", "E-mail", "Categoria", "Total Orientandos", "Orientandos Ativos",
                "Orientandos Titulados", "Orientandos Evadidos", "Disciplinas Ministradas",
                "Bancas Participadas", "Bancas Qualificação", "Bancas Defesa",
                "Total Publicações", "Total Citações", "H-index", "i10-index"
            };
            writer.write(String.join(CSV_SEPARATOR, headers));
            writer.write(CSV_LINE_BREAK);

            // Dados
            for (ProducaoDocenteDTO p : producoes) {
                String[] values = {
                    escapeCsv(p.getDocenteNome()),
                    escapeCsv(p.getDocenteEmail()),
                    escapeCsv(p.getDocenteCategoria()),
                    String.valueOf(p.getTotalOrientandos()),
                    String.valueOf(p.getOrientandosAtivos()),
                    String.valueOf(p.getOrientandosTitulados()),
                    String.valueOf(p.getOrientandosEvadidos()),
                    String.valueOf(p.getDisciplinasMinistradas()),
                    String.valueOf(p.getBancasParticipadas()),
                    String.valueOf(p.getBancasQualificacao()),
                    String.valueOf(p.getBancasDefesa()),
                    String.valueOf(p.getTotalPublicacoes()),
                    String.valueOf(p.getTotalCitacoes()),
                    String.valueOf(p.getHIndex()),
                    String.valueOf(p.getI10Index())
                };
                writer.write(String.join(CSV_SEPARATOR, values));
                writer.write(CSV_LINE_BREAK);
            }

            writer.flush();
            log.info("Relatório CSV de produção docente gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório CSV de produção docente: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório CSV", e);
        }
    }

    /**
     * Gera CSV de evasão e conclusão.
     *
     * @param evasoes Lista de dados de evasão/conclusão
     * @param programaNome Nome do programa
     * @param outputStream Stream de saída para escrita do CSV
     * @throws IOException Em caso de erro na geração do CSV
     */
    public void generateEvasaoConclusaoCSV(List<EvasaoConclusaoDTO> evasoes, String programaNome, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório CSV de evasão/conclusão para: {}", programaNome);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            // BOM para UTF-8
            writer.write('\ufeff');

            // Cabeçalho
            writer.write("RELATÓRIO DE EVASÃO E CONCLUSÃO - " + escapeCsv(programaNome));
            writer.write(CSV_LINE_BREAK);
            writer.write(CSV_LINE_BREAK);

            // Headers da tabela
            String[] headers = {
                "Ano Ingresso", "Tipo Curso", "Total Ingressantes", "Total Titulados",
                "Total Evadidos", "Total Cursando", "Total Trancados",
                "Taxa Conclusão (%)", "Taxa Evasão (%)", "Taxa Cursando (%)",
                "Tempo Médio Titulação (anos)"
            };
            writer.write(String.join(CSV_SEPARATOR, headers));
            writer.write(CSV_LINE_BREAK);

            // Dados
            for (EvasaoConclusaoDTO e : evasoes) {
                String[] values = {
                    String.valueOf(e.getAnoIngresso()),
                    escapeCsv(e.getTipoCurso().getDescricao()),
                    String.valueOf(e.getTotalIngressantes()),
                    String.valueOf(e.getTotalTitulados()),
                    String.valueOf(e.getTotalEvadidos()),
                    String.valueOf(e.getTotalCursando()),
                    String.valueOf(e.getTotalTrancados()),
                    formatDecimal(e.getTaxaConclusao()),
                    formatDecimal(e.getTaxaEvasao()),
                    formatDecimal(e.getTaxaCursando()),
                    e.getTempoMedioTitulacao() != null ? formatDecimal(e.getTempoMedioTitulacao()) : "N/A"
                };
                writer.write(String.join(CSV_SEPARATOR, values));
                writer.write(CSV_LINE_BREAK);
            }

            writer.flush();
            log.info("Relatório CSV de evasão/conclusão gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório CSV de evasão/conclusão: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório CSV", e);
        }
    }

    // ===== Métodos auxiliares =====

    /**
     * Escreve uma linha CSV com dois valores (chave-valor).
     *
     * @param writer BufferedWriter para escrita
     * @param key Chave (primeira coluna)
     * @param value Valor (segunda coluna)
     * @throws IOException Em caso de erro na escrita
     */
    private void writeCsvRow(BufferedWriter writer, String key, String value) throws IOException {
        writer.write(escapeCsv(key));
        writer.write(CSV_SEPARATOR);
        writer.write(escapeCsv(value));
        writer.write(CSV_LINE_BREAK);
    }

    /**
     * Escapa valores para formato CSV (RFC 4180).
     * <p>
     * - Envolve valores com aspas se contiverem vírgula, aspas ou quebra de linha
     * - Duplica aspas internas
     * </p>
     *
     * @param value Valor a ser escapado
     * @return Valor escapado para CSV
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        boolean needsQuoting = value.contains(CSV_SEPARATOR) ||
                               value.contains(String.valueOf(CSV_QUOTE)) ||
                               value.contains("\n") ||
                               value.contains("\r");

        if (!needsQuoting) {
            return value;
        }

        // Duplicar aspas internas e envolver com aspas
        String escaped = value.replace(String.valueOf(CSV_QUOTE), String.valueOf(CSV_QUOTE) + CSV_QUOTE);
        return CSV_QUOTE + escaped + CSV_QUOTE;
    }

    /**
     * Formata número decimal para CSV.
     *
     * @param value Valor decimal
     * @return String formatada com 2 casas decimais
     */
    private String formatDecimal(Number value) {
        if (value == null) {
            return "0.00";
        }
        return String.format("%.2f", value.doubleValue());
    }
}
