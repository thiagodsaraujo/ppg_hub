package br.edu.ppg.hub.core.application.service;

import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoCreateDTO;
import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoResponseDTO;
import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoUpdateDTO;
import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoMapper;
import br.edu.ppg.hub.shared.exception.DuplicateResourceException;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import br.edu.ppg.hub.core.domain.model.Instituicao;
import br.edu.ppg.hub.core.infrastructure.repository.InstituicaoRepository;
import br.edu.ppg.hub.shared.validation.CNPJValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio de Instituição.
 *
 * Contém toda a lógica de negócio, validações e orquestração
 * entre repository e controller.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InstituicaoService {

    private final InstituicaoRepository repository;
    private final InstituicaoMapper mapper;

    /**
     * Cria uma nova instituição
     */
    @Transactional
    public InstituicaoResponseDTO create(InstituicaoCreateDTO dto) {
        log.info("Criando instituição com código: {}", dto.getCodigo());

        // Valida se código já existe
        if (repository.existsByCodigo(dto.getCodigo())) {
            throw new DuplicateResourceException("Instituição", "codigo", dto.getCodigo());
        }

        // Valida se CNPJ já existe (se fornecido)
        if (dto.getCnpj() != null && !dto.getCnpj().isEmpty()) {
            if (repository.existsByCnpj(dto.getCnpj())) {
                throw new DuplicateResourceException("Instituição", "CNPJ", dto.getCnpj());
            }
            // Formata CNPJ
            dto.setCnpj(CNPJValidator.formatarCNPJ(dto.getCnpj()));
        }

        // Converte DTO para entidade
        Instituicao entity = mapper.toEntity(dto);

        // Salva no banco
        Instituicao saved = repository.save(entity);

        log.info("Instituição criada com sucesso. ID: {}", saved.getId());

        return mapper.toResponseDTO(saved);
    }

    /**
     * Busca instituição por ID
     */
    public InstituicaoResponseDTO findById(Long id) {
        log.debug("Buscando instituição por ID: {}", id);

        Instituicao entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", "id", id));

        return mapper.toResponseDTO(entity);
    }

    /**
     * Busca instituição por código
     */
    public InstituicaoResponseDTO findByCodigo(String codigo) {
        log.debug("Buscando instituição por código: {}", codigo);

        Instituicao entity = repository.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", "codigo", codigo));

        return mapper.toResponseDTO(entity);
    }

    /**
     * Busca instituição por CNPJ
     */
    public InstituicaoResponseDTO findByCnpj(String cnpj) {
        log.debug("Buscando instituição por CNPJ: {}", cnpj);

        Instituicao entity = repository.findByCnpj(cnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", "CNPJ", cnpj));

        return mapper.toResponseDTO(entity);
    }

    /**
     * Lista todas as instituições com paginação
     */
    public Page<InstituicaoResponseDTO> findAll(Pageable pageable) {
        log.debug("Listando todas as instituições. Página: {}", pageable.getPageNumber());

        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    /**
     * Lista apenas instituições ativas com paginação
     */
    public Page<InstituicaoResponseDTO> findAllAtivas(Pageable pageable) {
        log.debug("Listando instituições ativas. Página: {}", pageable.getPageNumber());

        return repository.findByAtivoTrue(pageable)
                .map(mapper::toResponseDTO);
    }

    /**
     * Busca instituições por termo livre
     */
    public Page<InstituicaoResponseDTO> search(String termo, boolean apenasAtivas, Pageable pageable) {
        log.debug("Buscando instituições com termo: {} (apenas ativas: {})", termo, apenasAtivas);

        Page<Instituicao> results;

        if (apenasAtivas) {
            results = repository.searchAtivasByTermo(termo, pageable);
        } else {
            results = repository.searchByTermo(termo, pageable);
        }

        return results.map(mapper::toResponseDTO);
    }

    /**
     * Lista instituições por tipo
     */
    public List<InstituicaoResponseDTO> findByTipo(String tipo) {
        log.debug("Listando instituições por tipo: {}", tipo);

        return repository.findByTipoAndAtivoTrue(tipo)
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza uma instituição existente
     */
    @Transactional
    public InstituicaoResponseDTO update(Long id, InstituicaoUpdateDTO dto) {
        log.info("Atualizando instituição ID: {}", id);

        // Busca instituição existente
        Instituicao entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", "id", id));

        // Valida código se foi alterado
        if (dto.getCodigo() != null && !dto.getCodigo().equalsIgnoreCase(entity.getCodigo())) {
            if (repository.existsByCodigoAndIdNot(dto.getCodigo(), id)) {
                throw new DuplicateResourceException("Instituição", "codigo", dto.getCodigo());
            }
        }

        // Valida CNPJ se foi alterado
        if (dto.getCnpj() != null && !dto.getCnpj().equals(entity.getCnpj())) {
            if (repository.existsByCnpjAndIdNot(dto.getCnpj(), id)) {
                throw new DuplicateResourceException("Instituição", "CNPJ", dto.getCnpj());
            }
            // Formata CNPJ
            dto.setCnpj(CNPJValidator.formatarCNPJ(dto.getCnpj()));
        }

        // Atualiza campos
        mapper.updateEntityFromDTO(dto, entity);

        // Salva alterações
        Instituicao updated = repository.save(entity);

        log.info("Instituição atualizada com sucesso. ID: {}", id);

        return mapper.toResponseDTO(updated);
    }

    /**
     * Remove uma instituição
     */
    @Transactional
    public void delete(Long id) {
        log.info("Removendo instituição ID: {}", id);

        // Verifica se existe
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Instituição", "id", id);
        }

        repository.deleteById(id);

        log.info("Instituição removida com sucesso. ID: {}", id);
    }

    /**
     * Desativa uma instituição (soft delete)
     */
    @Transactional
    public InstituicaoResponseDTO deactivate(Long id) {
        log.info("Desativando instituição ID: {}", id);

        Instituicao entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", "id", id));

        entity.setAtivo(false);

        Instituicao updated = repository.save(entity);

        log.info("Instituição desativada com sucesso. ID: {}", id);

        return mapper.toResponseDTO(updated);
    }

    /**
     * Reativa uma instituição
     */
    @Transactional
    public InstituicaoResponseDTO activate(Long id) {
        log.info("Reativando instituição ID: {}", id);

        Instituicao entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", "id", id));

        entity.setAtivo(true);

        Instituicao updated = repository.save(entity);

        log.info("Instituição reativada com sucesso. ID: {}", id);

        return mapper.toResponseDTO(updated);
    }

    /**
     * Conta total de instituições
     */
    public long countTotal() {
        return repository.count();
    }

    /**
     * Conta instituições ativas
     */
    public long countAtivas() {
        return repository.countByAtivoTrue();
    }

    /**
     * Retorna estatísticas de instituições por tipo
     */
    public Map<String, Long> getEstatisticasPorTipo() {
        log.debug("Obtendo estatísticas por tipo");

        List<Object[]> results = repository.getEstatisticasPorTipo();

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }
}
