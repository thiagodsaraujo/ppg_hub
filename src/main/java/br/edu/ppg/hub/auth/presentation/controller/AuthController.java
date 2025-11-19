package br.edu.ppg.hub.auth.presentation.controller;

import br.edu.ppg.hub.auth.application.dto.auth.*;
import br.edu.ppg.hub.auth.application.service.AuthService;
import br.edu.ppg.hub.auth.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para autenticação e autorização.
 *
 * Endpoints:
 * - POST /api/v1/auth/login - Login
 * - POST /api/v1/auth/register - Registro
 * - POST /api/v1/auth/refresh - Renovar token
 * - POST /api/v1/auth/logout - Logout
 * - POST /api/v1/auth/forgot-password - Recuperar senha
 * - POST /api/v1/auth/reset-password - Resetar senha
 * - POST /api/v1/auth/change-password - Alterar senha
 * - GET /api/v1/auth/verify-email - Verificar email
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação e autorização")
public class AuthController {

    private final AuthService authService;

    /**
     * Realiza login de um usuário.
     *
     * @param dto Dados de login
     * @return Resposta com tokens JWT e dados do usuário
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica um usuário e retorna tokens JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("POST /api/v1/auth/login - Email: {}", dto.getEmail());
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra um novo usuário.
     *
     * @param dto Dados de registro
     * @return Resposta com tokens JWT e dados do usuário
     */
    @PostMapping("/register")
    @Operation(summary = "Registro", description = "Registra um novo usuário no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        log.info("POST /api/v1/auth/register - Email: {}", dto.getEmail());
        LoginResponseDTO response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Renova o access token usando o refresh token.
     *
     * @param dto Dados de refresh
     * @return Resposta com novo access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar Token", description = "Renova o access token usando o refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
    })
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody TokenRefreshDTO dto) {
        log.info("POST /api/v1/auth/refresh");
        LoginResponseDTO response = authService.refreshToken(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Realiza logout (invalida token).
     *
     * @param authorization Header Authorization com o token
     * @return Mensagem de sucesso
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalida o token atual do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorization) {
        log.info("POST /api/v1/auth/logout");

        String token = authorization.replace("Bearer ", "");
        authService.logout(token);

        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    /**
     * Inicia o processo de recuperação de senha.
     *
     * @param dto Dados de recuperação
     * @return Mensagem de sucesso
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Esqueci Minha Senha", description = "Envia email com link para reset de senha")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email enviado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Email não encontrado")
    })
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        log.info("POST /api/v1/auth/forgot-password - Email: {}", dto.getEmail());
        authService.forgotPassword(dto);
        return ResponseEntity.ok("Email de recuperação enviado com sucesso");
    }

    /**
     * Reseta a senha usando o token de reset.
     *
     * @param dto Dados de reset
     * @return Mensagem de sucesso
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Resetar Senha", description = "Reseta a senha usando o token recebido por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha resetada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    })
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        log.info("POST /api/v1/auth/reset-password");
        authService.resetPassword(dto);
        return ResponseEntity.ok("Senha resetada com sucesso");
    }

    /**
     * Altera a senha de um usuário autenticado.
     *
     * @param dto Dados de alteração
     * @return Mensagem de sucesso
     */
    @PostMapping("/change-password")
    @Operation(summary = "Alterar Senha", description = "Altera a senha do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        log.info("POST /api/v1/auth/change-password");

        String email = SecurityUtils.getCurrentUserEmail()
                .orElseThrow(() -> new IllegalStateException("Usuário não autenticado"));

        authService.changePassword(dto, email);
        return ResponseEntity.ok("Senha alterada com sucesso");
    }

    /**
     * Verifica o email de um usuário usando token.
     *
     * @param token Token de verificação
     * @return Mensagem de sucesso
     */
    @GetMapping("/verify-email")
    @Operation(summary = "Verificar Email", description = "Verifica o email usando o token recebido por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verificado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    })
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        log.info("GET /api/v1/auth/verify-email");
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verificado com sucesso");
    }
}
