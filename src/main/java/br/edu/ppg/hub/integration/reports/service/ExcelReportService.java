package br.edu.ppg.hub.integration.reports.service;

import br.edu.ppg.hub.integration.reports.dto.EvasaoConclusaoDTO;
import br.edu.ppg.hub.integration.reports.dto.ProducaoDocenteDTO;
import br.edu.ppg.hub.integration.reports.dto.ProgramaStatsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Serviço responsável pela geração de relatórios em formato Excel (.xlsx).
 * <p>
 * Utiliza Apache POI para criação de planilhas Excel com formatação profissional,
 * incluindo cabeçalhos estilizados, múltiplas abas e fórmulas.
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Gera planilha Excel de estatísticas do programa.
     *
     * @param stats Estatísticas do programa
     * @param outputStream Stream de saída para escrita do Excel
     * @throws IOException Em caso de erro na geração do Excel
     */
    public void generateProgramaStatsExcel(ProgramaStatsDTO stats, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório Excel de estatísticas para programa: {}", stats.getProgramaSigla());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Estatísticas do Programa");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle labelStyle = createLabelStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("RELATÓRIO DE ESTATÍSTICAS DO PROGRAMA");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            // Subtítulo
            Row subtitleRow = sheet.createRow(rowNum++);
            Cell subtitleCell = subtitleRow.createCell(0);
            subtitleCell.setCellValue(stats.getProgramaNome());
            subtitleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

            rowNum++; // Linha em branco

            // Seção: Informações Gerais
            addSectionHeader(sheet, rowNum++, "INFORMAÇÕES GERAIS", headerStyle);
            rowNum = addDataRow(sheet, rowNum, "Programa:", stats.getProgramaNome(), labelStyle, dataStyle);
            rowNum = addDataRow(sheet, rowNum, "Sigla:", stats.getProgramaSigla(), labelStyle, dataStyle);
            rowNum = addDataRow(sheet, rowNum, "Média de Notas:",
                stats.getMediaNotas() != null ? String.format("%.2f", stats.getMediaNotas()) : "N/A",
                labelStyle, dataStyle);

            rowNum++; // Linha em branco

            // Seção: Corpo Docente
            addSectionHeader(sheet, rowNum++, "CORPO DOCENTE", headerStyle);
            rowNum = addDataRow(sheet, rowNum, "Total de Docentes:", String.valueOf(stats.getTotalDocentes()), labelStyle, dataStyle);
            rowNum = addDataRow(sheet, rowNum, "Docentes Permanentes:", String.valueOf(stats.getDocentesPermanentes()), labelStyle, dataStyle);
            rowNum = addDataRow(sheet, rowNum, "Taxa de Permanentes:",
                String.format("%.2f%%", stats.calcularTaxaDocentesPermanentes()), labelStyle, dataStyle);

            rowNum++; // Linha em branco

            // Seção: Corpo Discente
            addSectionHeader(sheet, rowNum++, "CORPO DISCENTE", headerStyle);
            rowNum = addDataRow(sheet, rowNum, "Total de Discentes:", String.valueOf(stats.getTotalDiscentes()), labelStyle, dataStyle);
            rowNum = addDataRow(sheet, rowNum, "Mestrandos:", String.valueOf(stats.getMestrandos()), labelStyle, dataStyle);
            rowNum = addDataRow(sheet, rowNum, "Doutorandos:", String.valueOf(stats.getDoutorandos()), labelStyle, dataStyle);
            rowNum = addDataRow(sheet, rowNum, "Discentes Ativos:", String.valueOf(stats.getDiscentesAtivos()), labelStyle, dataStyle);
            rowNum = addDataRow(sheet, rowNum, "Titulados:", String.valueOf(stats.getTitulados()), labelStyle, dataStyle);

            rowNum++; // Linha em branco

            // Seção: Disciplinas
            addSectionHeader(sheet, rowNum++, "DISCIPLINAS E OFERTAS", headerStyle);
            rowNum = addDataRow(sheet, rowNum, "Total de Disciplinas:", String.valueOf(stats.getTotalDisciplinas()), labelStyle, dataStyle);
            rowNum = addDataRow(sheet, rowNum, "Ofertas Ativas:", String.valueOf(stats.getOfertasAtivas()), labelStyle, dataStyle);

            // Rodapé
            rowNum += 2;
            Row footerRow = sheet.createRow(rowNum);
            Cell footerCell = footerRow.createCell(0);
            footerCell.setCellValue("Relatório gerado em: " + LocalDateTime.now().format(DATE_FORMATTER));
            CellStyle footerStyle = workbook.createCellStyle();
            Font footerFont = workbook.createFont();
            footerFont.setFontHeightInPoints((short) 9);
            footerFont.setItalic(true);
            footerStyle.setFont(footerFont);
            footerCell.setCellStyle(footerStyle);

            // Auto-size colunas
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            log.info("Relatório Excel de estatísticas gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório Excel de estatísticas: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório Excel", e);
        }
    }

    /**
     * Gera planilha Excel de produção docente.
     *
     * @param producoes Lista de produções docentes
     * @param programaNome Nome do programa
     * @param outputStream Stream de saída para escrita do Excel
     * @throws IOException Em caso de erro na geração do Excel
     */
    public void generateProducaoDocenteExcel(List<ProducaoDocenteDTO> producoes, String programaNome, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório Excel de produção docente para: {}", programaNome);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Produção Docente");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("RELATÓRIO DE PRODUÇÃO DOCENTE - " + programaNome);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            rowNum++; // Linha em branco

            // Cabeçalho da tabela
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Docente", "E-mail", "Categoria", "Orientandos", "Orientandos Ativos",
                               "Disciplinas", "Bancas", "Publicações", "Citações", "H-index"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dados
            for (ProducaoDocenteDTO p : producoes) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                createCell(row, colNum++, p.getDocenteNome(), dataStyle);
                createCell(row, colNum++, p.getDocenteEmail(), dataStyle);
                createCell(row, colNum++, p.getDocenteCategoria(), dataStyle);
                createCell(row, colNum++, p.getTotalOrientandos(), dataStyle);
                createCell(row, colNum++, p.getOrientandosAtivos(), dataStyle);
                createCell(row, colNum++, p.getDisciplinasMinistradas(), dataStyle);
                createCell(row, colNum++, p.getBancasParticipadas(), dataStyle);
                createCell(row, colNum++, p.getTotalPublicacoes(), dataStyle);
                createCell(row, colNum++, p.getTotalCitacoes(), dataStyle);

                // H-index em destaque
                Cell hIndexCell = row.createCell(colNum);
                hIndexCell.setCellValue(p.getHIndex());
                CellStyle hIndexStyle = workbook.createCellStyle();
                hIndexStyle.cloneStyleFrom(dataStyle);
                hIndexStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                hIndexStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font boldFont = workbook.createFont();
                boldFont.setBold(true);
                hIndexStyle.setFont(boldFont);
                hIndexCell.setCellStyle(hIndexStyle);
            }

            // Auto-size colunas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            log.info("Relatório Excel de produção docente gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório Excel de produção docente: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório Excel", e);
        }
    }

    /**
     * Gera planilha Excel de evasão e conclusão.
     *
     * @param evasoes Lista de dados de evasão/conclusão
     * @param programaNome Nome do programa
     * @param outputStream Stream de saída para escrita do Excel
     * @throws IOException Em caso de erro na geração do Excel
     */
    public void generateEvasaoConclusaoExcel(List<EvasaoConclusaoDTO> evasoes, String programaNome, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório Excel de evasão/conclusão para: {}", programaNome);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Evasão e Conclusão");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle percentStyle = createPercentStyle(workbook);
            CellStyle criticalStyle = createCriticalStyle(workbook);

            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("RELATÓRIO DE EVASÃO E CONCLUSÃO - " + programaNome);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            rowNum++; // Linha em branco

            // Cabeçalho da tabela
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Ano", "Curso", "Ingressantes", "Titulados", "Evadidos",
                               "Cursando", "Taxa Conclusão (%)", "Taxa Evasão (%)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dados
            for (EvasaoConclusaoDTO e : evasoes) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                createCell(row, colNum++, e.getAnoIngresso(), dataStyle);
                createCell(row, colNum++, e.getTipoCurso().getDescricao(), dataStyle);
                createCell(row, colNum++, e.getTotalIngressantes(), dataStyle);
                createCell(row, colNum++, e.getTotalTitulados(), dataStyle);
                createCell(row, colNum++, e.getTotalEvadidos(), dataStyle);
                createCell(row, colNum++, e.getTotalCursando(), dataStyle);

                // Taxa de conclusão com destaque
                Cell conclusaoCell = row.createCell(colNum++);
                conclusaoCell.setCellValue(e.getTaxaConclusao().doubleValue());
                conclusaoCell.setCellStyle(e.isConclusaoBaixa() ? criticalStyle : percentStyle);

                // Taxa de evasão com destaque
                Cell evasaoCell = row.createCell(colNum);
                evasaoCell.setCellValue(e.getTaxaEvasao().doubleValue());
                evasaoCell.setCellStyle(e.isEvasaoCritica() ? criticalStyle : percentStyle);
            }

            // Auto-size colunas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            log.info("Relatório Excel de evasão/conclusão gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório Excel de evasão/conclusão: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório Excel", e);
        }
    }

    // ===== Métodos auxiliares de formatação =====

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createPercentStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
        return style;
    }

    private CellStyle createCriticalStyle(Workbook workbook) {
        CellStyle style = createPercentStyle(workbook);
        style.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_RED.getIndex());
        style.setFont(font);
        return style;
    }

    private void addSectionHeader(Sheet sheet, int rowNum, String text, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 3));
    }

    private int addDataRow(Sheet sheet, int rowNum, String label, String value, CellStyle labelStyle, CellStyle dataStyle) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(dataStyle);

        return rowNum + 1;
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value != null) {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }
}
