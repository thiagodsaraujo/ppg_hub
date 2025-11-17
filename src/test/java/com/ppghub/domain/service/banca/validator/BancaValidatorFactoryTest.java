package com.ppghub.domain.service.banca.validator;

import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Factory de Validadores de Composição de Bancas")
class BancaValidatorFactoryTest {

    private BancaValidatorFactory factory;
    private List<BancaComposicaoValidator> validators;

    @BeforeEach
    void setUp() {
        validators = List.of(
            new DefesaComposicaoValidator(),
            new QualificacaoComposicaoValidator()
        );
        factory = new BancaValidatorFactory(validators);
    }

    @Test
    @DisplayName("Deve retornar DefesaComposicaoValidator para DEFESA_MESTRADO")
    void deveRetornarDefesaValidatorParaDefesaMestrado() {
        // When
        BancaComposicaoValidator validator = factory.getValidator(BancaEntity.TipoBanca.DEFESA_MESTRADO);

        // Then
        assertNotNull(validator);
        assertInstanceOf(DefesaComposicaoValidator.class, validator);
    }

    @Test
    @DisplayName("Deve retornar DefesaComposicaoValidator para DEFESA_DOUTORADO")
    void deveRetornarDefesaValidatorParaDefesaDoutorado() {
        // When
        BancaComposicaoValidator validator = factory.getValidator(BancaEntity.TipoBanca.DEFESA_DOUTORADO);

        // Then
        assertNotNull(validator);
        assertInstanceOf(DefesaComposicaoValidator.class, validator);
    }

    @Test
    @DisplayName("Deve retornar DefesaComposicaoValidator para DEFESA_DOUTORADO_DIRETO")
    void deveRetornarDefesaValidatorParaDefesaDoutoradoDireto() {
        // When
        BancaComposicaoValidator validator = factory.getValidator(BancaEntity.TipoBanca.DEFESA_DOUTORADO_DIRETO);

        // Then
        assertNotNull(validator);
        assertInstanceOf(DefesaComposicaoValidator.class, validator);
    }

    @Test
    @DisplayName("Deve retornar QualificacaoComposicaoValidator para QUALIFICACAO_MESTRADO")
    void deveRetornarQualificacaoValidatorParaQualificacaoMestrado() {
        // When
        BancaComposicaoValidator validator = factory.getValidator(BancaEntity.TipoBanca.QUALIFICACAO_MESTRADO);

        // Then
        assertNotNull(validator);
        assertInstanceOf(QualificacaoComposicaoValidator.class, validator);
    }

    @Test
    @DisplayName("Deve retornar QualificacaoComposicaoValidator para QUALIFICACAO_DOUTORADO")
    void deveRetornarQualificacaoValidatorParaQualificacaoDoutorado() {
        // When
        BancaComposicaoValidator validator = factory.getValidator(BancaEntity.TipoBanca.QUALIFICACAO_DOUTORADO);

        // Then
        assertNotNull(validator);
        assertInstanceOf(QualificacaoComposicaoValidator.class, validator);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para EXAME_PROFICIENCIA (sem validador)")
    void deveLancarExcecaoParaTipoSemValidador() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.getValidator(BancaEntity.TipoBanca.EXAME_PROFICIENCIA)
        );

        assertTrue(exception.getMessage().contains("Nenhum validador encontrado"));
        assertTrue(exception.getMessage().contains("EXAME_PROFICIENCIA"));
    }

    @Test
    @DisplayName("Deve retornar o mesmo validador para tipos diferentes do mesmo grupo")
    void deveRetornarMesmoValidadorParaTiposMesmoGrupo() {
        // When
        BancaComposicaoValidator validator1 = factory.getValidator(BancaEntity.TipoBanca.DEFESA_MESTRADO);
        BancaComposicaoValidator validator2 = factory.getValidator(BancaEntity.TipoBanca.DEFESA_DOUTORADO);

        // Then
        assertSame(validator1, validator2, "Deve retornar a mesma instância do validador");
    }
}
