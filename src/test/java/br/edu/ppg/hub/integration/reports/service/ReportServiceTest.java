package br.edu.ppg.hub.integration.reports.service;

import br.edu.ppg.hub.integration.reports.dto.DashboardResponseDTO;
import br.edu.ppg.hub.integration.reports.dto.EvasaoConclusaoDTO;
import br.edu.ppg.hub.integration.reports.dto.ProducaoDocenteDTO;
import br.edu.ppg.hub.integration.reports.dto.ProgramaStatsDTO;
import br.edu.ppg.hub.integration.reports.repository.EvasaoConclusaoRepository;
import br.edu.ppg.hub.integration.reports.repository.ProducaoDocenteRepository;
import br.edu.ppg.hub.integration.reports.repository.ProgramaStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ReportService.
 * <p>
 * Testa:
 * - Obtenção de dashboard de programa
 * - Busca de estatísticas de programa
 * - Busca de top docentes
 * - Análise de evasão/conclusão
 * - Exportação de relatórios (PDF, Excel, CSV)
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.3
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService - Testes Unitários")
class ReportServiceTest {

    @Mock
    private ProgramaStatsRepository programaStatsRepository;

    @Mock
    private ProducaoDocenteRepository producaoDocenteRepository;

    @Mock
    private EvasaoConclusaoRepository evasaoConclusaoRepository;

    @Mock
    private PdfReportService pdfReportService;

    @Mock
    private ExcelReportService excelReportService;

    @Mock
    private CsvReportService csvReportService;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ReportService reportService;

    private Map<String, Object> mockProgramaData;
    private List<Map<String, Object>> mockDocentesData;
    private List<Map<String, Object>> mockEvasaoData;

    @BeforeEach
    void setUp() {
        // Mock de dados do programa
        mockProgramaData = new HashMap<>();
        mockProgramaData.put("programa_id", 1L);
        mockProgramaData.put("programa_nome", "Programa de Teste");
        mockProgramaData.put("total_docentes", 20);
        mockProgramaData.put("total_discentes", 100);
        mockProgramaData.put("total_disciplinas", 50);
        mockProgramaData.put("total_publicacoes", 200);
        mockProgramaData.put("h_index_medio", new BigDecimal("15.5"));
        mockProgramaData.put("taxa_titulacao", new BigDecimal("0.85"));

        // Mock de dados de docentes
        Map<String, Object> docente1 = new HashMap<>();
        docente1.put("docente_id", 1L);
        docente1.put("docente_nome", "Prof. Dr. João Silva");
        docente1.put("h_index", 25);
        docente1.put("total_publicacoes", 50);
        docente1.put("total_citacoes", 300);
        docente1.put("publicacoes_ultimos_5_anos", 20);

        Map<String, Object> docente2 = new HashMap<>();
        docente2.put("docente_id", 2L);
        docente2.put("docente_nome", "Profa. Dra. Maria Santos");
        docente2.put("h_index", 20);
        docente2.put("total_publicacoes", 40);
        docente2.put("total_citacoes", 250);
        docente2.put("publicacoes_ultimos_5_anos", 15);

        mockDocentesData = List.of(docente1, docente2);

        // Mock de dados de evasão
        Map<String, Object> evasao1 = new HashMap<>();
        evasao1.put("programa_id", 1L);
        evasao1.put("ano_ingresso", 2020);
        evasao1.put("total_ingressantes", 25);
        evasao1.put("total_concluintes", 20);
        evasao1.put("total_evasoes", 5);
        evasao1.put("total_ativos", 0);
        evasao1.put("taxa_conclusao", new BigDecimal("0.80"));
        evasao1.put("taxa_evasao", new BigDecimal("0.20"));

        mockEvasaoData = List.of(evasao1);
    }

    @Test
    @DisplayName("Deve obter estatísticas do programa com sucesso")
    void shouldGetProgramaStats_Success() {
        // Given
        Long programaId = 1L;
        when(programaStatsRepository.findByProgramaId(programaId))
                .thenReturn(Optional.of(mockProgramaData));

        // When
        ProgramaStatsDTO result = reportService.getProgramaStats(programaId);

        // Then
        assertNotNull(result);
        verify(programaStatsRepository, times(1)).findByProgramaId(programaId);
    }

    @Test
    @DisplayName("Deve retornar null quando programa não encontrado")
    void shouldReturnNull_WhenProgramaNotFound() {
        // Given
        Long programaId = 999L;
        when(programaStatsRepository.findByProgramaId(programaId))
                .thenReturn(Optional.empty());

        // When
        ProgramaStatsDTO result = reportService.getProgramaStats(programaId);

        // Then
        assertNull(result);
        verify(programaStatsRepository, times(1)).findByProgramaId(programaId);
    }

