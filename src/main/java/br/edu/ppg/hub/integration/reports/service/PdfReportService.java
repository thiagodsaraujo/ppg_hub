package br.edu.ppg.hub.integration.reports.service;

import br.edu.ppg.hub.integration.reports.dto.EvasaoConclusaoDTO;
import br.edu.ppg.hub.integration.reports.dto.ProducaoDocenteDTO;
import br.edu.ppg.hub.integration.reports.dto.ProgramaStatsDTO;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Serviço responsável pela geração de relatórios em formato PDF.
 * <p>
 * Utiliza iText 8 para criação de documentos PDF com formatação profissional,
 * incluindo cabeçalhos, tabelas e rodapés.
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PdfReportService {

    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(240, 240, 240);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Gera relatório PDF de estatísticas do programa.
     *
     * @param stats Estatísticas do programa
     * @param outputStream Stream de saída para escrita do PDF
     * @throws IOException Em caso de erro na geração do PDF
     */
    public void generateProgramaStatsPDF(ProgramaStatsDTO stats, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório PDF de estatísticas para programa: {}", stats.getProgramaSigla());

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Cabeçalho
            addHeader(document, "RELATÓRIO DE ESTATÍSTICAS DO PROGRAMA", stats.getProgramaNome());

            // Informações Gerais
            addSection(document, "Informações Gerais");
            Table infoTable = createInfoTable(stats);
            document.add(infoTable);

            // Corpo Docente
            document.add(new Paragraph("\n"));
            addSection(document, "Corpo Docente");
            Table docentesTable = createDocentesTable(stats);
            document.add(docentesTable);

            // Corpo Discente
            document.add(new Paragraph("\n"));
            addSection(document, "Corpo Discente");
            Table discentesTable = createDiscentesTable(stats);
            document.add(discentesTable);

            // Disciplinas
            document.add(new Paragraph("\n"));
            addSection(document, "Disciplinas e Ofertas");
            Table disciplinasTable = createDisciplinasTable(stats);
            document.add(disciplinasTable);

            // Rodapé
            addFooter(document);

            document.close();
            log.info("Relatório PDF de estatísticas gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório PDF de estatísticas: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório PDF", e);
        }
    }

    /**
     * Gera relatório PDF de produção docente.
     *
     * @param producoes Lista de produções docentes
     * @param programaNome Nome do programa
     * @param outputStream Stream de saída para escrita do PDF
     * @throws IOException Em caso de erro na geração do PDF
     */
    public void generateProducaoDocentePDF(List<ProducaoDocenteDTO> producoes, String programaNome, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório PDF de produção docente para: {}", programaNome);

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Cabeçalho
            addHeader(document, "RELATÓRIO DE PRODUÇÃO DOCENTE", programaNome);

            // Tabela de produção
            addSection(document, "Ranking de Produtividade");
            Table table = createProducaoDocenteTable(producoes);
            document.add(table);

            // Rodapé
            addFooter(document);

            document.close();
            log.info("Relatório PDF de produção docente gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório PDF de produção docente: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório PDF", e);
        }
    }

    /**
     * Gera relatório PDF de evasão e conclusão.
     *
     * @param evasoes Lista de dados de evasão/conclusão
     * @param programaNome Nome do programa
     * @param outputStream Stream de saída para escrita do PDF
     * @throws IOException Em caso de erro na geração do PDF
     */
    public void generateEvasaoConclusaoPDF(List<EvasaoConclusaoDTO> evasoes, String programaNome, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório PDF de evasão/conclusão para: {}", programaNome);

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Cabeçalho
            addHeader(document, "RELATÓRIO DE EVASÃO E CONCLUSÃO", programaNome);

            // Tabela de evasão
            addSection(document, "Análise por Coorte de Ingresso");
            Table table = createEvasaoConclusaoTable(evasoes);
            document.add(table);

            // Rodapé
            addFooter(document);

            document.close();
            log.info("Relatório PDF de evasão/conclusão gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório PDF de evasão/conclusão: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório PDF", e);
        }
    }

    /**
     * Gera relatório PDF completo (dashboard).
     *
     * @param stats Estatísticas do programa
     * @param producoes Lista de produções docentes
     * @param evasoes Lista de dados de evasão/conclusão
     * @param outputStream Stream de saída para escrita do PDF
     * @throws IOException Em caso de erro na geração do PDF
     */
    public void generateDashboardPDF(ProgramaStatsDTO stats, List<ProducaoDocenteDTO> producoes,
                                      List<EvasaoConclusaoDTO> evasoes, OutputStream outputStream) throws IOException {
        log.info("Gerando relatório PDF completo do dashboard para: {}", stats.getProgramaSigla());

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Cabeçalho principal
            addHeader(document, "DASHBOARD DO PROGRAMA", stats.getProgramaNome());

            // Estatísticas gerais
            addSection(document, "1. Estatísticas Gerais");
            document.add(createInfoTable(stats));
            document.add(createDocentesTable(stats));
            document.add(createDiscentesTable(stats));

            // Produção docente
            document.add(new Paragraph("\n"));
            addSection(document, "2. Top 10 Docentes por Produtividade");
            document.add(createProducaoDocenteTable(producoes.stream().limit(10).toList()));

            // Evasão e conclusão
            document.add(new Paragraph("\n"));
            addSection(document, "3. Evasão e Conclusão (Últimos 5 Anos)");
            document.add(createEvasaoConclusaoTable(evasoes));

            // Rodapé
            addFooter(document);

            document.close();
            log.info("Relatório PDF completo do dashboard gerado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao gerar relatório PDF do dashboard: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar relatório PDF", e);
        }
    }

    // ===== Métodos auxiliares de formatação =====

    private void addHeader(Document document, String title, String subtitle) {
        Paragraph titlePara = new Paragraph(title)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(HEADER_COLOR);
        document.add(titlePara);

        Paragraph subtitlePara = new Paragraph(subtitle)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitlePara);
    }

    private void addSection(Document document, String sectionTitle) {
        Paragraph section = new Paragraph(sectionTitle)
                .setFontSize(12)
                .setBold()
                .setFontColor(HEADER_COLOR)
                .setMarginTop(10)
                .setMarginBottom(5);
        document.add(section);
    }

    private void addFooter(Document document) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        Paragraph footer = new Paragraph("Relatório gerado em: " + timestamp)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20)
                .setFontColor(ColorConstants.GRAY);
        document.add(footer);
    }

    private Table createInfoTable(ProgramaStatsDTO stats) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "Programa:", stats.getProgramaNome());
        addTableRow(table, "Sigla:", stats.getProgramaSigla());
        addTableRow(table, "Média de Notas:", stats.getMediaNotas() != null ?
                String.format("%.2f", stats.getMediaNotas()) : "N/A");

        return table;
    }

    private Table createDocentesTable(ProgramaStatsDTO stats) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "Total de Docentes:", String.valueOf(stats.getTotalDocentes()));
        addTableRow(table, "Docentes Permanentes:", String.valueOf(stats.getDocentesPermanentes()));
        addTableRow(table, "Taxa de Permanentes:",
                String.format("%.2f%%", stats.calcularTaxaDocentesPermanentes()));

        return table;
    }

    private Table createDiscentesTable(ProgramaStatsDTO stats) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "Total de Discentes:", String.valueOf(stats.getTotalDiscentes()));
        addTableRow(table, "Mestrandos:", String.valueOf(stats.getMestrandos()));
        addTableRow(table, "Doutorandos:", String.valueOf(stats.getDoutorandos()));
        addTableRow(table, "Discentes Ativos:", String.valueOf(stats.getDiscentesAtivos()));
        addTableRow(table, "Titulados:", String.valueOf(stats.getTitulados()));

        return table;
    }

    private Table createDisciplinasTable(ProgramaStatsDTO stats) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "Total de Disciplinas:", String.valueOf(stats.getTotalDisciplinas()));
        addTableRow(table, "Ofertas Ativas:", String.valueOf(stats.getOfertasAtivas()));

        return table;
    }

    private Table createProducaoDocenteTable(List<ProducaoDocenteDTO> producoes) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 15, 15, 15, 15, 10}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Header
        addHeaderCell(table, "Docente");
        addHeaderCell(table, "Orientandos");
        addHeaderCell(table, "Disciplinas");
        addHeaderCell(table, "Publicações");
        addHeaderCell(table, "Citações");
        addHeaderCell(table, "H-index");

        // Rows
        for (ProducaoDocenteDTO p : producoes) {
            table.addCell(new Cell().add(new Paragraph(p.getDocenteNome()).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getTotalOrientandos())).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getDisciplinasMinistradas())).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getTotalPublicacoes())).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getTotalCitacoes())).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getHIndex())).setFontSize(9)).setBold());
        }

        return table;
    }

    private Table createEvasaoConclusaoTable(List<EvasaoConclusaoDTO> evasoes) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 15, 15, 15, 15, 15, 15}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Header
        addHeaderCell(table, "Ano");
        addHeaderCell(table, "Curso");
        addHeaderCell(table, "Ingressantes");
        addHeaderCell(table, "Titulados");
        addHeaderCell(table, "Evadidos");
        addHeaderCell(table, "Taxa Conclusão");
        addHeaderCell(table, "Taxa Evasão");

        // Rows
        for (EvasaoConclusaoDTO e : evasoes) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(e.getAnoIngresso())).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(e.getTipoCurso().getDescricao()).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(e.getTotalIngressantes())).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(e.getTotalTitulados())).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(e.getTotalEvadidos())).setFontSize(9)));

            Cell conclusaoCell = new Cell().add(new Paragraph(
                    String.format("%.2f%%", e.getTaxaConclusao())).setFontSize(9));
            if (e.isConclusaoBaixa()) {
                conclusaoCell.setBackgroundColor(new DeviceRgb(255, 200, 200));
            }
            table.addCell(conclusaoCell);

            Cell evasaoCell = new Cell().add(new Paragraph(
                    String.format("%.2f%%", e.getTaxaEvasao())).setFontSize(9));
            if (e.isEvasaoCritica()) {
                evasaoCell.setBackgroundColor(new DeviceRgb(255, 200, 200));
            }
            table.addCell(evasaoCell);
        }

        return table;
    }

    private void addTableRow(Table table, String label, String value) {
        Cell labelCell = new Cell().add(new Paragraph(label).setBold().setFontSize(10))
                .setBackgroundColor(LIGHT_GRAY);
        Cell valueCell = new Cell().add(new Paragraph(value).setFontSize(10));
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addHeaderCell(Table table, String text) {
        Cell cell = new Cell().add(new Paragraph(text).setBold().setFontSize(10))
                .setBackgroundColor(HEADER_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(cell);
    }
}
