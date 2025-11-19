package br.edu.ppg.hub.integration.openalex.service;

import br.edu.ppg.hub.integration.openalex.client.OpenAlexClient;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexAuthorDTO;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexResponseDTO;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexWorkDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para OpenAlexService.
 * <p>
 * Testa:
 * - Busca de autores por ORCID
 * - Busca de trabalhos por autor
 * - Busca de trabalho por DOI
 * - Cache de respostas
 * - Tratamento de erros
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.3
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OpenAlexService - Testes Unitários")
class OpenAlexServiceTest {

    @Mock
    private OpenAlexClient openAlexClient;

    @InjectMocks
    private OpenAlexService openAlexService;

    private OpenAlexAuthorDTO mockAuthor;
    private OpenAlexResponseDTO<OpenAlexAuthorDTO> mockAuthorResponse;
    private OpenAlexResponseDTO<OpenAlexWorkDTO> mockWorksResponse;

    @BeforeEach
    void setUp() {
        // Preparar autor mock
        Map<String, Object> summaryStats = new HashMap<>();
        summaryStats.put("h_index", 25);
        summaryStats.put("i10_index", 50);
        summaryStats.put("2yr_mean_citedness", 3.5);

        mockAuthor = OpenAlexAuthorDTO.builder()
                .id("https://openalex.org/A1234567890")
                .orcid("https://orcid.org/0000-0001-2345-6789")
                .display_name("Dr. Test Author")
                .works_count(100)
                .cited_by_count(500)
                .summary_stats(summaryStats)
                .build();

        // Preparar resposta de autor
        mockAuthorResponse = OpenAlexResponseDTO.<OpenAlexAuthorDTO>builder()
                .results(List.of(mockAuthor))
                .build();

        // Preparar trabalhos mock
        OpenAlexWorkDTO work1 = OpenAlexWorkDTO.builder()
                .id("https://openalex.org/W1111111111")
                .doi("10.1234/test.2023.001")
                .title("Test Article 1")
                .publication_year(2023)
                .cited_by_count(25)
                .build();

        OpenAlexWorkDTO work2 = OpenAlexWorkDTO.builder()
                .id("https://openalex.org/W2222222222")
                .doi("10.1234/test.2022.002")
                .title("Test Article 2")
                .publication_year(2022)
                .cited_by_count(15)
                .build();

        mockWorksResponse = OpenAlexResponseDTO.<OpenAlexWorkDTO>builder()
                .results(List.of(work1, work2))
                .build();
    }

    @Test
    @DisplayName("Deve buscar autor por ORCID com sucesso")
    void shouldSearchAuthorByOrcid_Success() {
        // Given
        String orcid = "0000-0001-2345-6789";
        when(openAlexClient.searchAuthors(isNull(), anyString(), eq(1)))
                .thenReturn(mockAuthorResponse);

        // When
        OpenAlexAuthorDTO result = openAlexService.searchAuthorByOrcid(orcid);

        // Then
        assertNotNull(result);
        assertEquals("https://openalex.org/A1234567890", result.getId());
        assertEquals("https://orcid.org/0000-0001-2345-6789", result.getOrcid());
        assertEquals("Dr. Test Author", result.getDisplay_name());
        assertEquals(100, result.getWorks_count());
        assertEquals(500, result.getCited_by_count());
        assertEquals(25, result.getHIndex());

        verify(openAlexClient, times(1)).searchAuthors(isNull(), eq("orcid:" + orcid), eq(1));
    }

    @Test
    @DisplayName("Deve retornar null quando autor não encontrado por ORCID")
    void shouldReturnNull_WhenAuthorNotFoundByOrcid() {
        // Given
        String orcid = "0000-0000-0000-0000";
        OpenAlexResponseDTO<OpenAlexAuthorDTO> emptyResponse = OpenAlexResponseDTO.<OpenAlexAuthorDTO>builder()
                .results(List.of())
                .build();

        when(openAlexClient.searchAuthors(isNull(), anyString(), eq(1)))
                .thenReturn(emptyResponse);

        // When
        OpenAlexAuthorDTO result = openAlexService.searchAuthorByOrcid(orcid);

        // Then
        assertNull(result);
        verify(openAlexClient, times(1)).searchAuthors(isNull(), eq("orcid:" + orcid), eq(1));
    }

