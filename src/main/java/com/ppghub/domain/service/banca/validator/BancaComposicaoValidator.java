package com.ppghub.domain.service.banca.validator;

import com.ppghub.domain.model.ComposicaoBanca;
import com.ppghub.infrastructure.persistence.entity.BancaEntity;

/**
 * Strategy para validação de composição de bancas.
 * Cada tipo de banca (Defesa, Qualificação) tem regras específicas de composição.
 *
 * Este padrão permite adicionar novos tipos de validação sem modificar
 * o código existente (Open/Closed Principle).
 */
public interface BancaComposicaoValidator {

    /**
     * Valida a composição completa da banca.
     * Lança BusinessRuleException se houver violação de regras.
     *
     * @param composicao Composição da banca a validar
     * @throws com.ppghub.domain.exception.BusinessRuleException se a composição for inválida
     */
    void validarComposicao(ComposicaoBanca composicao);

    /**
     * Retorna os tipos de banca que este validador suporta.
     * Usado pela Factory para seleção do validador correto.
     *
     * @return Array com os tipos de banca suportados
     */
    BancaEntity.TipoBanca[] getTiposSuportados();
}
