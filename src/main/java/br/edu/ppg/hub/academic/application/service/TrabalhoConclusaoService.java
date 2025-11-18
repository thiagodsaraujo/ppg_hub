package br.edu.ppg.hub.academic.application.service;

import br.edu.ppg.hub.academic.application.dto.trabalho_conclusao.TrabalhoConclusaoCreateDTO;
import br.edu.ppg.hub.academic.application.dto.trabalho_conclusao.TrabalhoConclusaoMapper;
import br.edu.ppg.hub.academic.application.dto.trabalho_conclusao.TrabalhoConclusaoResponseDTO;
import br.edu.ppg.hub.academic.application.dto.trabalho_conclusao.TrabalhoConclusaoUpdateDTO;
import br.edu.ppg.hub.academic.domain.enums.StatusTrabalho;
import br.edu.ppg.hub.academic.domain.enums.TipoTrabalho;
import br.edu.ppg.hub.academic.domain.model.Discente;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.TrabalhoConclusao;
import br.edu.ppg.hub.academic.infrastructure.repository.DiscenteRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.DocenteRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.TrabalhoConclusaoRepository;
import br.edu.ppg.hub.shared.exception.BusinessException;
import br.edu.ppg.hub.shared.exception.DuplicateResourceException;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações com Trabalhos de Conclusão.
 * Implementa toda a lógica de negócio relacionada a dissertações e teses.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrabalhoConclusaoService {

    private final TrabalhoConclusaoRepository trabalhoConclusaoRepository;
    private final DiscenteRepository discenteRepository;
    private final DocenteRepository docenteRepository;
    private final TrabalhoConclusaoMapper trabalhoConclusaoMapper;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    /**
     * Cria um novo trabalho de conclusão.
     *
     * @param dto Dados do trabalho
     * @return Trabalho criado
     */
    @Transactional
    public TrabalhoConclusaoResponseDTO criar(TrabalhoConclusaoCreateDTO dto) {
        log.info("Criando novo trabalho de conclusão para discente: {}", dto.getDiscenteId());

        // Validar se discente já possui trabalho cadastrado
        if (trabalhoConclusaoRepository.existsByDiscenteId(dto.getDiscenteId())) {
            throw new DuplicateResourceException("Discente já possui um trabalho de conclusão cadastrado");
        }

        // Buscar entidades relacionadas
        Discente discente = discenteRepository.findById(dto.getDiscenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        // Validar status do discente
        if (!discente.getStatus().name().equals("QUALIFICADO") &&
            !discente.getStatus().name().equals("DEFENDENDO") &&
            !discente.getStatus().name().equals("CURSANDO")) {
            throw new BusinessException("Discente deve estar com status CURSANDO, QUALIFICADO ou DEFENDENDO");
        }

        Docente orientador = docenteRepository.findById(dto.getOrientadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Orientador não encontrado"));

        // Validar se orientador está ativo
        if (!orientador.isAtivo()) {
            throw new BusinessException("Orientador não está ativo");
        }

        Docente coorientador = null;
        if (dto.getCoorientadorId() != null) {
            coorientador = docenteRepository.findById(dto.getCoorientadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coorientador não encontrado"));

            if (!coorientador.isAtivo()) {
                throw new BusinessException("Coorientador não está ativo");
            }
        }

        // Criar trabalho
        TrabalhoConclusao trabalho = trabalhoConclusaoMapper.toEntity(dto, discente, orientador, coorientador);

        trabalho = trabalhoConclusaoRepository.save(trabalho);

        log.info("Trabalho de conclusão criado com sucesso: {}", trabalho.getId());
        return trabalhoConclusaoMapper.toResponseDTO(trabalho);
    }

    /**
     * Busca trabalho por ID.
     *
     * @param id ID do trabalho
     * @return Trabalho encontrado
     */
    @Transactional(readOnly = true)
    public TrabalhoConclusaoResponseDTO buscarPorId(Long id) {
        log.debug("Buscando trabalho de conclusão: {}", id);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

        return trabalhoConclusaoMapper.toResponseDTO(trabalho);
    }

    /**
     * Lista todos os trabalhos com paginação.
     *
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    @Transactional(readOnly = true)
    public Page<TrabalhoConclusaoResponseDTO> listarTodos(Pageable pageable) {
        log.debug("Listando todos os trabalhos de conclusão");

        return trabalhoConclusaoRepository.findAll(pageable)
                .map(trabalhoConclusaoMapper::toResponseDTO);
    }

    /**
     * Busca trabalhos por discente.
     *
     * @param discenteId ID do discente
     * @return Trabalho do discente
     */
    @Transactional(readOnly = true)
    public TrabalhoConclusaoResponseDTO buscarPorDiscente(Long discenteId) {
        log.debug("Buscando trabalho do discente: {}", discenteId);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findByDiscenteId(discenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado para este discente"));

        return trabalhoConclusaoMapper.toResponseDTO(trabalho);
    }

    /**
     * Busca trabalhos por orientador.
     *
     * @param orientadorId ID do orientador
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    @Transactional(readOnly = true)
    public Page<TrabalhoConclusaoResponseDTO> buscarPorOrientador(Long orientadorId, Pageable pageable) {
        log.debug("Buscando trabalhos do orientador: {}", orientadorId);

        return trabalhoConclusaoRepository.findByOrientadorId(orientadorId, pageable)
                .map(trabalhoConclusaoMapper::toResponseDTO);
    }

    /**
     * Busca trabalhos por coorientador.
     *
     * @param coorientadorId ID do coorientador
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    @Transactional(readOnly = true)
    public Page<TrabalhoConclusaoResponseDTO> buscarPorCoorientador(Long coorientadorId, Pageable pageable) {
        log.debug("Buscando trabalhos do coorientador: {}", coorientadorId);

        return trabalhoConclusaoRepository.findByCoorientadorId(coorientadorId, pageable)
                .map(trabalhoConclusaoMapper::toResponseDTO);
    }

    /**
     * Atualiza um trabalho de conclusão.
     *
     * @param id ID do trabalho
     * @param dto Dados para atualização
     * @return Trabalho atualizado
     */
    @Transactional
    public TrabalhoConclusaoResponseDTO atualizar(Long id, TrabalhoConclusaoUpdateDTO dto) {
        log.info("Atualizando trabalho de conclusão: {}", id);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

        // Validar se trabalho pode ser editado
        if (trabalho.getStatus() == StatusTrabalho.PUBLICADO) {
            throw new BusinessException("Trabalho publicado não pode ser editado");
        }

        Docente orientador = null;
        if (dto.getOrientadorId() != null) {
            orientador = docenteRepository.findById(dto.getOrientadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Orientador não encontrado"));

            if (!orientador.isAtivo()) {
                throw new BusinessException("Orientador não está ativo");
            }
        }

        Docente coorientador = null;
        if (dto.getCoorientadorId() != null) {
            coorientador = docenteRepository.findById(dto.getCoorientadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coorientador não encontrado"));

            if (!coorientador.isAtivo()) {
                throw new BusinessException("Coorientador não está ativo");
            }
        }

        trabalhoConclusaoMapper.updateEntity(trabalho, dto, orientador, coorientador);

        trabalho = trabalhoConclusaoRepository.save(trabalho);

        log.info("Trabalho de conclusão atualizado com sucesso: {}", id);
        return trabalhoConclusaoMapper.toResponseDTO(trabalho);
    }

    /**
     * Submete um trabalho para avaliação.
     *
     * @param id ID do trabalho
     * @return Trabalho submetido
     */
    @Transactional
    public TrabalhoConclusaoResponseDTO submeter(Long id) {
        log.info("Submetendo trabalho de conclusão: {}", id);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

        // Validar se trabalho pode ser submetido
        if (trabalho.getStatus() != StatusTrabalho.EM_PREPARACAO) {
            throw new BusinessException("Apenas trabalhos em preparação podem ser submetidos");
        }

        if (!trabalho.prontoParaSubmissao()) {
            throw new BusinessException("Trabalho não está pronto para submissão. Verifique se título e resumo estão preenchidos");
        }

        trabalho.setStatus(StatusTrabalho.QUALIFICADO);
        trabalho = trabalhoConclusaoRepository.save(trabalho);

        log.info("Trabalho submetido com sucesso: {}", id);
        return trabalhoConclusaoMapper.toResponseDTO(trabalho);
    }

    /**
     * Aprova um trabalho de conclusão.
     *
     * @param id ID do trabalho
     * @return Trabalho aprovado
     */
    @Transactional
    public TrabalhoConclusaoResponseDTO aprovar(Long id) {
        log.info("Aprovando trabalho de conclusão: {}", id);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

        if (trabalho.getStatus() != StatusTrabalho.DEFENDIDO) {
            throw new BusinessException("Apenas trabalhos defendidos podem ser aprovados");
        }

        trabalho.setStatus(StatusTrabalho.APROVADO);
        trabalho = trabalhoConclusaoRepository.save(trabalho);

        log.info("Trabalho aprovado com sucesso: {}", id);
        return trabalhoConclusaoMapper.toResponseDTO(trabalho);
    }

    /**
     * Reprova um trabalho de conclusão.
     *
     * @param id ID do trabalho
     * @return Trabalho reprovado
     */
    @Transactional
    public TrabalhoConclusaoResponseDTO reprovar(Long id) {
        log.info("Reprovando trabalho de conclusão: {}", id);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

        if (trabalho.getStatus() != StatusTrabalho.DEFENDIDO) {
            throw new BusinessException("Apenas trabalhos defendidos podem ser reprovados");
        }

        trabalho.setStatus(StatusTrabalho.EM_PREPARACAO);
        trabalho = trabalhoConclusaoRepository.save(trabalho);

        log.info("Trabalho reprovado: {}", id);
        return trabalhoConclusaoMapper.toResponseDTO(trabalho);
    }

    /**
     * Publica um trabalho no repositório institucional.
     *
     * @param id ID do trabalho
     * @return Trabalho publicado
     */
    @Transactional
    public TrabalhoConclusaoResponseDTO publicar(Long id) {
        log.info("Publicando trabalho de conclusão: {}", id);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

        if (trabalho.getStatus() != StatusTrabalho.APROVADO) {
            throw new BusinessException("Apenas trabalhos aprovados podem ser publicados");
        }

        if (!trabalho.temArquivo()) {
            throw new BusinessException("Trabalho deve ter arquivo PDF anexado para ser publicado");
        }

        trabalho.setStatus(StatusTrabalho.PUBLICADO);

        // Simular geração de URI no repositório
        trabalho.setUriRepositorio("http://repositorio.ppghub.edu.br/handle/" + id);
        trabalho.setHandleUri("http://hdl.handle.net/ppghub/" + id);
        trabalho.setUrlDownload("http://repositorio.ppghub.edu.br/bitstream/" + id + "/trabalho.pdf");

        trabalho = trabalhoConclusaoRepository.save(trabalho);

        log.info("Trabalho publicado com sucesso: {}", id);
        return trabalhoConclusaoMapper.toResponseDTO(trabalho);
    }

    /**
     * Simula upload de arquivo PDF do trabalho.
     *
     * @param id ID do trabalho
     * @param arquivo Arquivo PDF
     * @return Trabalho com arquivo anexado
     */
    @Transactional
    public TrabalhoConclusaoResponseDTO uploadArquivo(Long id, MultipartFile arquivo) {
        log.info("Fazendo upload de arquivo para trabalho: {}", id);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

        // Validar arquivo
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Arquivo não pode ser vazio");
        }

        if (arquivo.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("Arquivo muito grande. Tamanho máximo: 50MB");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new BusinessException("Arquivo deve ser do tipo PDF");
        }

        // Simular salvamento do arquivo (em produção, usar S3/MinIO)
        String nomeArquivo = arquivo.getOriginalFilename();
        String path = "/uploads/trabalhos/" + id + "/" + nomeArquivo;

        trabalho.setArquivoPdf(path);
        trabalho.setTamanhoArquivoBytes(arquivo.getSize());

        trabalho = trabalhoConclusaoRepository.save(trabalho);

        log.info("Arquivo enviado com sucesso para trabalho: {}", id);
        return trabalhoConclusaoMapper.toResponseDTO(trabalho);
    }

    /**
     * Simula download do arquivo PDF do trabalho.
     *
     * @param id ID do trabalho
     * @return Conteúdo simulado do arquivo
     */
    @Transactional
    public byte[] downloadArquivo(Long id) {
        log.info("Fazendo download de arquivo do trabalho: {}", id);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

        if (!trabalho.temArquivo()) {
            throw new BusinessException("Trabalho não possui arquivo anexado");
        }

        // Incrementar contador de downloads
        trabalho.incrementarDownloads();
        trabalhoConclusaoRepository.save(trabalho);

        log.info("Download realizado para trabalho: {}", id);

        // Simular conteúdo do arquivo (em produção, buscar do S3/MinIO)
        return ("Conteúdo simulado do PDF do trabalho " + id).getBytes();
    }

    /**
     * Busca trabalhos pendentes de defesa.
     *
     * @return Lista de trabalhos pendentes
     */
    @Transactional(readOnly = true)
    public List<TrabalhoConclusaoResponseDTO> buscarTrabalhosPendentes() {
        log.debug("Buscando trabalhos pendentes de defesa");

        return trabalhoConclusaoRepository.findTrabalhosPendentesDefesa().stream()
                .map(trabalhoConclusaoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca trabalhos por tipo.
     *
     * @param tipo Tipo do trabalho
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    @Transactional(readOnly = true)
    public Page<TrabalhoConclusaoResponseDTO> buscarPorTipo(TipoTrabalho tipo, Pageable pageable) {
        log.debug("Buscando trabalhos por tipo: {}", tipo);

        return trabalhoConclusaoRepository.findByTipo(tipo, pageable)
                .map(trabalhoConclusaoMapper::toResponseDTO);
    }

    /**
     * Busca trabalhos por status.
     *
     * @param status Status do trabalho
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    @Transactional(readOnly = true)
    public Page<TrabalhoConclusaoResponseDTO> buscarPorStatus(StatusTrabalho status, Pageable pageable) {
        log.debug("Buscando trabalhos por status: {}", status);

        return trabalhoConclusaoRepository.findByStatus(status, pageable)
                .map(trabalhoConclusaoMapper::toResponseDTO);
    }

    /**
     * Busca trabalhos por período de defesa.
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    @Transactional(readOnly = true)
    public Page<TrabalhoConclusaoResponseDTO> buscarPorPeriodoDefesa(
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) {
        log.debug("Buscando trabalhos defendidos entre {} e {}", dataInicio, dataFim);

        return trabalhoConclusaoRepository.findByDataDefesaBetween(dataInicio, dataFim, pageable)
                .map(trabalhoConclusaoMapper::toResponseDTO);
    }

    /**
     * Deleta um trabalho de conclusão.
     *
     * @param id ID do trabalho
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando trabalho de conclusão: {}", id);

        TrabalhoConclusao trabalho = trabalhoConclusaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

        // Validar se pode deletar
        if (trabalho.getStatus() == StatusTrabalho.PUBLICADO) {
            throw new BusinessException("Trabalhos publicados não podem ser deletados");
        }

        trabalhoConclusaoRepository.delete(trabalho);

        log.info("Trabalho de conclusão deletado com sucesso: {}", id);
    }
}
