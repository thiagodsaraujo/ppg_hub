package com.ppghub.domain.model;

import com.ppghub.infrastructure.persistence.entity.MembroBancaEntity;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Value Object que representa a composição de uma banca.
 * Encapsula métricas e análise da composição de membros.
 *
 * Este VO fornece métodos de consulta para facilitar validações
 * e análises de composição de bancas de defesa e qualificação.
 */
@Value
@Builder
public class ComposicaoBanca {
    List<MembroBancaEntity> membros;

    /**
     * Retorna apenas os membros titulares da banca.
     */
    public List<MembroBancaEntity> getTitulares() {
        return membros.stream()
                .filter(MembroBancaEntity::isTitular)
                .collect(Collectors.toList());
    }

    /**
     * Retorna apenas os membros suplentes da banca.
     */
    public List<MembroBancaEntity> getSuplentes() {
        return membros.stream()
                .filter(MembroBancaEntity::isSuplente)
                .collect(Collectors.toList());
    }

    /**
     * Retorna apenas os membros externos da banca.
     */
    public List<MembroBancaEntity> getExternos() {
        return membros.stream()
                .filter(MembroBancaEntity::isExterno)
                .collect(Collectors.toList());
    }

    /**
     * Retorna apenas os membros internos da banca.
     */
    public List<MembroBancaEntity> getInternos() {
        return membros.stream()
                .filter(MembroBancaEntity::isInterno)
                .collect(Collectors.toList());
    }

    /**
     * Retorna os membros que são presidentes da banca.
     */
    public List<MembroBancaEntity> getPresidentes() {
        return membros.stream()
                .filter(m -> m.getFuncao() == MembroBancaEntity.Funcao.PRESIDENTE)
                .collect(Collectors.toList());
    }

    /**
     * Retorna apenas os titulares externos.
     */
    public List<MembroBancaEntity> getTitularesExternos() {
        return membros.stream()
                .filter(MembroBancaEntity::isTitular)
                .filter(MembroBancaEntity::isExterno)
                .collect(Collectors.toList());
    }

    /**
     * Retorna apenas os titulares internos.
     */
    public List<MembroBancaEntity> getTitularesInternos() {
        return membros.stream()
                .filter(MembroBancaEntity::isTitular)
                .filter(MembroBancaEntity::isInterno)
                .collect(Collectors.toList());
    }

    /**
     * Retorna o número total de membros titulares.
     */
    public int getNumeroTitulares() {
        return getTitulares().size();
    }

    /**
     * Retorna o número total de membros externos.
     */
    public int getNumeroExternos() {
        return getExternos().size();
    }

    /**
     * Retorna o número total de membros internos.
     */
    public int getNumeroInternos() {
        return getInternos().size();
    }

    /**
     * Retorna o número de titulares externos.
     */
    public int getNumeroTitularesExternos() {
        return getTitularesExternos().size();
    }

    /**
     * Retorna o número de titulares internos.
     */
    public int getNumeroTitularesInternos() {
        return getTitularesInternos().size();
    }

    /**
     * Verifica se a banca tem pelo menos um presidente.
     */
    public boolean temPresidente() {
        return !getPresidentes().isEmpty();
    }

    /**
     * Verifica se a banca tem exatamente um presidente.
     */
    public boolean temExatamenteUmPresidente() {
        return getPresidentes().size() == 1;
    }
}
