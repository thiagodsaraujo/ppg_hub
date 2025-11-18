package br.edu.ppg.hub.auth.presentation.controller;

import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioCreateDTO;
import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioResponseDTO;
import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioUpdateDTO;
import br.edu.ppg.hub.auth.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gerenciamento de usuários.
 *
 * Endpoints:
 * - GET /api/v1/usuarios - Listar usuários
 * - GET /api/v1/usuarios/{id} - Buscar por ID
 * - GET /api/v1/usuarios/uuid/{uuid} - Buscar por UUID
 * - GET /api/v1/usuarios/email/{email} - Buscar por email
 * - POST /api/v1/usuarios - Criar usuário
 * - PUT /api/v1/usuarios/{id} - Atualizar usuário
 * - DELETE /api/v1/usuarios/{id} - Deletar usuário
 * - PATCH /api/v1/usuarios/{id}/activate - Ativar usuário
 * - PATCH /api/v1/usuarios/{id}/deactivate - Desativar usuário
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Lista todos os usuários com paginação.
     *
     * @param pageable Configuração de paginação
     * @return Page de usuários
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Listar Usuários", description = "Lista todos os usuários com paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<Page<UsuarioResponseDTO>> findAll(
            @PageableDefault(size = 20, sort = "nomeCompleto") Pageable pageable
    ) {
        log.info("GET /api/v1/usuarios - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UsuarioResponseDTO> usuarios = usuarioService.findAll(pageable);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Lista todos os usuários ativos.
     *
     * @return Lista de usuários ativos
     */
    @GetMapping("/ativos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Listar Usuários Ativos", description = "Lista todos os usuários ativos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários ativos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<UsuarioResponseDTO>> findAllAtivos() {
        log.info("GET /api/v1/usuarios/ativos");
        List<UsuarioResponseDTO> usuarios = usuarioService.findAllAtivos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca um usuário por ID.
     *
     * @param id ID do usuário
     * @return Usuário encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA') or @securityUtils.isOwner(#id)")
    @Operation(summary = "Buscar Usuário por ID", description = "Busca um usuário pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        log.info("GET /api/v1/usuarios/{}", id);
        UsuarioResponseDTO usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Busca um usuário por UUID.
     *
     * @param uuid UUID do usuário
     * @return Usuário encontrado
     */
    @GetMapping("/uuid/{uuid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Buscar Usuário por UUID", description = "Busca um usuário pelo UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<UsuarioResponseDTO> findByUuid(@PathVariable UUID uuid) {
        log.info("GET /api/v1/usuarios/uuid/{}", uuid);
        UsuarioResponseDTO usuario = usuarioService.findByUuid(uuid);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Busca um usuário por email.
     *
     * @param email Email do usuário
     * @return Usuário encontrado
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Buscar Usuário por Email", description = "Busca um usuário pelo email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<UsuarioResponseDTO> findByEmail(@PathVariable String email) {
        log.info("GET /api/v1/usuarios/email/{}", email);
        UsuarioResponseDTO usuario = usuarioService.findByEmail(email);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Busca usuários por nome.
     *
     * @param nome Nome a buscar
     * @return Lista de usuários encontrados
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Buscar Usuários por Nome", description = "Busca usuários pelo nome (busca parcial)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrados"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<UsuarioResponseDTO>> findByNome(@RequestParam String nome) {
        log.info("GET /api/v1/usuarios/search?nome={}", nome);
        List<UsuarioResponseDTO> usuarios = usuarioService.findByNome(nome);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Cria um novo usuário.
     *
     * @param dto Dados do usuário
     * @return Usuário criado
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Criar Usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioCreateDTO dto) {
        log.info("POST /api/v1/usuarios - Email: {}", dto.getEmail());
        UsuarioResponseDTO usuario = usuarioService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    /**
     * Atualiza um usuário existente.
     *
     * @param id ID do usuário
     * @param dto Dados atualizados
     * @return Usuário atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR') or @securityUtils.isOwner(#id)")
    @Operation(summary = "Atualizar Usuário", description = "Atualiza os dados de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<UsuarioResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO dto
    ) {
        log.info("PUT /api/v1/usuarios/{}", id);
        UsuarioResponseDTO usuario = usuarioService.update(id, dto);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Ativa um usuário.
     *
     * @param id ID do usuário
     * @return Usuário ativado
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Ativar Usuário", description = "Ativa um usuário inativo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<UsuarioResponseDTO> activate(@PathVariable Long id) {
        log.info("PATCH /api/v1/usuarios/{}/activate", id);
        UsuarioResponseDTO usuario = usuarioService.activate(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Desativa um usuário.
     *
     * @param id ID do usuário
     * @return Usuário desativado
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Desativar Usuário", description = "Desativa um usuário ativo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<UsuarioResponseDTO> deactivate(@PathVariable Long id) {
        log.info("PATCH /api/v1/usuarios/{}/deactivate", id);
        UsuarioResponseDTO usuario = usuarioService.deactivate(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Deleta um usuário.
     *
     * @param id ID do usuário
     * @return Mensagem de sucesso
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar Usuário", description = "Remove um usuário do sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/usuarios/{}", id);
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna estatísticas de usuários.
     *
     * @return Estatísticas
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Estatísticas de Usuários", description = "Retorna estatísticas sobre usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estatísticas"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<?> getStats() {
        log.info("GET /api/v1/usuarios/stats");

        Long totalAtivos = usuarioService.countAtivos();
        Long totalEmailVerificado = usuarioService.countEmailVerificado();

        return ResponseEntity.ok(new Object() {
            public final Long ativos = totalAtivos;
            public final Long emailVerificado = totalEmailVerificado;
        });
    }
}
