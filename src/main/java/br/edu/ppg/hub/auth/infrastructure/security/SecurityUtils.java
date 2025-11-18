package br.edu.ppg.hub.auth.infrastructure.security;

import br.edu.ppg.hub.auth.domain.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utilitários para trabalhar com o contexto de segurança.
 *
 * Facilita o acesso ao usuário autenticado e verificação de permissões.
 */
@Slf4j
@Component
public class SecurityUtils {

    /**
     * Obtém o usuário atualmente autenticado.
     *
     * @return Optional com o usuário autenticado
     */
    public static Optional<Usuario> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Usuario) {
            return Optional.of((Usuario) principal);
        }

        return Optional.empty();
    }

    /**
     * Obtém o ID do usuário atualmente autenticado.
     *
     * @return Optional com o ID do usuário
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(Usuario::getId);
    }

    /**
     * Obtém o email do usuário atualmente autenticado.
     *
     * @return Optional com o email do usuário
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(Usuario::getEmail);
    }

    /**
     * Obtém o nome do usuário atualmente autenticado.
     *
     * @return Optional com o nome do usuário
     */
    public static Optional<String> getCurrentUserNome() {
        return getCurrentUser().map(Usuario::getNomeExibicao);
    }

    /**
     * Verifica se o usuário atual tem uma role específica.
     *
     * @param role Nome da role (com ou sem prefixo ROLE_)
     * @return true se o usuário tem a role
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(roleToCheck));
    }

    /**
     * Verifica se o usuário atual tem qualquer uma das roles especificadas.
     *
     * @param roles Nomes das roles
     * @return true se o usuário tem pelo menos uma das roles
     */
    public static boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se o usuário atual tem todas as roles especificadas.
     *
     * @param roles Nomes das roles
     * @return true se o usuário tem todas as roles
     */
    public static boolean hasAllRoles(String... roles) {
        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se há um usuário autenticado.
     *
     * @return true se há autenticação
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Verifica se o usuário autenticado é o proprietário do recurso.
     *
     * @param usuarioId ID do usuário proprietário
     * @return true se o usuário autenticado é o proprietário
     */
    public static boolean isOwner(Long usuarioId) {
        return getCurrentUserId()
                .map(currentId -> currentId.equals(usuarioId))
                .orElse(false);
    }

    /**
     * Verifica se o usuário autenticado é o proprietário OU tem role administrativa.
     *
     * @param usuarioId ID do usuário proprietário
     * @return true se é proprietário ou admin
     */
    public static boolean isOwnerOrAdmin(Long usuarioId) {
        return isOwner(usuarioId) || hasAnyRole("ADMIN", "SUPERADMIN");
    }
}
