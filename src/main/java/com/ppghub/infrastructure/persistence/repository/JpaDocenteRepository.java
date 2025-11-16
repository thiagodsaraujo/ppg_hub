package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.DocenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para Docente.
 */
@Repository
public interface JpaDocenteRepository extends JpaRepository<DocenteEntity, Long> {

    /**
     * Busca docente por CPF
     */
    Optional<DocenteEntity> findByCpf(String cpf);

    /**
     * Busca docente por Lattes ID
     */
    Optional<DocenteEntity> findByLattesId(String lattesId);

    /**
     * Busca docente por ORCID
     */
    Optional<DocenteEntity> findByOrcid(String orcid);

    /**
     * Busca docente por OpenAlex Author ID
     */
    Optional<DocenteEntity> findByOpenalexAuthorId(String openalexAuthorId);

    /**
     * Busca docentes por instituição
     */
    List<DocenteEntity> findByInstituicaoId(Long instituicaoId);

    /**
     * Busca docentes ativos por instituição
     */
    @Query("SELECT d FROM DocenteEntity d WHERE d.instituicao.id = :instituicaoId AND d.ativo = true")
    List<DocenteEntity> findDocentesAtivosByInstituicao(@Param("instituicaoId") Long instituicaoId);

    /**
     * Busca docentes que precisam de sincronização com OpenAlex
     */
    @Query("SELECT d FROM DocenteEntity d WHERE d.openalexAuthorId IS NOT NULL " +
           "AND (d.openalexLastSyncAt IS NULL OR d.openalexLastSyncAt < :thresholdDate)")
    List<DocenteEntity> findDocentesNeedingSync(@Param("thresholdDate") java.time.LocalDateTime thresholdDate);

    /**
     * Busca docentes sem OpenAlex ID
     */
    @Query("SELECT d FROM DocenteEntity d WHERE d.openalexAuthorId IS NULL AND d.ativo = true")
    List<DocenteEntity> findDocentesSemOpenAlexId();
}
