package br.edu.ppg.hub.auth.application.service;

import br.edu.ppg.hub.auth.application.dto.auth.*;
import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioMapper;
import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioResponseDTO;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.auth.infrastructure.repository.UsuarioRepository;
import br.edu.ppg.hub.auth.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Serviço de autenticação e autorização.
 *
 * Responsável por:
 * - Login
 * - Registro de novos usuários
 * - Refresh de tokens
 * - Recuperação de senha
 * - Verificação de email
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

    private static final int MAX_TENTATIVAS_LOGIN = 5;
    private static final int BLOQUEIO_MINUTOS = 30;
    private static final int RESET_TOKEN_EXPIRACAO_MINUTOS = 60;

    /**
     * Realiza login de um usuário.
     *
     * @param dto Dados de login
     * @return Resposta com tokens JWT e dados do usuário
     */
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        log.info("Tentativa de login: {}", dto.getEmail());

        try {
            // Buscar usuário
            Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Email ou senha inválidos"));

            // Verificar se conta está bloqueada
            if (usuario.getContaBloqueada() != null && usuario.getContaBloqueada()) {
                if (usuario.getBloqueadaAte() != null && usuario.getBloqueadaAte().isAfter(LocalDateTime.now())) {
                    log.warn("Tentativa de login em conta bloqueada: {}", dto.getEmail());
                    throw new BadCredentialsException("Conta bloqueada. Tente novamente mais tarde.");
                } else {
                    // Bloqueio expirou, desbloquear
                    usuario.resetarTentativasLogin();
                }
            }

            // Verificar se usuário está ativo
            if (!usuario.getAtivo()) {
                log.warn("Tentativa de login em conta inativa: {}", dto.getEmail());
                throw new BadCredentialsException("Conta inativa");
            }

            // Autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            // Login bem-sucedido - resetar tentativas
            usuario.resetarTentativasLogin();
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);

            // Gerar tokens
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            log.info("Login bem-sucedido: {}", dto.getEmail());

            // Montar resposta
            UsuarioResponseDTO usuarioResponse = usuarioMapper.toResponseDTO(usuario);

            return LoginResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                    .usuario(usuarioResponse)
                    .build();

        } catch (AuthenticationException e) {
            // Login falhou - incrementar tentativas
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(usuario -> {
                usuario.incrementarTentativasLogin();

                if (usuario.getTentativasLogin() >= MAX_TENTATIVAS_LOGIN) {
                    usuario.bloquearConta(BLOQUEIO_MINUTOS);
                    log.warn("Conta bloqueada por excesso de tentativas: {}", dto.getEmail());
                }

                usuarioRepository.save(usuario);
            });

            log.warn("Falha no login: {}", dto.getEmail());
            throw new BadCredentialsException("Email ou senha inválidos");
        }
    }

    /**
     * Registra um novo usuário.
     *
     * @param dto Dados de registro
     * @return Resposta com tokens JWT e dados do usuário
     */
    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO dto) {
        log.info("Registrando novo usuário: {}", dto.getEmail());

        // Validar se email já existe
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Validar se CPF já existe (se informado)
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && usuarioRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        // Validar se CPF ou Passaporte foi informado
        if ((dto.getCpf() == null || dto.getCpf().isBlank()) &&
                (dto.getPassaporte() == null || dto.getPassaporte().isBlank())) {
            throw new IllegalArgumentException("CPF ou Passaporte é obrigatório");
        }

        // Criar usuário
        Usuario usuario = Usuario.builder()
                .nomeCompleto(dto.getNomeCompleto())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .cpf(dto.getCpf())
                .telefone(dto.getTelefone())
                .passaporte(dto.getPassaporte())
                .emailVerificado(false)
                .ativo(true)
                .tentativasLogin(0)
                .contaBloqueada(false)
                .nacionalidade("Brasileira")
                .configuracoes("{}")
                .preferencias("{}")
                .build();

        // Salvar
        Usuario saved = usuarioRepository.save(usuario);

        log.info("Usuário registrado com sucesso: ID={}, Email={}", saved.getId(), saved.getEmail());

        // TODO: Enviar email de verificação

        // Gerar tokens
        String accessToken = jwtTokenProvider.generateAccessToken(saved);
        String refreshToken = jwtTokenProvider.generateRefreshToken(saved);

        // Montar resposta
        UsuarioResponseDTO usuarioResponse = usuarioMapper.toResponseDTO(saved);

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .usuario(usuarioResponse)
                .build();
    }

    /**
     * Renova o access token usando o refresh token.
     *
     * @param dto Dados de refresh
     * @return Resposta com novo access token
     */
    @Transactional
    public LoginResponseDTO refreshToken(TokenRefreshDTO dto) {
        log.info("Renovando token");

        // Validar refresh token
        if (!jwtTokenProvider.validateToken(dto.getRefreshToken())) {
            throw new BadCredentialsException("Refresh token inválido ou expirado");
        }

        // Extrair username do token
        String username = jwtTokenProvider.getUsernameFromToken(dto.getRefreshToken());

        // Buscar usuário
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        // Verificar se está ativo
        if (!usuario.getAtivo()) {
            throw new BadCredentialsException("Conta inativa");
        }

        // Gerar novo access token
        String newAccessToken = jwtTokenProvider.generateAccessToken(usuario);

        // Montar resposta
        UsuarioResponseDTO usuarioResponse = usuarioMapper.toResponseDTO(usuario);

        return LoginResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(dto.getRefreshToken()) // Mantém o mesmo refresh token
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .usuario(usuarioResponse)
                .build();
    }

    /**
     * Inicia o processo de recuperação de senha.
     *
     * @param dto Dados de recuperação
     */
    @Transactional
    public void forgotPassword(ForgotPasswordDTO dto) {
        log.info("Recuperação de senha solicitada: {}", dto.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email não encontrado"));

        // Gerar token de reset
        String resetToken = UUID.randomUUID().toString();
        usuario.setResetToken(resetToken, RESET_TOKEN_EXPIRACAO_MINUTOS);

        usuarioRepository.save(usuario);

        // TODO: Enviar email com link de reset
        log.info("Token de reset gerado para: {}", dto.getEmail());
        log.debug("Reset token: {}", resetToken); // Apenas para desenvolvimento
    }

    /**
     * Reseta a senha usando o token de reset.
     *
     * @param dto Dados de reset
     */
    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        log.info("Resetando senha com token");

        Usuario usuario = usuarioRepository.findByResetToken(dto.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        // Verificar se token é válido
        if (!usuario.isResetTokenValido()) {
            throw new IllegalArgumentException("Token expirado ou inválido");
        }

        // Atualizar senha
        usuario.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        usuario.limparResetToken();
        usuario.resetarTentativasLogin(); // Desbloquear conta se estava bloqueada

        usuarioRepository.save(usuario);

        log.info("Senha resetada com sucesso para usuário: {}", usuario.getEmail());
    }

    /**
     * Altera a senha de um usuário autenticado.
     *
     * @param dto Dados de alteração
     * @param email Email do usuário autenticado
     */
    @Transactional
    public void changePassword(ChangePasswordDTO dto, String email) {
        log.info("Alterando senha: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Verificar senha atual
        if (!passwordEncoder.matches(dto.getOldPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        // Atualizar senha
        usuario.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        usuarioRepository.save(usuario);

        log.info("Senha alterada com sucesso: {}", email);
    }

    /**
     * Verifica o email de um usuário usando token.
     *
     * @param token Token de verificação
     */
    @Transactional
    public void verifyEmail(String token) {
        log.info("Verificando email com token");

        // TODO: Implementar lógica de verificação de email com token
        // Por enquanto, apenas um placeholder

        throw new UnsupportedOperationException("Verificação de email ainda não implementada");
    }

    /**
     * Realiza logout (invalida token).
     *
     * @param token Token a invalidar
     */
    @Transactional
    public void logout(String token) {
        log.info("Realizando logout");

        // TODO: Implementar blacklist de tokens
        // Por enquanto, apenas log

        log.info("Logout realizado");
    }
}
