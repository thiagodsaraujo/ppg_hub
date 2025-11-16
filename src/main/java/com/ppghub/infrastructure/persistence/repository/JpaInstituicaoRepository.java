package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para Instituição.
 */
@Repository
public interface JpaInstituicaoRepository extends JpaRepository<InstituicaoEntity, Long> {

    /**
     * Busca instituição por sigla
     */
    Optional<InstituicaoEntity> findBySigla(String sigla);

    /**
     * Busca instituição por CNPJ
     */
    Optional<InstituicaoEntity> findByCnpj(String cnpj);

    /**
     * Busca instituição por OpenAlex ID
     */
    Optional<InstituicaoEntity> findByOpenalexInstitutionId(String openalexInstitutionId);

    /**
     * Busca instituição por ROR ID
     */
    Optional<InstituicaoEntity> findByRorId(String rorId);

    /**
     * Busca instituições por cidade e estado
     */
    List<InstituicaoEntity> findByCidadeAndEstado(String cidade, String estado);

    /**
     * Busca instituições ativas
     */
    List<InstituicaoEntity> findByAtivoTrue();

    /**
     * Busca instituições por tipo
     */
    List<InstituicaoEntity> findByTipo(String tipo);

    /**
     * Busca instituições que precisam de sincronização com OpenAlex
     * (nunca sincronizadas ou sincronizadas há mais de X dias)
     */
    @Query("SELECT i FROM InstituicaoEntity i WHERE i.openalexInstitutionId IS NOT NULL " +
           "AND (i.openalexLastSyncAt IS NULL OR i.openalexLastSyncAt < :thresholdDate)")
    List<InstituicaoEntity> findInstituicoesNeedingSync(@Param("thresholdDate") java.time.LocalDateTime thresholdDate);

    /**
     * Busca instituições sem OpenAlex ID (para matching)
     */
    @Query("SELECT i FROM InstituicaoEntity i WHERE i.openalexInstitutionId IS NULL AND i.ativo = true")
    List<InstituicaoEntity> findInstituicoesSemOpenAlexId();

    /**
     * Busca por nome (case insensitive, partial match)
     */
    @Query("SELECT i FROM InstituicaoEntity i WHERE LOWER(i.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<InstituicaoEntity> searchByNome(@Param("nome") String nome, Pageable pageable);

    /**
     * Busca instituições com mais publicações no OpenAlex
     */
    @Query("SELECT i FROM InstituicaoEntity i WHERE i.openalexWorksCount > 0 ORDER BY i.openalexWorksCount DESC")
    Page<InstituicaoEntity> findTopByPublicacoes(Pageable pageable);

    /**
     * Conta instituições por estado
     */
    @Query("SELECT i.estado, COUNT(i) FROM InstituicaoEntity i GROUP BY i.estado")
    List<Object[]> countByEstado();

    /**
     * Verifica se existe instituição com CNPJ
     */
    boolean existsByCnpj(String cnpj);

    /**
     * Verifica se existe instituição com sigla
     */
    boolean existsBySigla(String sigla);
}
