package br.edu.ppg.hub.academic.application.service;

import br.edu.ppg.hub.academic.application.dto.membro_banca.MembroBancaMapper;
import br.edu.ppg.hub.academic.application.dto.membro_banca.MembroBancaResponseDTO;
import br.edu.ppg.hub.academic.domain.model.Banca;
import br.edu.ppg.hub.academic.domain.model.MembroBanca;
import br.edu.ppg.hub.academic.infrastructure.repository.BancaRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.MembroBancaRepository;
import br.edu.ppg.hub.shared.exception.BusinessException;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações com Membros de Banca.
 * Implementa a lógica de negócio relacionada aos membros de bancas examinadoras.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MembroBancaService {

    private final MembroBancaRepository membroBancaRepository;
    private final BancaRepository bancaRepository;
    private final MembroBancaMapper membroBancaMapper;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Busca membro por ID.
     *
     * @param id ID do membro
     * @return Membro encontrado
     */
    @Transactional(readOnly = true)
    public MembroBancaResponseDTO buscarPorId(Long id) {
        log.debug("Buscando membro de banca: {}", id);

        MembroBanca membro = membroBancaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de banca não encontrado"));

        return membroBancaMapper.toResponseDTO(membro);
    }

    /**
     * Busca todos os membros de uma banca.
     *
     * @param bancaId ID da banca
     * @return Lista de membros
     */
    @Transactional(readOnly = true)
    public List<MembroBancaResponseDTO> buscarPorBanca(Long bancaId) {
        log.debug("Buscando membros da banca: {}", bancaId);

        // Verificar se banca existe
        if (!bancaRepository.existsById(bancaId)) {
            throw new ResourceNotFoundException("Banca não encontrada");
        }

        return membroBancaRepository.findByBancaIdOrderByOrdemApresentacaoAsc(bancaId).stream()
                .map(membroBancaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca participações de um docente em bancas.
     *
     * @param docenteId ID do docente
     * @param pageable Parâmetros de paginação
     * @return Página de participações
     */
    @Transactional(readOnly = true)
    public Page<MembroBancaResponseDTO> buscarPorDocente(Long docenteId, Pageable pageable) {
        log.debug("Buscando participações do docente: {}", docenteId);

        return membroBancaRepository.findByDocenteId(docenteId, pageable)
                .map(membroBancaMapper::toResponseDTO);
    }

    /**
     * Atribui nota e parecer de um membro.
     *
     * @param membroId ID do membro
     * @param nota Nota atribuída (0.00 a 10.00)
     * @param parecer Parecer textual
     * @return Membro atualizado
     */
    @Transactional
    public MembroBancaResponseDTO atribuirNota(Long membroId, BigDecimal nota, String parecer) {
        log.info("Atribuindo nota {} ao membro: {}", nota, membroId);

        MembroBanca membro = membroBancaRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de banca não encontrado"));

        // Validar se banca está realizada
        Banca banca = membro.getBanca();
        if (!banca.isRealizada()) {
            throw new BusinessException("Notas só podem ser atribuídas após a banca ser realizada");
        }

        // Validar nota
        if (nota == null) {
            throw new BusinessException("Nota é obrigatória");
        }

        if (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(new BigDecimal("10.00")) > 0) {
            throw new BusinessException("Nota deve estar entre 0.00 e 10.00");
        }

        // Validar parecer
        if (parecer == null || parecer.isEmpty()) {
            throw new BusinessException("Parecer é obrigatório");
        }

        membro.setNotaIndividual(nota);
        membro.setParecerIndividual(parecer);

        membro = membroBancaRepository.save(membro);

        log.info("Nota e parecer atribuídos com sucesso ao membro: {}", membroId);
        return membroBancaMapper.toResponseDTO(membro);
    }

    /**
     * Simula upload do arquivo de parecer de um membro.
     *
     * @param membroId ID do membro
     * @param arquivo Arquivo do parecer (PDF)
     * @return Membro atualizado
     */
    @Transactional
    public MembroBancaResponseDTO uploadParecer(Long membroId, MultipartFile arquivo) {
        log.info("Fazendo upload de parecer para membro: {}", membroId);

        MembroBanca membro = membroBancaRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de banca não encontrado"));

        // Validar se banca está realizada
        if (!membro.getBanca().isRealizada()) {
            throw new BusinessException("Pareceres só podem ser enviados após a banca ser realizada");
        }

        // Validar arquivo
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Arquivo não pode ser vazio");
        }

        if (arquivo.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("Arquivo muito grande. Tamanho máximo: 10MB");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new BusinessException("Arquivo deve ser do tipo PDF");
        }

        // Simular salvamento do arquivo (em produção, usar S3/MinIO)
        String nomeArquivo = arquivo.getOriginalFilename();
        String path = "/uploads/pareceres/" + membro.getBanca().getId() + "/" + membroId + "/" + nomeArquivo;

        membro.setArquivoParecer(path);

        membro = membroBancaRepository.save(membro);

        log.info("Parecer enviado com sucesso para membro: {}", membroId);
        return membroBancaMapper.toResponseDTO(membro);
    }

    /**
     * Confirma presença de um membro na banca.
     *
     * @param membroId ID do membro
     * @return Membro atualizado
     */
    @Transactional
    public MembroBancaResponseDTO confirmarPresenca(Long membroId) {
        log.info("Confirmando presença do membro: {}", membroId);

        MembroBanca membro = membroBancaRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de banca não encontrado"));

        // Validar se banca ainda não foi realizada
        if (membro.getBanca().isRealizada()) {
            throw new BusinessException("Não é possível confirmar presença após banca realizada");
        }

        membro.setConfirmado(true);

        membro = membroBancaRepository.save(membro);

        log.info("Presença confirmada para membro: {}", membroId);
        return membroBancaMapper.toResponseDTO(membro);
    }

    /**
     * Registra ausência de um membro com justificativa.
     *
     * @param membroId ID do membro
     * @param justificativa Justificativa da ausência
     * @return Membro atualizado
     */
    @Transactional
    public MembroBancaResponseDTO registrarAusencia(Long membroId, String justificativa) {
        log.info("Registrando ausência do membro: {}", membroId);

        MembroBanca membro = membroBancaRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de banca não encontrado"));

        // Validar se banca foi realizada
        if (!membro.getBanca().isRealizada()) {
            throw new BusinessException("Ausência só pode ser registrada após a banca ser realizada");
        }

        if (justificativa == null || justificativa.isEmpty()) {
            throw new BusinessException("Justificativa é obrigatória");
        }

        membro.setPresente(false);
        membro.setJustificativaAusencia(justificativa);

        membro = membroBancaRepository.save(membro);

        log.info("Ausência registrada para membro: {}", membroId);
        return membroBancaMapper.toResponseDTO(membro);
    }

    /**
     * Registra presença de um membro na banca.
     *
     * @param membroId ID do membro
     * @return Membro atualizado
     */
    @Transactional
    public MembroBancaResponseDTO registrarPresenca(Long membroId) {
        log.info("Registrando presença do membro: {}", membroId);

        MembroBanca membro = membroBancaRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de banca não encontrado"));

        // Validar se banca foi iniciada
        if (!membro.getBanca().isRealizada()) {
            throw new BusinessException("Presença só pode ser registrada após a banca ser iniciada");
        }

        membro.setPresente(true);
        membro.setJustificativaAusencia(null);

        membro = membroBancaRepository.save(membro);

        log.info("Presença registrada para membro: {}", membroId);
        return membroBancaMapper.toResponseDTO(membro);
    }

    /**
     * Busca membros externos de uma banca.
     *
     * @param bancaId ID da banca
     * @return Lista de membros externos
     */
    @Transactional(readOnly = true)
    public List<MembroBancaResponseDTO> buscarMembrosExternos(Long bancaId) {
        log.debug("Buscando membros externos da banca: {}", bancaId);

        return membroBancaRepository.findMembrosExternosPorBanca(bancaId).stream()
                .map(membroBancaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca membros internos de uma banca.
     *
     * @param bancaId ID da banca
     * @return Lista de membros internos
     */
    @Transactional(readOnly = true)
    public List<MembroBancaResponseDTO> buscarMembrosInternos(Long bancaId) {
        log.debug("Buscando membros internos da banca: {}", bancaId);

        return membroBancaRepository.findMembrosInternosPorBanca(bancaId).stream()
                .map(membroBancaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca presidente de uma banca.
     *
     * @param bancaId ID da banca
     * @return Presidente da banca
     */
    @Transactional(readOnly = true)
    public MembroBancaResponseDTO buscarPresidente(Long bancaId) {
        log.debug("Buscando presidente da banca: {}", bancaId);

        MembroBanca presidente = membroBancaRepository.findPresidenteBanca(bancaId)
                .orElseThrow(() -> new ResourceNotFoundException("Presidente não definido para esta banca"));

        return membroBancaMapper.toResponseDTO(presidente);
    }

    /**
     * Busca membros que confirmaram presença.
     *
     * @param bancaId ID da banca
     * @return Lista de membros confirmados
     */
    @Transactional(readOnly = true)
    public List<MembroBancaResponseDTO> buscarMembrosConfirmados(Long bancaId) {
        log.debug("Buscando membros confirmados da banca: {}", bancaId);

        return membroBancaRepository.findMembrosConfirmadosPorBanca(bancaId).stream()
                .map(membroBancaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca membros que não confirmaram presença.
     *
     * @param bancaId ID da banca
     * @return Lista de membros não confirmados
     */
    @Transactional(readOnly = true)
    public List<MembroBancaResponseDTO> buscarMembrosNaoConfirmados(Long bancaId) {
        log.debug("Buscando membros não confirmados da banca: {}", bancaId);

        return membroBancaRepository.findMembrosNaoConfirmadosPorBanca(bancaId).stream()
                .map(membroBancaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca bancas onde o docente foi presidente.
     *
     * @param docenteId ID do docente
     * @return Lista de bancas como presidente
     */
    @Transactional(readOnly = true)
    public List<MembroBancaResponseDTO> buscarBancasComoPresidente(Long docenteId) {
        log.debug("Buscando bancas onde docente {} foi presidente", docenteId);

        return membroBancaRepository.findBancasComoPresidente(docenteId).stream()
                .map(membroBancaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza ordem de apresentação de um membro.
     *
     * @param membroId ID do membro
     * @param ordem Nova ordem de apresentação
     * @return Membro atualizado
     */
    @Transactional
    public MembroBancaResponseDTO atualizarOrdem(Long membroId, Integer ordem) {
        log.info("Atualizando ordem de apresentação do membro {} para: {}", membroId, ordem);

        MembroBanca membro = membroBancaRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de banca não encontrado"));

        if (membro.getBanca().isRealizada()) {
            throw new BusinessException("Ordem não pode ser alterada após banca realizada");
        }

        if (ordem == null || ordem < 1) {
            throw new BusinessException("Ordem deve ser maior que zero");
        }

        membro.setOrdemApresentacao(ordem);

        membro = membroBancaRepository.save(membro);

        log.info("Ordem de apresentação atualizada para membro: {}", membroId);
        return membroBancaMapper.toResponseDTO(membro);
    }

    /**
     * Deleta um membro de banca.
     *
     * @param id ID do membro
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando membro de banca: {}", id);

        MembroBanca membro = membroBancaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de banca não encontrado"));

        if (!membro.podeSerRemovido()) {
            throw new BusinessException("Membro não pode ser removido após banca iniciada");
        }

        membroBancaRepository.delete(membro);

        log.info("Membro de banca deletado com sucesso: {}", id);
    }
}