    @Test
    @DisplayName("Deve buscar top docentes por H-index")
    void shouldGetTopDocentes_Success() {
        // Given
        Long programaId = 1L;
        int limit = 10;
        when(producaoDocenteRepository.findTopDocentesByHIndex(programaId, limit))
                .thenReturn(mockDocentesData);

        // When
        List<ProducaoDocenteDTO> result = reportService.getTopDocentes(programaId, limit);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(producaoDocenteRepository, times(1)).findTopDocentesByHIndex(programaId, limit);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há docentes")
    void shouldReturnEmptyList_WhenNoDocentes() {
        // Given
        Long programaId = 1L;
        int limit = 10;
        when(producaoDocenteRepository.findTopDocentesByHIndex(programaId, limit))
                .thenReturn(List.of());

        // When
        List<ProducaoDocenteDTO> result = reportService.getTopDocentes(programaId, limit);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(producaoDocenteRepository, times(1)).findTopDocentesByHIndex(programaId, limit);
    }

    @Test
    @DisplayName("Deve obter análise de evasão/conclusão")
    void shouldGetEvasaoConclusao_Success() {
        // Given
        Long programaId = 1L;
        when(evasaoConclusaoRepository.findByProgramaId(programaId))
                .thenReturn(mockEvasaoData);

        // When
        List<EvasaoConclusaoDTO> result = reportService.getEvasaoConclusao(programaId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(evasaoConclusaoRepository, times(1)).findByProgramaId(programaId);
    }

    @Test
    @DisplayName("Deve obter análise de evasão por período")
    void shouldGetEvasaoPorPeriodo_Success() {
        // Given
        Long programaId = 1L;
        int anoInicio = 2020;
        int anoFim = 2024;
        when(evasaoConclusaoRepository.findByProgramaIdAndPeriodo(programaId, anoInicio, anoFim))
                .thenReturn(mockEvasaoData);

        // When
        List<EvasaoConclusaoDTO> result = reportService.getEvasaoPorPeriodo(programaId, anoInicio, anoFim);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(evasaoConclusaoRepository, times(1))
                .findByProgramaIdAndPeriodo(programaId, anoInicio, anoFim);
    }

    @Test
    @DisplayName("Deve obter dashboard completo do programa")
    void shouldGetDashboardPrograma_Success() {
        // Given
        Long programaId = 1L;
        when(programaStatsRepository.findByProgramaId(programaId))
                .thenReturn(Optional.of(mockProgramaData));
        when(producaoDocenteRepository.findTopDocentesByHIndex(eq(programaId), anyInt()))
                .thenReturn(mockDocentesData);
        when(evasaoConclusaoRepository.findByProgramaIdAndPeriodo(eq(programaId), anyInt(), anyInt()))
                .thenReturn(mockEvasaoData);

        // When
        DashboardResponseDTO result = reportService.getDashboardPrograma(programaId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getEstatisticas());
        assertNotNull(result.getTopDocentes());
        assertNotNull(result.getEvasaoPorAno());
        assertNotNull(result.getGraficos());

        verify(programaStatsRepository, times(1)).findByProgramaId(programaId);
        verify(producaoDocenteRepository, times(1)).findTopDocentesByHIndex(eq(programaId), anyInt());
        verify(evasaoConclusaoRepository, times(1))
                .findByProgramaIdAndPeriodo(eq(programaId), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Deve exportar estatísticas em PDF sem erros")
    void shouldExportProgramaStatsPDF_Success() throws IOException {
        // Given
        Long programaId = 1L;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(programaStatsRepository.findByProgramaId(programaId))
                .thenReturn(Optional.of(mockProgramaData));
        doNothing().when(pdfReportService).generateProgramaStatsPDF(any(), any());

        // When
        reportService.exportProgramaStatsPDF(programaId, outputStream);

        // Then
        verify(programaStatsRepository, times(1)).findByProgramaId(programaId);
        verify(pdfReportService, times(1)).generateProgramaStatsPDF(any(), eq(outputStream));
    }

    @Test
    @DisplayName("Deve lançar exceção ao exportar PDF de programa inexistente")
    void shouldThrowException_WhenExportingPdfForNonExistentPrograma() {
        // Given
        Long programaId = 999L;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(programaStatsRepository.findByProgramaId(programaId))
                .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reportService.exportProgramaStatsPDF(programaId, outputStream)
        );

        assertTrue(exception.getMessage().contains("Programa não encontrado"));
        verify(programaStatsRepository, times(1)).findByProgramaId(programaId);
        verify(pdfReportService, never()).generateProgramaStatsPDF(any(), any());
    }

    @Test
    @DisplayName("Deve exportar produção docente em Excel sem erros")
    void shouldExportProducaoDocenteExcel_Success() throws IOException {
        // Given
        Long programaId = 1L;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(programaStatsRepository.findByProgramaId(programaId))
                .thenReturn(Optional.of(mockProgramaData));
        when(producaoDocenteRepository.findByProgramaId(programaId))
                .thenReturn(mockDocentesData);
        doNothing().when(excelReportService).generateProducaoDocenteExcel(anyList(), anyString(), any());

        // When
        reportService.exportProducaoDocenteExcel(programaId, outputStream);

        // Then
        verify(programaStatsRepository, times(1)).findByProgramaId(programaId);
        verify(producaoDocenteRepository, times(1)).findByProgramaId(programaId);
        verify(excelReportService, times(1))
                .generateProducaoDocenteExcel(anyList(), anyString(), eq(outputStream));
    }

    @Test
    @DisplayName("Deve lançar exceção ao exportar Excel de programa inexistente")
    void shouldThrowException_WhenExportingExcelForNonExistentPrograma() {
        // Given
        Long programaId = 999L;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(programaStatsRepository.findByProgramaId(programaId))
                .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reportService.exportProducaoDocenteExcel(programaId, outputStream)
        );

        assertTrue(exception.getMessage().contains("Programa não encontrado"));
        verify(programaStatsRepository, times(1)).findByProgramaId(programaId);
        verify(excelReportService, never()).generateProducaoDocenteExcel(anyList(), anyString(), any());
    }

    @Test
    @DisplayName("Deve verificar que mocks estão configurados corretamente")
    void shouldVerifyMocksAreConfigured() {
        // Given & When & Then
        assertNotNull(programaStatsRepository);
        assertNotNull(producaoDocenteRepository);
        assertNotNull(evasaoConclusaoRepository);
        assertNotNull(pdfReportService);
        assertNotNull(excelReportService);
        assertNotNull(csvReportService);
        assertNotNull(jdbcTemplate);
        assertNotNull(reportService);
    }
}
