package com.ppghub.presentation.controller;

import com.ppghub.application.dto.request.BancaCreateRequest;
import com.ppghub.application.dto.request.BancaUpdateRequest;
import com.ppghub.application.dto.response.BancaResponse;
import com.ppghub.application.dto.response.PagedResponse;
import com.ppghub.domain.service.BancaService;
import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller REST para gerenciamento de Bancas de Defesa.
 */
@RestController
@RequestMapping("/v1/bancas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bancas", description = "Endpoints para gerenciamento de bancas de defesa e qualificação")
public class BancaController {

    private final BancaService bancaService;

    @GetMapping
    @Operation(summary = "Listar todas as bancas", description = "Retorna lista de todas as bancas")
    public ResponseEntity<List<BancaResponse>> listarTodas() {
        log.info("Listando todas as bancas");
        return ResponseEntity.ok(bancaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar banca por ID", description = "Retorna uma banca específica pelo ID")
    public ResponseEntity<BancaResponse> buscarPorId(
            @Parameter(description = "ID da banca") @PathVariable Long id) {
        log.info("Buscando banca por ID: {}", id);
        return bancaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/discente/{discenteId}")
    @Operation(summary = "Listar bancas por discente", description = "Retorna bancas de um discente")
    public ResponseEntity<List<BancaResponse>> listarPorDiscente(
            @Parameter(description = "ID do discente") @PathVariable Long discenteId) {
        log.info("Listando bancas do discente ID: {}", discenteId);
        return ResponseEntity.ok(bancaService.findByDiscente(discenteId));
    }

    @GetMapping("/programa/{programaId}")
    @Operation(summary = "Listar bancas por programa", description = "Retorna bancas de um programa com paginação")
    public ResponseEntity<PagedResponse<BancaResponse>> listarPorPrograma(
            @Parameter(description = "ID do programa") @PathVariable Long programaId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Listando bancas do programa ID: {} com paginação", programaId);
        return ResponseEntity.ok(PagedResponse.of(bancaService.findByPrograma(programaId, pageable)));
    }

    @GetMapping("/proximas")
    @Operation(summary = "Listar próximas bancas", description = "Retorna bancas agendadas/confirmadas futuras")
    public ResponseEntity<List<BancaResponse>> listarProximas() {
        log.info("Listando próximas bancas");
        return ResponseEntity.ok(bancaService.findProximasBancas());
    }

    @GetMapping("/proximas/page")
    @Operation(summary = "Listar próximas bancas (paginado)", description = "Retorna próximas bancas com paginação")
    public ResponseEntity<PagedResponse<BancaResponse>> listarProximasPaginado(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Listando próximas bancas com paginação");
        return ResponseEntity.ok(PagedResponse.of(bancaService.findProximasBancas(pageable)));
    }

    @GetMapping("/sem-ata")
    @Operation(summary = "Listar bancas sem ata", description = "Retorna bancas realizadas sem documento de ata")
    public ResponseEntity<List<BancaResponse>> listarSemAta() {
        log.info("Listando bancas sem ata");
        return ResponseEntity.ok(bancaService.findBancasSemAta());
    }

    @PostMapping
    @Operation(summary = "Criar nova banca", description = "Cria uma nova banca de defesa/qualificação")
    public ResponseEntity<BancaResponse> criar(
            @Valid @RequestBody BancaCreateRequest request) {
        log.info("Criando nova banca para discente ID: {}", request.getDiscenteId());
        BancaResponse created = bancaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar banca", description = "Atualiza os dados de uma banca existente")
    public ResponseEntity<BancaResponse> atualizar(
            @Parameter(description = "ID da banca") @PathVariable Long id,
            @Valid @RequestBody BancaUpdateRequest request) {
        log.info("Atualizando banca ID: {}", id);
        return bancaService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/realizar")
    @Operation(summary = "Marcar banca como realizada", description = "Registra que a banca foi realizada e define o resultado")
    public ResponseEntity<BancaResponse> marcarComoRealizada(
            @Parameter(description = "ID da banca") @PathVariable Long id,
            @Parameter(description = "Resultado da banca") @RequestParam String resultado) {
        log.info("Marcando banca como realizada: ID {}, Resultado: {}", id, resultado);

        BancaEntity.ResultadoBanca resultadoBanca;
        try {
            resultadoBanca = BancaEntity.ResultadoBanca.valueOf(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return bancaService.marcarComoRealizada(id, resultadoBanca)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar banca", description = "Cancela uma banca agendada")
    public ResponseEntity<BancaResponse> cancelar(
            @Parameter(description = "ID da banca") @PathVariable Long id,
            @Parameter(description = "Motivo do cancelamento") @RequestParam(required = false) String motivo) {
        log.info("Cancelando banca ID: {}", id);
        return bancaService.cancelar(id, motivo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/reagendar")
    @Operation(summary = "Reagendar banca", description = "Reagenda uma banca para nova data/hora")
    public ResponseEntity<BancaResponse> reagendar(
            @Parameter(description = "ID da banca") @PathVariable Long id,
            @Parameter(description = "Nova data e hora (formato ISO: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime novaDataHora) {
        log.info("Reagendando banca ID: {} para {}", id, novaDataHora);
        return bancaService.reagendar(id, novaDataHora)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar banca", description = "Remove uma banca do sistema (não permite bancas já realizadas)")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da banca") @PathVariable Long id) {
        log.info("Deletando banca ID: {}", id);
        return bancaService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/validar-composicao")
    @Operation(summary = "Validar composição da banca", description = "Valida se a composição da banca atende às regras de negócio")
    public ResponseEntity<Void> validarComposicao(
            @Parameter(description = "ID da banca") @PathVariable Long id) {
        log.info("Validando composição da banca ID: {}", id);
        try {
            bancaService.validarComposicaoBanca(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            log.warn("Composição da banca inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
