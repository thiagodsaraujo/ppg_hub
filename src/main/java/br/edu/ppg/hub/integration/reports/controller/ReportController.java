package br.edu.ppg.hub.integration.reports.controller;

import br.edu.ppg.hub.integration.reports.dto.DashboardResponseDTO;
import br.edu.ppg.hub.integration.reports.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller REST para geração de relatórios e dashboards.
 * <p>
 * Endpoints disponíveis:
 * - GET /dashboard/programa/{id} - Dashboard JSON completo
 * - GET /programa/{id}/stats.pdf - Estatísticas em PDF
 * - GET /programa/{id}/producao.xlsx - Produção docente em Excel
 * - GET /programa/{id}/evasao.csv - Evasão e conclusão em CSV
 * - POST /refresh-views - Atualizar views materializadas (ADMIN)
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Relatórios", description = "APIs para geração de relatórios e dashboards")
@SecurityRequirement(name = "bearer-jwt")
public class ReportController {

    private final ReportService reportService;
    private static final DateTimeFormatter FILENAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Obtém dashboard completo de um programa em formato JSON.
     *
     * @param programaId ID do programa
     * @return Dashboard com estatísticas, top docentes e análise de evasão
     */
    @GetMapping("/dashboard/programa/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN', 'DOCENTE')")
    @Operation(
        summary = "Obter dashboard do programa",
        description = "Retorna dashboard completo com estatísticas, ranking de docentes e análise de evasão/conclusão"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard obtido com sucesso",
            content = @Content(schema = @Schema(implementation = DashboardResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Programa não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<DashboardResponseDTO> getDashboardPrograma(
            @Parameter(description = "ID do programa", required = true)
            @PathVariable("id") Long programaId) {

        log.info("Requisição de dashboard para programa ID: {}", programaId);

        try {
            DashboardResponseDTO dashboard = reportService.getDashboardPrograma(programaId);

            if (dashboard.getEstatisticas() == null) {
                log.warn("Programa não encontrado: {}", programaId);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            log.error("Erro ao gerar dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exporta estatísticas do programa em formato PDF.
     *
     * @param programaId ID do programa
     * @return Arquivo PDF para download
     */
    @GetMapping("/programa/{id}/stats.pdf")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN', 'DOCENTE')")
    @Operation(
        summary = "Exportar estatísticas em PDF",
        description = "Gera relatório PDF com estatísticas consolidadas do programa"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Programa não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar PDF")
    })
    public ResponseEntity<byte[]> exportProgramaStatsPDF(
            @Parameter(description = "ID do programa", required = true)
            @PathVariable("id") Long programaId) {

        log.info("Requisição de export PDF de estatísticas para programa ID: {}", programaId);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            reportService.exportProgramaStatsPDF(programaId, outputStream);

            byte[] pdfBytes = outputStream.toByteArray();
            String filename = generateFilename("estatisticas_programa", "pdf");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (IllegalArgumentException e) {
            log.warn("Programa não encontrado: {}", programaId);
            return ResponseEntity.notFound().build();

        } catch (IOException e) {
            log.error("Erro ao gerar PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exporta produção docente em formato Excel (.xlsx).
     *
     * @param programaId ID do programa
     * @return Arquivo Excel para download
     */
    @GetMapping("/programa/{id}/producao.xlsx")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN', 'DOCENTE')")
    @Operation(
        summary = "Exportar produção docente em Excel",
        description = "Gera planilha Excel com métricas de produtividade de todos os docentes do programa"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Excel gerado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Programa não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar Excel")
    })
    public ResponseEntity<byte[]> exportProducaoDocenteExcel(
            @Parameter(description = "ID do programa", required = true)
            @PathVariable("id") Long programaId) {

        log.info("Requisição de export Excel de produção docente para programa ID: {}", programaId);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            reportService.exportProducaoDocenteExcel(programaId, outputStream);

            byte[] excelBytes = outputStream.toByteArray();
            String filename = generateFilename("producao_docente", "xlsx");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);

        } catch (IllegalArgumentException e) {
            log.warn("Programa não encontrado: {}", programaId);
            return ResponseEntity.notFound().build();

        } catch (IOException e) {
            log.error("Erro ao gerar Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exporta análise de evasão e conclusão em formato CSV.
     *
     * @param programaId ID do programa
     * @return Arquivo CSV para download
     */
    @GetMapping("/programa/{id}/evasao.csv")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN', 'DOCENTE')")
    @Operation(
        summary = "Exportar evasão/conclusão em CSV",
        description = "Gera arquivo CSV com análise de evasão e conclusão por coorte de ingresso"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV gerado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Programa não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar CSV")
    })
    public ResponseEntity<byte[]> exportEvasaoCSV(
            @Parameter(description = "ID do programa", required = true)
            @PathVariable("id") Long programaId) {

        log.info("Requisição de export CSV de evasão para programa ID: {}", programaId);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            reportService.exportEvasaoCSV(programaId, outputStream);

            byte[] csvBytes = outputStream.toByteArray();
            String filename = generateFilename("evasao_conclusao", "csv");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(csvBytes.length);
            headers.set("Content-Encoding", "UTF-8");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);

        } catch (IllegalArgumentException e) {
            log.warn("Programa não encontrado: {}", programaId);
            return ResponseEntity.notFound().build();

        } catch (IOException e) {
            log.error("Erro ao gerar CSV: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exporta dashboard completo em formato PDF.
     *
     * @param programaId ID do programa
     * @return Arquivo PDF completo para download
     */
    @GetMapping("/programa/{id}/dashboard.pdf")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @Operation(
        summary = "Exportar dashboard completo em PDF",
        description = "Gera relatório PDF completo com todas as seções do dashboard"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Programa não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar PDF")
    })
    public ResponseEntity<byte[]> exportDashboardPDF(
            @Parameter(description = "ID do programa", required = true)
            @PathVariable("id") Long programaId) {

        log.info("Requisição de export PDF completo do dashboard para programa ID: {}", programaId);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            reportService.exportDashboardPDF(programaId, outputStream);

            byte[] pdfBytes = outputStream.toByteArray();
            String filename = generateFilename("dashboard_completo", "pdf");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (IllegalArgumentException e) {
            log.warn("Programa não encontrado: {}", programaId);
            return ResponseEntity.notFound().build();

        } catch (IOException e) {
            log.error("Erro ao gerar PDF do dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza todas as views materializadas.
     * <p>
     * Endpoint restrito a administradores. Executa refresh concorrente de todas as views.
     * </p>
     *
     * @return Mensagem de sucesso ou erro
     */
    @PostMapping("/refresh-views")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Atualizar views materializadas",
        description = "Executa refresh concorrente de todas as views materializadas (requer permissão ADMIN)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Views atualizadas com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN"),
        @ApiResponse(responseCode = "500", description = "Erro ao atualizar views")
    })
    public ResponseEntity<String> refreshMaterializedViews() {
        log.info("Requisição de refresh de views materializadas");

        try {
            reportService.refreshMaterializedViews();
            return ResponseEntity.ok("Views materializadas atualizadas com sucesso");

        } catch (Exception e) {
            log.error("Erro ao atualizar views: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar views materializadas: " + e.getMessage());
        }
    }

    // ===== Métodos auxiliares =====

    /**
     * Gera nome de arquivo com timestamp.
     *
     * @param prefix Prefixo do nome do arquivo
     * @param extension Extensão do arquivo (sem ponto)
     * @return Nome do arquivo formatado
     */
    private String generateFilename(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(FILENAME_DATE_FORMATTER);
        return String.format("%s_%s.%s", prefix, timestamp, extension);
    }
}
