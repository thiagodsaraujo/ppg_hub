package com.ppghub.domain.service.banca.validator;

import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Factory para obter o validador correto baseado no tipo de banca.
 * Implementa Strategy Pattern para seleção dinâmica de validador.
 *
 * <p>
 * Esta factory permite que o sistema escolha automaticamente o validador
 * apropriado para cada tipo de banca, sem necessidade de lógica condicional
 * espalhada pelo código.
 * </p>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * BancaComposicaoValidator validator = factory.getValidator(TipoBanca.DEFESA_MESTRADO);
 * validator.validarComposicao(composicao); // Usa DefesaComposicaoValidator
 * }</pre>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BancaValidatorFactory {

    private final List<BancaComposicaoValidator> validators;

    /**
     * Retorna o validador apropriado para o tipo de banca especificado.
     *
     * @param tipoBanca Tipo da banca (DEFESA_MESTRADO, QUALIFICACAO_DOUTORADO, etc.)
     * @return Validador correspondente ao tipo de banca
     * @throws IllegalArgumentException se nenhum validador for encontrado para o tipo
     */
    public BancaComposicaoValidator getValidator(BancaEntity.TipoBanca tipoBanca) {
        log.debug("Buscando validador para tipo de banca: {}", tipoBanca);

        BancaComposicaoValidator validator = validators.stream()
                .filter(v -> Arrays.asList(v.getTiposSuportados()).contains(tipoBanca))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Nenhum validador encontrado para tipo de banca: {}", tipoBanca);
                    return new IllegalArgumentException(
                        "Nenhum validador encontrado para tipo de banca: " + tipoBanca
                    );
                });

        log.debug("Validador selecionado: {} para tipo de banca: {}",
            validator.getClass().getSimpleName(), tipoBanca);

        return validator;
    }
}
