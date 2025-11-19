package br.edu.ppg.hub.auth.application.service;

import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioCreateDTO;
import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioMapper;
import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioResponseDTO;
import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioUpdateDTO;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.auth.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço de negócio para Usuario.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cria um novo usuário.
     *
     * @param dto DTO de criação
     * @return DTO de resposta com o usuário criado
     */
    @Transactional
    public UsuarioResponseDTO create(UsuarioCreateDTO dto) {
        log.info("Criando novo usuário: {}", dto.getEmail());

        // Validar se email já existe
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + dto.getEmail());
        }

        // Validar se CPF já existe (se informado)
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && usuarioRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado: " + dto.getCpf());
        }

        // Converter DTO para entidade
        Usuario usuario = usuarioMapper.toEntity(dto);

        // Encriptar senha
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        // Salvar
        Usuario saved = usuarioRepository.save(usuario);

        log.info("Usuário criado com sucesso: ID={}, Email={}", saved.getId(), saved.getEmail());

        return usuarioMapper.toResponseDTO(saved);
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário
     * @return DTO de resposta
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Long id) {
        log.debug("Buscando usuário por ID: {}", id);
        Usuario usuario = findUsuarioById(id);
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Busca um usuário pelo UUID.
     *
     * @param uuid UUID do usuário
     * @return DTO de resposta
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO findByUuid(UUID uuid) {
        log.debug("Buscando usuário por UUID: {}", uuid);
        Usuario usuario = usuarioRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com UUID: " + uuid));
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário
     * @return DTO de resposta
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO findByEmail(String email) {
        log.debug("Buscando usuário por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com email: " + email));
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Busca todos os usuários ativos.
     *
     * @return Lista de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAllAtivos() {
        log.debug("Buscando todos os usuários ativos");
        return usuarioRepository.findAllAtivos().stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca todos os usuários com paginação.
     *
     * @param pageable Configuração de paginação
     * @return Page de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> findAll(Pageable pageable) {
        log.debug("Buscando usuários com paginação: {}", pageable);
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toResponseDTO);
    }

    /**
     * Busca usuários pelo nome (busca parcial).
     *
     * @param nome Nome a buscar
     * @return Lista de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findByNome(String nome) {
        log.debug("Buscando usuários por nome: {}", nome);
        return usuarioRepository.findByNomeContaining(nome).stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza um usuário existente.
     *
     * @param id ID do usuário
     * @param dto DTO de atualização
     * @return DTO de resposta com o usuário atualizado
     */
    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioUpdateDTO dto) {
        log.info("Atualizando usuário ID: {}", id);

        Usuario usuario = findUsuarioById(id);

        // Validar se email já existe (se foi alterado)
        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email já cadastrado: " + dto.getEmail());
            }
        }

        // Atualizar campos
        usuarioMapper.updateEntityFromDTO(usuario, dto);

        // Salvar
        Usuario updated = usuarioRepository.save(usuario);

        log.info("Usuário atualizado com sucesso: ID={}", id);

        return usuarioMapper.toResponseDTO(updated);
    }

    /**
     * Ativa um usuário.
     *
     * @param id ID do usuário
     * @return DTO de resposta
     */
    @Transactional
    public UsuarioResponseDTO activate(Long id) {
        log.info("Ativando usuário ID: {}", id);
        Usuario usuario = findUsuarioById(id);
        usuario.setAtivo(true);
        Usuario saved = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(saved);
    }

    /**
     * Desativa um usuário.
     *
     * @param id ID do usuário
     * @return DTO de resposta
     */
    @Transactional
    public UsuarioResponseDTO deactivate(Long id) {
        log.info("Desativando usuário ID: {}", id);
        Usuario usuario = findUsuarioById(id);
        usuario.setAtivo(false);
        Usuario saved = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(saved);
    }

    /**
     * Altera a senha de um usuário.
     *
     * @param id ID do usuário
     * @param oldPassword Senha atual
     * @param newPassword Nova senha
     */
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        log.info("Alterando senha do usuário ID: {}", id);

        Usuario usuario = findUsuarioById(id);

        // Verificar senha atual
        if (!passwordEncoder.matches(oldPassword, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        // Atualizar senha
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        log.info("Senha alterada com sucesso para usuário ID: {}", id);
    }

    /**
     * Verifica o email de um usuário.
     *
     * @param email Email do usuário
     */
    @Transactional
    public void verifyEmail(String email) {
        log.info("Verificando email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com email: " + email));

        usuario.setEmailVerificado(true);
        usuario.setEmailVerificadoEm(LocalDateTime.now());
        usuarioRepository.save(usuario);

        log.info("Email verificado com sucesso: {}", email);
    }

    /**
     * Deleta um usuário.
     *
     * @param id ID do usuário
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando usuário ID: {}", id);
        Usuario usuario = findUsuarioById(id);
        usuarioRepository.delete(usuario);
        log.info("Usuário deletado com sucesso: ID={}", id);
    }

    /**
     * Método auxiliar para buscar usuário por ID.
     *
     * @param id ID do usuário
     * @return Usuário encontrado
     */
    private Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
    }

    /**
     * Conta usuários ativos.
     *
     * @return Quantidade de usuários ativos
     */
    @Transactional(readOnly = true)
    public Long countAtivos() {
        return usuarioRepository.countAtivos();
    }

    /**
     * Conta usuários com email verificado.
     *
     * @return Quantidade de usuários com email verificado
     */
    @Transactional(readOnly = true)
    public Long countEmailVerificado() {
        return usuarioRepository.countEmailVerificado();
    }
}
