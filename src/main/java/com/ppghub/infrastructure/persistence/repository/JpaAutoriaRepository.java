package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.AutoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA para Autoria.
 */
@Repository
public interface JpaAutoriaRepository extends JpaRepository<AutoriaEntity, Long> {

    /**
     * Busca autorias por publicação
     */
    List<AutoriaEntity> findByPublicacaoId(Long publicacaoId);

    /**
     * Busca autorias por docente
     */
    List<AutoriaEntity> findByDocenteId(Long docenteId);

    /**
     * Busca autorias por OpenAlex Author ID (quando não há docente)
     */
    List<AutoriaEntity> findByRawAuthorOpenalexId(String rawAuthorOpenalexId);

    /**
     * Busca primeiro autor de uma publicação
     */
    @Query("SELECT a FROM AutoriaEntity a WHERE a.publicacao.id = :publicacaoId AND a.authorPosition = 'first'")
    AutoriaEntity findPrimeiroAutor(@Param("publicacaoId") Long publicacaoId);

    /**
     * Busca autor correspondente de uma publicação
     */
    @Query("SELECT a FROM AutoriaEntity a WHERE a.publicacao.id = :publicacaoId AND a.isCorresponding = true")
    AutoriaEntity findAutorCorrespondente(@Param("publicacaoId") Long publicacaoId);

    /**
     * Conta publicações por docente
     */
    @Query("SELECT COUNT(a) FROM AutoriaEntity a WHERE a.docente.id = :docenteId")
    Long countByDocenteId(@Param("docenteId") Long docenteId);
}
