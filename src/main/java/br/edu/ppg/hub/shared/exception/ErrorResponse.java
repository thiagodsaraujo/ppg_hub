package br.edu.ppg.hub.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO padronizado para respostas de erro da API.
 * Suporta erros simples, de validação e com informações adicionais.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Timestamp do erro.
     */
    private LocalDateTime timestamp;

    /**
     * Status HTTP do erro.
     */
    private Integer status;

    /**
     * Nome do status HTTP (ex: "Bad Request", "Not Found").
     */
    private String error;

    /**
     * Mensagem de erro principal.
     */
    private String message;

    /**
     * Detalhes adicionais do erro.
     */
    private String details;

    /**
     * Path da requisição que gerou o erro.
     */
    private String path;

    /**
     * Lista de erros de validação (campo: mensagem).
     */
    private List<FieldError> fieldErrors;

    /**
     * Informações extras (opcional).
     */
    private Map<String, Object> additionalInfo;

    /**
     * Representa um erro de validação de campo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
