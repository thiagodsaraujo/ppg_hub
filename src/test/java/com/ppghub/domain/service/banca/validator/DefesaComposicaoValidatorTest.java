package com.ppghub.domain.service.banca.validator;

import com.ppghub.domain.exception.BusinessRuleException;
import com.ppghub.domain.model.ComposicaoBanca;
import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import com.ppghub.infrastructure.persistence.entity.MembroBancaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validador de Composição - Bancas de Defesa")
class DefesaComposicaoValidatorTest {

    private DefesaComposicaoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DefesaComposicaoValidator();
    }

    @Test
    @DisplayName("Deve aceitar banca de defesa com 3 titulares (2 internos + 1 externo)")
    void deveAceitarComposicaoValida_3Titulares_2Internos1Externo() {
        // Given
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));  // Interno Presidente
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO)); // Interno
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO)); // Externo

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> validator.validarComposicao(composicao));
    }

    @Test
    @DisplayName("Deve aceitar banca de defesa com 4 titulares (2 internos + 2 externos)")
    void deveAceitarComposicaoValida_4Titulares_2Internos2Externos() {
        // Given
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));  // Interno Presidente
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO)); // Interno
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO)); // Externo 1
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO)); // Externo 2

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        assertDoesNotThrow(() -> validator.validarComposicao(composicao));
    }

    @Test
    @DisplayName("Deve aceitar banca de defesa com 5 titulares (3 internos + 2 externos)")
    void deveAceitarComposicaoValida_5Titulares_3Internos2Externos() {
        // Given
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        assertDoesNotThrow(() -> validator.validarComposicao(composicao));
    }

    @Test
    @DisplayName("Deve rejeitar banca de defesa SEM membros externos (só internos)")
    void deveRejeitarBancaSemExternos() {
        // Given - 3 internos, 0 externos
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("no mínimo 1 membro titular externo"));
    }

    @Test
    @DisplayName("Deve rejeitar banca de defesa com 3 membros externos (máximo é 2)")
    void deveRejeitarBancaCom3Externos() {
        // Given - 1 interno + 3 externos = 4 titulares
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("no máximo 2 membros titulares externos"));
    }

    @Test
    @DisplayName("Deve rejeitar banca de defesa com menos de 3 titulares")
    void deveRejeitarBancaComMenosDe3Titulares() {
        // Given - Apenas 2 titulares
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("no mínimo 3 membros titulares"));
    }

    @Test
    @DisplayName("Deve rejeitar banca de defesa com mais de 5 titulares")
    void deveRejeitarBancaComMaisDe5Titulares() {
        // Given - 6 titulares
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("no máximo 5 membros titulares"));
    }

    @Test
    @DisplayName("Deve rejeitar banca de defesa sem presidente")
    void deveRejeitarBancaSemPresidente() {
        // Given - 3 membros mas nenhum é presidente
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("exatamente 1 presidente"));
    }

    @Test
    @DisplayName("Deve rejeitar banca de defesa com 2 presidentes")
    void deveRejeitarBancaCom2Presidentes() {
        // Given - 4 membros com 2 presidentes
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("apenas 1 presidente"));
    }

    @Test
    @DisplayName("Deve retornar os tipos de banca suportados (Defesa Mestrado, Doutorado, Doutorado Direto)")
    void deveRetornarTiposSuportados() {
        // When
        BancaEntity.TipoBanca[] tipos = validator.getTiposSuportados();

        // Then
        assertEquals(3, tipos.length);
        assertArrayEquals(
            new BancaEntity.TipoBanca[] {
                BancaEntity.TipoBanca.DEFESA_MESTRADO,
                BancaEntity.TipoBanca.DEFESA_DOUTORADO,
                BancaEntity.TipoBanca.DEFESA_DOUTORADO_DIRETO
            },
            tipos
        );
    }

    // Helper methods
    private MembroBancaEntity criarMembroTitular(boolean interno, MembroBancaEntity.Funcao funcao) {
        MembroBancaEntity membro = new MembroBancaEntity();
        membro.setTipoMembro(MembroBancaEntity.TipoMembro.TITULAR);
        membro.setFuncao(funcao);

        if (interno) {
            // Membro interno (tem docente)
            membro.setDocente(new com.ppghub.infrastructure.persistence.entity.DocenteEntity());
        } else {
            // Membro externo (tem professor externo)
            membro.setProfessorExterno(new com.ppghub.infrastructure.persistence.entity.ProfessorExternoEntity());
        }

        return membro;
    }
}
