package com.ppghub.domain.service.banca.validator;

import com.ppghub.domain.exception.BusinessRuleException;
import com.ppghub.domain.model.ComposicaoBanca;
import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validador de composição para Bancas de Defesa (Mestrado/Doutorado).
 *
 * <h3>Regras específicas de Defesa:</h3>
 * <ul>
 *   <li>Mínimo 3 titulares, máximo 5</li>
 *   <li><strong>OBRIGATÓRIO pelo menos 1 membro externo</strong></li>
 *   <li>Máximo 2 membros externos</li>
 *   <li>Exatamente 1 presidente</li>
 * </ul>
 *
 * <h3>Exemplos de composições válidas:</h3>
 * <ul>
 *   <li>2 internos + 1 externo ✅</li>
 *   <li>1 interno + 2 externos ✅</li>
 *   <li>3 internos + 2 externos ✅ (total 5)</li>
 * </ul>
 *
 * <h3>Exemplos de composições inválidas:</h3>
 * <ul>
 *   <li>3 internos + 0 externos ❌ (falta externo)</li>
 *   <li>1 interno + 3 externos ❌ (máximo 2 externos)</li>
 * </ul>
 */
@Component
@Slf4j
public class DefesaComposicaoValidator implements BancaComposicaoValidator {

    private static final int MIN_TITULARES = 3;
    private static final int MAX_TITULARES = 5;
    private static final int MIN_EXTERNOS = 1;  // ✅ Obrigatório para defesa
    private static final int MAX_EXTERNOS = 2;

    @Override
    public void validarComposicao(ComposicaoBanca composicao) {
        log.debug("Validando composição de banca de DEFESA: {} titulares ({} internos, {} externos)",
            composicao.getNumeroTitulares(),
            composicao.getNumeroTitularesInternos(),
            composicao.getNumeroTitularesExternos());

        validarNumeroTitulares(composicao);
        validarPresidente(composicao);
        validarMembrosExternos(composicao);  // ✅ Regra específica de defesa

        log.info("Composição de banca de DEFESA validada com sucesso: {} titulares ({} internos, {} externos)",
            composicao.getNumeroTitulares(),
            composicao.getNumeroTitularesInternos(),
            composicao.getNumeroTitularesExternos());
    }

    private void validarNumeroTitulares(ComposicaoBanca composicao) {
        int numTitulares = composicao.getNumeroTitulares();

        if (numTitulares < MIN_TITULARES) {
            throw new BusinessRuleException(
                String.format("Banca de defesa deve ter no mínimo %d membros titulares. Atual: %d",
                    MIN_TITULARES, numTitulares)
            );
        }

        if (numTitulares > MAX_TITULARES) {
            throw new BusinessRuleException(
                String.format("Banca de defesa deve ter no máximo %d membros titulares. Atual: %d",
                    MAX_TITULARES, numTitulares)
            );
        }
    }

    private void validarPresidente(ComposicaoBanca composicao) {
        if (!composicao.temPresidente()) {
            throw new BusinessRuleException("Banca de defesa deve ter exatamente 1 presidente");
        }

        if (!composicao.temExatamenteUmPresidente()) {
            throw new BusinessRuleException(
                String.format("Banca de defesa deve ter apenas 1 presidente. Atual: %d",
                    composicao.getPresidentes().size())
            );
        }
    }

    /**
     * Valida a regra específica de bancas de defesa sobre membros externos.
     * OBRIGATÓRIO: pelo menos 1 externo, máximo 2 externos.
     */
    private void validarMembrosExternos(ComposicaoBanca composicao) {
        int numTitularesExternos = composicao.getNumeroTitularesExternos();

        // ✅ REGRA ESPECÍFICA DE DEFESA: Obrigatório pelo menos 1 externo
        if (numTitularesExternos < MIN_EXTERNOS) {
            throw new BusinessRuleException(
                String.format(
                    "Banca de defesa deve ter no mínimo %d membro titular externo. " +
                    "Atual: %d externos de %d titulares totais. " +
                    "Composição válida: ex: 2 internos + 1 externo, ou 1 interno + 2 externos",
                    MIN_EXTERNOS, numTitularesExternos, composicao.getNumeroTitulares())
            );
        }

        if (numTitularesExternos > MAX_EXTERNOS) {
            throw new BusinessRuleException(
                String.format(
                    "Banca de defesa deve ter no máximo %d membros titulares externos. " +
                    "Atual: %d externos de %d titulares totais",
                    MAX_EXTERNOS, numTitularesExternos, composicao.getNumeroTitulares())
            );
        }

        log.debug("Validação de externos aprovada: {} externos de {} titulares",
            numTitularesExternos, composicao.getNumeroTitulares());
    }

    @Override
    public BancaEntity.TipoBanca[] getTiposSuportados() {
        return new BancaEntity.TipoBanca[] {
            BancaEntity.TipoBanca.DEFESA_MESTRADO,
            BancaEntity.TipoBanca.DEFESA_DOUTORADO,
            BancaEntity.TipoBanca.DEFESA_DOUTORADO_DIRETO
        };
    }
}
