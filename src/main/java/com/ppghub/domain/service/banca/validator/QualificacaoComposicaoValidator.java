package com.ppghub.domain.service.banca.validator;

import com.ppghub.domain.exception.BusinessRuleException;
import com.ppghub.domain.model.ComposicaoBanca;
import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validador de composição para Bancas de Qualificação (Mestrado/Doutorado).
 *
 * <h3>Regras específicas de Qualificação:</h3>
 * <ul>
 *   <li>Mínimo 3 titulares, máximo 5</li>
 *   <li><strong>SEM restrição obrigatória de membros externos</strong> (pode ter 0, 1 ou 2)</li>
 *   <li>Exatamente 1 presidente</li>
 * </ul>
 *
 * <h3>Diferença crucial vs Defesa:</h3>
 * <p>
 * Qualificação é mais flexível - permite bancas compostas apenas por membros internos,
 * enquanto defesa OBRIGATORIAMENTE requer pelo menos 1 externo.
 * </p>
 *
 * <h3>Exemplos de composições válidas:</h3>
 * <ul>
 *   <li>3 internos + 0 externos ✅ (diferença para defesa!)</li>
 *   <li>2 internos + 1 externo ✅</li>
 *   <li>1 interno + 2 externos ✅</li>
 *   <li>3 internos + 2 externos ✅ (total 5)</li>
 * </ul>
 */
@Component
@Slf4j
public class QualificacaoComposicaoValidator implements BancaComposicaoValidator {

    private static final int MIN_TITULARES = 3;
    private static final int MAX_TITULARES = 5;
    // ✅ Qualificação: SEM restrição mínima de externos (pode ter 0)

    @Override
    public void validarComposicao(ComposicaoBanca composicao) {
        log.debug("Validando composição de banca de QUALIFICAÇÃO: {} titulares ({} internos, {} externos)",
            composicao.getNumeroTitulares(),
            composicao.getNumeroTitularesInternos(),
            composicao.getNumeroTitularesExternos());

        validarNumeroTitulares(composicao);
        validarPresidente(composicao);
        // ✅ Não valida número mínimo de externos - qualificação é flexível

        log.info("Composição de banca de QUALIFICAÇÃO validada com sucesso: {} titulares ({} internos, {} externos)",
            composicao.getNumeroTitulares(),
            composicao.getNumeroTitularesInternos(),
            composicao.getNumeroTitularesExternos());
    }

    private void validarNumeroTitulares(ComposicaoBanca composicao) {
        int numTitulares = composicao.getNumeroTitulares();

        if (numTitulares < MIN_TITULARES) {
            throw new BusinessRuleException(
                String.format("Banca de qualificação deve ter no mínimo %d membros titulares. Atual: %d",
                    MIN_TITULARES, numTitulares)
            );
        }

        if (numTitulares > MAX_TITULARES) {
            throw new BusinessRuleException(
                String.format("Banca de qualificação deve ter no máximo %d membros titulares. Atual: %d",
                    MAX_TITULARES, numTitulares)
            );
        }
    }

    private void validarPresidente(ComposicaoBanca composicao) {
        if (!composicao.temPresidente()) {
            throw new BusinessRuleException("Banca de qualificação deve ter exatamente 1 presidente");
        }

        if (!composicao.temExatamenteUmPresidente()) {
            throw new BusinessRuleException(
                String.format("Banca de qualificação deve ter apenas 1 presidente. Atual: %d",
                    composicao.getPresidentes().size())
            );
        }
    }

    @Override
    public BancaEntity.TipoBanca[] getTiposSuportados() {
        return new BancaEntity.TipoBanca[] {
            BancaEntity.TipoBanca.QUALIFICACAO_MESTRADO,
            BancaEntity.TipoBanca.QUALIFICACAO_DOUTORADO
        };
    }
}
