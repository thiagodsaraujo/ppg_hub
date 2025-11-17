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

@DisplayName("Validador de Composição - Bancas de Qualificação")
class QualificacaoComposicaoValidatorTest {

    private QualificacaoComposicaoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new QualificacaoComposicaoValidator();
    }

    @Test
    @DisplayName("Deve aceitar banca de qualificação com 3 internos e 0 externos (diferença crucial vs defesa)")
    void deveAceitarBancaSomenteInternos() {
        // Given - 3 internos, 0 externos (válido para qualificação, inválido para defesa)
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> validator.validarComposicao(composicao));
    }

    @Test
    @DisplayName("Deve aceitar banca de qualificação com 2 internos e 1 externo")
    void deveAceitarBancaMista_2Internos1Externo() {
        // Given
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        assertDoesNotThrow(() -> validator.validarComposicao(composicao));
    }

    @Test
    @DisplayName("Deve aceitar banca de qualificação com 1 interno e 2 externos")
    void deveAceitarBancaMista_1Interno2Externos() {
        // Given
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));
        membros.add(criarMembroTitular(false, MembroBancaEntity.Funcao.MEMBRO_EXTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        assertDoesNotThrow(() -> validator.validarComposicao(composicao));
    }

    @Test
    @DisplayName("Deve aceitar banca de qualificação com 5 titulares (3 internos + 2 externos)")
    void deveAceitarBanca5Titulares() {
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
    @DisplayName("Deve aceitar banca de qualificação com 4 internos (sem externos)")
    void deveAceitarBanca4Internos() {
        // Given - Flexibilidade de qualificação
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        assertDoesNotThrow(() -> validator.validarComposicao(composicao));
    }

    @Test
    @DisplayName("Deve rejeitar banca de qualificação com menos de 3 titulares")
    void deveRejeitarBancaComMenosDe3Titulares() {
        // Given - Apenas 2 titulares
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("no mínimo 3 membros titulares"));
    }

    @Test
    @DisplayName("Deve rejeitar banca de qualificação com mais de 5 titulares")
    void deveRejeitarBancaComMaisDe5Titulares() {
        // Given - 6 titulares
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("no máximo 5 membros titulares"));
    }

    @Test
    @DisplayName("Deve rejeitar banca de qualificação sem presidente")
    void deveRejeitarBancaSemPresidente() {
        // Given - 3 membros mas nenhum é presidente
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("exatamente 1 presidente"));
    }

    @Test
    @DisplayName("Deve rejeitar banca de qualificação com 2 presidentes")
    void deveRejeitarBancaCom2Presidentes() {
        // Given - 4 membros com 2 presidentes
        List<MembroBancaEntity> membros = new ArrayList<>();
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.PRESIDENTE));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));
        membros.add(criarMembroTitular(true, MembroBancaEntity.Funcao.MEMBRO_INTERNO));

        ComposicaoBanca composicao = ComposicaoBanca.builder().membros(membros).build();

        // When & Then
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> validator.validarComposicao(composicao)
        );

        assertTrue(exception.getMessage().contains("apenas 1 presidente"));
    }

    @Test
    @DisplayName("Deve retornar os tipos de banca suportados (Qualificação Mestrado e Doutorado)")
    void deveRetornarTiposSuportados() {
        // When
        BancaEntity.TipoBanca[] tipos = validator.getTiposSuportados();

        // Then
        assertEquals(2, tipos.length);
        assertArrayEquals(
            new BancaEntity.TipoBanca[] {
                BancaEntity.TipoBanca.QUALIFICACAO_MESTRADO,
                BancaEntity.TipoBanca.QUALIFICACAO_DOUTORADO
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
