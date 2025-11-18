package br.edu.ppg.hub.auth.infrastructure.security;

import br.edu.ppg.hub.shared.config.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticação JWT.
 *
 * Intercepta todas as requisições HTTP e:
 * 1. Extrai o token JWT do header Authorization
 * 2. Valida o token
 * 3. Carrega os detalhes do usuário
 * 4. Configura a autenticação no SecurityContext
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Extrair token do header
            String token = extractTokenFromRequest(request);

            // 2. Validar token e autenticar usuário
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 3. Extrair username do token
                String username = jwtTokenProvider.getUsernameFromToken(token);

                // 4. Verificar se já não está autenticado
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 5. Carregar detalhes do usuário
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 6. Validar token com os detalhes do usuário
                    if (jwtTokenProvider.validateToken(token, userDetails)) {
                        // 7. Criar objeto de autenticação
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        // 8. Adicionar detalhes da requisição
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 9. Configurar autenticação no SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("Usuário '{}' autenticado com sucesso via JWT", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Não foi possível configurar autenticação do usuário: {}", e.getMessage());
        }

        // Continuar cadeia de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token JWT do header Authorization.
     *
     * Formato esperado: "Authorization: Bearer <token>"
     *
     * @param request Requisição HTTP
     * @return Token JWT ou null se não encontrado
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());

        if (bearerToken != null && bearerToken.startsWith(jwtConfig.getPrefix())) {
            return bearerToken.substring(jwtConfig.getPrefix().length());
        }

        return null;
    }
}
