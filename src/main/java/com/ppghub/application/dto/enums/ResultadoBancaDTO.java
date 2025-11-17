package com.ppghub.application.dto.enums;

/**
 * Enum para resultado de banca na camada de apresentação.
 * Este enum é usado nas requisições/respostas da API e é convertido
 * para/de BancaEntity.ResultadoBanca pela camada de serviço.
 */
public enum ResultadoBancaDTO {
    APROVADO,
    APROVADO_COM_RESTRICOES,
    APROVADO_COM_CORRECOES,
    REPROVADO
}
