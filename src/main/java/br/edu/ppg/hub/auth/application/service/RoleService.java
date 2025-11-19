package br.edu.ppg.hub.auth.application.service;

import br.edu.ppg.hub.auth.domain.model.Role;
import br.edu.ppg.hub.auth.infrastructure.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço de negócio para Role.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Busca todas as roles.
     *
     * @return Lista de todas as roles
     */
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        log.debug("Buscando todas as roles");
        return roleRepository.findAll();
    }

    /**
     * Busca todas as roles ativas.
     *
     * @return Lista de roles ativas
     */
    @Transactional(readOnly = true)
    public List<Role> findAllAtivas() {
        log.debug("Buscando todas as roles ativas");
        return roleRepository.findAllAtivas();
    }

    /**
     * Busca uma role pelo ID.
     *
     * @param id ID da role
     * @return Role encontrada
     * @throws IllegalArgumentException se não encontrada
     */
    @Transactional(readOnly = true)
    public Role findById(Long id) {
        log.debug("Buscando role por ID: {}", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role não encontrada com ID: " + id));
    }

    /**
     * Busca uma role pelo nome.
     *
     * @param nome Nome da role
     * @return Role encontrada
     * @throws IllegalArgumentException se não encontrada
     */
    @Transactional(readOnly = true)
    public Role findByNome(String nome) {
        log.debug("Buscando role por nome: {}", nome);
        return roleRepository.findByNome(nome)
                .orElseThrow(() -> new IllegalArgumentException("Role não encontrada com nome: " + nome));
    }

    /**
     * Busca roles administrativas (nível >= 3).
     *
     * @return Lista de roles administrativas
     */
    @Transactional(readOnly = true)
    public List<Role> findRolesAdministrativas() {
        log.debug("Buscando roles administrativas");
        return roleRepository.findRolesAdministrativas();
    }

    /**
     * Busca roles com nível de acesso maior ou igual ao especificado.
     *
     * @param nivelMinimo Nível mínimo
     * @return Lista de roles
     */
    @Transactional(readOnly = true)
    public List<Role> findByNivelMinimo(Integer nivelMinimo) {
        log.debug("Buscando roles com nível >= {}", nivelMinimo);
        return roleRepository.findByNivelAcessoGreaterThanEqual(nivelMinimo);
    }

    /**
     * Verifica se uma role existe pelo nome.
     *
     * @param nome Nome da role
     * @return true se existe
     */
    @Transactional(readOnly = true)
    public boolean existsByNome(String nome) {
        return roleRepository.existsByNome(nome);
    }

    /**
     * Cria uma nova role.
     *
     * @param role Role a criar
     * @return Role criada
     */
    @Transactional
    public Role create(Role role) {
        log.info("Criando nova role: {}", role.getNome());

        if (roleRepository.existsByNome(role.getNome())) {
            throw new IllegalArgumentException("Já existe uma role com o nome: " + role.getNome());
        }

        return roleRepository.save(role);
    }

    /**
     * Atualiza uma role existente.
     *
     * @param id ID da role
     * @param roleAtualizada Dados atualizados
     * @return Role atualizada
     */
    @Transactional
    public Role update(Long id, Role roleAtualizada) {
        log.info("Atualizando role ID: {}", id);

        Role role = findById(id);

        // Atualizar campos permitidos
        role.setDescricao(roleAtualizada.getDescricao());
        role.setNivelAcesso(roleAtualizada.getNivelAcesso());
        role.setPermissoes(roleAtualizada.getPermissoes());
        role.setAtivo(roleAtualizada.getAtivo());

        return roleRepository.save(role);
    }

    /**
     * Ativa uma role.
     *
     * @param id ID da role
     * @return Role ativada
     */
    @Transactional
    public Role activate(Long id) {
        log.info("Ativando role ID: {}", id);
        Role role = findById(id);
        role.setAtivo(true);
        return roleRepository.save(role);
    }

    /**
     * Desativa uma role.
     *
     * @param id ID da role
     * @return Role desativada
     */
    @Transactional
    public Role deactivate(Long id) {
        log.info("Desativando role ID: {}", id);
        Role role = findById(id);
        role.setAtivo(false);
        return roleRepository.save(role);
    }

    /**
     * Deleta uma role.
     *
     * @param id ID da role
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando role ID: {}", id);
        Role role = findById(id);
        roleRepository.delete(role);
    }
}