    @Test
    @DisplayName("Deve buscar trabalhos por ID de autor com sucesso")
    void shouldSearchWorksByAuthor_Success() {
        // Given
        String authorId = "A1234567890";
        when(openAlexClient.searchWorks(anyString(), isNull(), eq(200)))
                .thenReturn(mockWorksResponse);

        // When
        List<OpenAlexWorkDTO> result = openAlexService.searchWorksByAuthor(authorId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Article 1", result.get(0).getTitle());
        assertEquals(2023, result.get(0).getPublication_year());
        assertEquals("Test Article 2", result.get(1).getTitle());
        assertEquals(2022, result.get(1).getPublication_year());

        verify(openAlexClient, times(1)).searchWorks(eq("author.id:" + authorId), isNull(), eq(200));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando autor não tem trabalhos")
    void shouldReturnEmptyList_WhenAuthorHasNoWorks() {
        // Given
        String authorId = "A0000000000";
        OpenAlexResponseDTO<OpenAlexWorkDTO> emptyResponse = OpenAlexResponseDTO.<OpenAlexWorkDTO>builder()
                .results(List.of())
                .build();

        when(openAlexClient.searchWorks(anyString(), isNull(), eq(200)))
                .thenReturn(emptyResponse);

        // When
        List<OpenAlexWorkDTO> result = openAlexService.searchWorksByAuthor(authorId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(openAlexClient, times(1)).searchWorks(eq("author.id:" + authorId), isNull(), eq(200));
    }

    @Test
    @DisplayName("Deve buscar trabalho por DOI com sucesso")
    void shouldGetWorkByDoi_Success() {
        // Given
        String doi = "10.1234/test.2023.001";
        OpenAlexWorkDTO mockWork = OpenAlexWorkDTO.builder()
                .id("https://openalex.org/W1111111111")
                .doi(doi)
                .title("Test Article by DOI")
                .publication_year(2023)
                .cited_by_count(30)
                .build();

        when(openAlexClient.getWorkByDoi(anyString()))
                .thenReturn(mockWork);

        // When
        OpenAlexWorkDTO result = openAlexService.getWorkByDoi(doi);

        // Then
        assertNotNull(result);
        assertEquals("https://openalex.org/W1111111111", result.getId());
        assertEquals(doi, result.getDoi());
        assertEquals("Test Article by DOI", result.getTitle());
        assertEquals(2023, result.getPublication_year());
        assertEquals(30, result.getCited_by_count());

        verify(openAlexClient, times(1)).getWorkByDoi(eq(doi));
    }

    @Test
    @DisplayName("Deve retornar null quando trabalho não encontrado por DOI")
    void shouldReturnNull_WhenWorkNotFoundByDoi() {
        // Given
        String doi = "10.9999/notfound";
        when(openAlexClient.getWorkByDoi(anyString()))
                .thenReturn(null);

        // When
        OpenAlexWorkDTO result = openAlexService.getWorkByDoi(doi);

        // Then
        assertNull(result);
        verify(openAlexClient, times(1)).getWorkByDoi(eq(doi));
    }

    @Test
    @DisplayName("Deve normalizar DOI removendo prefixo https://doi.org/")
    void shouldNormalizeDoi_RemovingHttpsPrefix() {
        // Given
        String doiWithPrefix = "https://doi.org/10.1234/test.2023.001";
        String expectedDoi = "10.1234/test.2023.001";

        OpenAlexWorkDTO mockWork = OpenAlexWorkDTO.builder()
                .doi(expectedDoi)
                .title("Test Article")
                .build();

        when(openAlexClient.getWorkByDoi(eq(expectedDoi)))
                .thenReturn(mockWork);

        // When
        OpenAlexWorkDTO result = openAlexService.getWorkByDoi(doiWithPrefix);

        // Then
        assertNotNull(result);
        verify(openAlexClient, times(1)).getWorkByDoi(eq(expectedDoi));
    }

    @Test
    @DisplayName("Deve extrair estatísticas do autor corretamente")
    void shouldExtractAuthorStatistics_Correctly() {
        // Given - já configurado no setUp()

        // When
        Integer hIndex = mockAuthor.getHIndex();
        Integer i10Index = mockAuthor.getI10Index();
        Double meanCitedness = mockAuthor.getTwoYearMeanCitedness();

        // Then
        assertEquals(25, hIndex);
        assertEquals(50, i10Index);
        assertEquals(3.5, meanCitedness);
    }

    @Test
    @DisplayName("Deve lidar com resposta null do cliente")
    void shouldHandleNullResponseFromClient() {
        // Given
        String orcid = "0000-0001-2345-6789";
        when(openAlexClient.searchAuthors(isNull(), anyString(), eq(1)))
                .thenReturn(null);

        // When
        OpenAlexAuthorDTO result = openAlexService.searchAuthorByOrcid(orcid);

        // Then
        assertNull(result);
        verify(openAlexClient, times(1)).searchAuthors(isNull(), anyString(), eq(1));
    }

    @Test
    @DisplayName("Deve verificar se resposta tem resultados")
    void shouldCheckIfResponseHasResults() {
        // Given
        OpenAlexResponseDTO<OpenAlexAuthorDTO> responseWithResults = mockAuthorResponse;
        OpenAlexResponseDTO<OpenAlexAuthorDTO> emptyResponse = OpenAlexResponseDTO.<OpenAlexAuthorDTO>builder()
                .results(List.of())
                .build();

        // Then
        assertTrue(responseWithResults.hasResults());
        assertFalse(emptyResponse.hasResults());
        assertEquals(1, responseWithResults.getResultsSize());
        assertEquals(0, emptyResponse.getResultsSize());
    }
}
