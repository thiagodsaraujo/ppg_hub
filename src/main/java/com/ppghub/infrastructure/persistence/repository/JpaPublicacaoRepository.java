package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.PublicacaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para Publicação.
 */
@Repository
public interface JpaPublicacaoRepository extends JpaRepository<PublicacaoEntity, Long> {

    /**
     * Busca publicação por OpenAlex Work ID
     */
    Optional<PublicacaoEntity> findByOpenalexWorkId(String openalexWorkId);

    /**
     * Busca publicação por DOI
     */
    Optional<PublicacaoEntity> findByDoi(String doi);

    /**
     * Busca publicações por ano
     */
    List<PublicacaoEntity> findByAnoPublicacao(Integer ano);

    /**
     * Busca publicações por tipo
     */
    List<PublicacaoEntity> findByTipo(String tipo);

    /**
     * Busca publicações de uma instituição (via autorias)
     */
    @Query("SELECT DISTINCT p FROM PublicacaoEntity p " +
           "JOIN p.autorias a " +
           "JOIN a.docente d " +
           "WHERE d.instituicao.id = :instituicaoId")
    Page<PublicacaoEntity> findByInstituicaoId(@Param("instituicaoId") Long instituicaoId, Pageable pageable);

    /**
     * Busca publicações de um docente
     */
    @Query("SELECT p FROM PublicacaoEntity p " +
           "JOIN p.autorias a " +
           "WHERE a.docente.id = :docenteId " +
           "ORDER BY p.anoPublicacao DESC")
    List<PublicacaoEntity> findByDocenteId(@Param("docenteId") Long docenteId);

    /**
     * Busca publicações mais citadas
     */
    Page<PublicacaoEntity> findByOrderByCitedByCountDesc(Pageable pageable);

    /**
     * Verifica se publicação já existe
     */
    boolean existsByOpenalexWorkId(String openalexWorkId);

    boolean existsByDoi(String doi);
}
