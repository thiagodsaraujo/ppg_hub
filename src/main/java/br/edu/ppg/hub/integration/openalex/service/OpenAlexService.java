package br.edu.ppg.hub.integration.openalex.service;

import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.MetricaDocente;
import br.edu.ppg.hub.academic.infrastructure.repository.DocenteRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.MetricaDocenteRepository;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.auth.infrastructure.repository.UsuarioRepository;
import br.edu.ppg.hub.integration.openalex.client.OpenAlexClient;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexAuthorDTO;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexResponseDTO;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexWorkDTO;
import br.edu.ppg.hub.shared.exception.OpenAlexException;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Service para integração com a API OpenAlex.
 *
 * Responsável por:
 * - Sincronizar métricas de docentes com OpenAlex
 * - Buscar dados de autores e publicações
 * - Cachear respostas da API
 * - Atualizar dados dos usuários e métricas
 *
 * @author PPG Hub
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAlexService {

    private final OpenAlexClient openAlexClient;
    private final DocenteRepository docenteRepository;
    private final MetricaDocenteRepository metricaDocenteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Sincroniza métricas de um docente específico usando seu ORCID.
     *
     * Busca dados no OpenAlex e atualiza:
     * - ID do autor no OpenAlex (no Usuario)
     * - Data da última sincronização (no Usuario)
     * - Métricas acadêmicas (cria nova MetricaDocente)
     *
     * @param docenteId ID do docente
     * @throws ResourceNotFoundException se docente não for encontrado
     * @throws OpenAlexException se houver erro na sincronização
     */
    @Transactional
    public void syncDocenteMetrics(Long docenteId) {
        log.info("Iniciando sincronização de métricas do docente: {}", docenteId);

        // Buscar docente
        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        Usuario usuario = docente.getUsuario();

        // Verificar se possui ORCID
        if (usuario.getOrcid() == null || usuario.getOrcid().isBlank()) {
            log.warn("Docente {} não possui ORCID cadastrado. Sincronização cancelada.", docenteId);
            throw new OpenAlexException("Docente não possui ORCID cadastrado");
        }

        try {
            // Normalizar ORCID (remover prefixo se houver)
            String orcid = normalizeOrcid(usuario.getOrcid());

            // Buscar autor no OpenAlex pelo ORCID
            OpenAlexAuthorDTO author = searchAuthorByOrcid(orcid);

            if (author == null) {
                log.warn("Autor com ORCID {} não encontrado no OpenAlex", orcid);
                throw new OpenAlexException("Autor não encontrado no OpenAlex");
            }

            // Atualizar dados do usuário
            String authorId = extractOpenAlexId(author.getId());
            usuario.setOpenalexAuthorId(authorId);
            usuario.setUltimoSyncOpenalex(LocalDateTime.now());
            usuarioRepository.save(usuario);

            // Buscar trabalhos do autor
            List<OpenAlexWorkDTO> works = searchWorksByAuthor(authorId);

            // Calcular métricas
            Integer totalPublicacoes = author.getWorks_count();
            Integer totalCitacoes = author.getCited_by_count();
            Integer hIndex = author.getHIndex();
            Integer publicacoesUltimos5Anos = calculateRecentWorks(works);

            // Criar nova métrica
            MetricaDocente metrica = MetricaDocente.builder()
                    .docente(docente)
                    .hIndex(hIndex)
                    .totalPublicacoes(totalPublicacoes)
                    .totalCitacoes(totalCitacoes)
                    .publicacoesUltimos5Anos(publicacoesUltimos5Anos)
                    .fonte("OpenAlex")
                    .dataColeta(LocalDateTime.now())
                    .build();

            metricaDocenteRepository.save(metrica);

            log.info("Métricas do docente {} sincronizadas com sucesso. H-index: {}, Publicações: {}, Citações: {}",
                    docenteId, hIndex, totalPublicacoes, totalCitacoes);

        } catch (Exception e) {
            log.error("Erro ao sincronizar métricas do docente {}: {}", docenteId, e.getMessage(), e);
            throw new OpenAlexException("Erro ao sincronizar métricas: " + e.getMessage(), e);
        }
    }

    /**
     * Busca autor pelo ORCID no OpenAlex (com cache).
     *
     * @param orcid ORCID do autor (apenas números: 0000-0001-2345-6789)
     * @return dados do autor ou null se não encontrado
     */
    @Cacheable(value = "openalex", key = "'author:' + #orcid")
    public OpenAlexAuthorDTO searchAuthorByOrcid(String orcid) {
        log.debug("Buscando autor no OpenAlex com ORCID: {}", orcid);

        try {
            String filter = "orcid:" + orcid;
            OpenAlexResponseDTO<OpenAlexAuthorDTO> response = openAlexClient.searchAuthors(null, filter, 1);

            if (response != null && response.hasResults()) {
                OpenAlexAuthorDTO author = response.getResults().get(0);
                log.debug("Autor encontrado: {} ({})", author.getDisplay_name(), author.getId());
                return author;
            }

            log.warn("Nenhum autor encontrado com ORCID: {}", orcid);
            return null;

        } catch (Exception e) {
            log.error("Erro ao buscar autor com ORCID {}: {}", orcid, e.getMessage(), e);
            throw new OpenAlexException("Erro ao buscar autor no OpenAlex", e);
        }
    }

    /**
     * Busca trabalhos de um autor pelo ID do OpenAlex (com cache).
     *
     * @param authorId ID do autor no OpenAlex (ex: A1234567890)
     * @return lista de trabalhos
     */
    @Cacheable(value = "openalex", key = "'works:' + #authorId")
    public List<OpenAlexWorkDTO> searchWorksByAuthor(String authorId) {
        log.debug("Buscando trabalhos do autor: {}", authorId);

        try {
            String filter = "author.id:" + authorId;
            OpenAlexResponseDTO<OpenAlexWorkDTO> response = openAlexClient.searchWorks(filter, null, 200);

            if (response != null && response.hasResults()) {
                log.debug("Encontrados {} trabalhos para o autor {}", response.getResultsSize(), authorId);
                return response.getResults();
            }

            log.warn("Nenhum trabalho encontrado para o autor: {}", authorId);
            return List.of();

        } catch (Exception e) {
            log.error("Erro ao buscar trabalhos do autor {}: {}", authorId, e.getMessage(), e);
            throw new OpenAlexException("Erro ao buscar trabalhos no OpenAlex", e);
        }
    }

    /**
     * Busca trabalho por DOI (com cache).
     *
     * @param doi DOI da publicação
     * @return dados do trabalho ou null se não encontrado
     */
    @Cacheable(value = "openalex", key = "'work:' + #doi")
    public OpenAlexWorkDTO getWorkByDoi(String doi) {
        log.debug("Buscando trabalho no OpenAlex com DOI: {}", doi);

        try {
            // Normalizar DOI (remover prefixo se houver)
            String normalizedDoi = normalizeDoi(doi);
            OpenAlexWorkDTO work = openAlexClient.getWorkByDoi(normalizedDoi);

            if (work != null) {
                log.debug("Trabalho encontrado: {}", work.getTitle());
                return work;
            }

            log.warn("Trabalho não encontrado com DOI: {}", doi);
            return null;

        } catch (Exception e) {
            log.error("Erro ao buscar trabalho com DOI {}: {}", doi, e.getMessage(), e);
            throw new OpenAlexException("Erro ao buscar trabalho no OpenAlex", e);
        }
    }

    /**
     * Sincroniza métricas de todos os docentes que possuem ORCID.
     *
     * Busca todos os docentes ativos com ORCID e sincroniza suas métricas.
     * Continua a execução mesmo se houver erro em algum docente.
     */
    @Transactional
    public void syncAllDocentesMetrics() {
        log.info("Iniciando sincronização de métricas de todos os docentes");

        // Buscar todos os usuários que têm ORCID
        List<Usuario> usuariosComOrcid = usuarioRepository.findAll().stream()
                .filter(u -> u.getOrcid() != null && !u.getOrcid().isBlank())
                .toList();

        log.info("Encontrados {} usuários com ORCID cadastrado", usuariosComOrcid.size());

        int sucessos = 0;
        int falhas = 0;

        for (Usuario usuario : usuariosComOrcid) {
            // Buscar docente associado ao usuário
            Optional<Docente> docenteOpt = docenteRepository.findByUsuarioId(usuario.getId());

            if (docenteOpt.isEmpty()) {
                log.debug("Usuário {} não é docente, pulando...", usuario.getId());
                continue;
            }

            Docente docente = docenteOpt.get();

            try {
                syncDocenteMetrics(docente.getId());
                sucessos++;
            } catch (Exception e) {
                log.error("Erro ao sincronizar docente {}: {}", docente.getId(), e.getMessage());
                falhas++;
            }
        }

        log.info("Sincronização concluída. Sucessos: {}, Falhas: {}", sucessos, falhas);
    }

    // ===========================
    // Métodos auxiliares privados
    // ===========================

    /**
     * Normaliza ORCID removendo prefixos e mantendo apenas os números.
     *
     * @param orcid ORCID completo ou apenas números
     * @return ORCID normalizado (ex: 0000-0001-2345-6789)
     */
    private String normalizeOrcid(String orcid) {
        if (orcid == null) {
            return null;
        }

        // Remover prefixo https://orcid.org/ se existir
        String normalized = orcid.replace("https://orcid.org/", "")
                                  .replace("http://orcid.org/", "")
                                  .trim();

        return normalized;
    }

    /**
     * Normaliza DOI removendo prefixos.
     *
     * @param doi DOI completo ou apenas código
     * @return DOI normalizado
     */
    private String normalizeDoi(String doi) {
        if (doi == null) {
            return null;
        }

        // Remover prefixo https://doi.org/ se existir
        return doi.replace("https://doi.org/", "")
                  .replace("http://doi.org/", "")
                  .trim();
    }

    /**
     * Extrai o ID do OpenAlex da URL completa.
     *
     * @param fullId URL completa (ex: https://openalex.org/A1234567890)
     * @return apenas o ID (ex: A1234567890)
     */
    private String extractOpenAlexId(String fullId) {
        if (fullId == null) {
            return null;
        }

        // Extrair apenas o ID da URL
        String[] parts = fullId.split("/");
        return parts[parts.length - 1];
    }

    /**
     * Calcula o número de trabalhos publicados nos últimos 5 anos.
     *
     * @param works lista de trabalhos
     * @return quantidade de trabalhos recentes
     */
    private Integer calculateRecentWorks(List<OpenAlexWorkDTO> works) {
        if (works == null || works.isEmpty()) {
            return 0;
        }

        int currentYear = Year.now().getValue();
        int fiveYearsAgo = currentYear - 5;

        return (int) works.stream()
                .filter(work -> work.getPublication_year() != null)
                .filter(work -> work.getPublication_year() >= fiveYearsAgo)
                .count();
    }
}
