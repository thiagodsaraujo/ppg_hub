package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.DocenteProgramaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para DocentePrograma.
 */
@Repository
public interface JpaDocenteProgramaRepository extends JpaRepository<DocenteProgramaEntity, DocenteProgramaEntity.DocenteProgramaId> {

    /**
     * Busca vínculos por docente
     */
    @Query("SELECT dp FROM DocenteProgramaEntity dp WHERE dp.docente.id = :docenteId")
    List<DocenteProgramaEntity> findByDocenteId(@Param("docenteId") Long docenteId);

    /**
     * Busca vínculos por programa
     */
    @Query("SELECT dp FROM DocenteProgramaEntity dp WHERE dp.programa.id = :programaId")
    List<DocenteProgramaEntity> findByProgramaId(@Param("programaId") Long programaId);

    /**
     * Busca vínculos ativos por programa (sem data fim)
     */
    @Query("SELECT dp FROM DocenteProgramaEntity dp WHERE dp.programa.id = :programaId AND dp.dataFim IS NULL")
    List<DocenteProgramaEntity> findVinculosAtivosByProgramaId(@Param("programaId") Long programaId);

    /**
     * Busca vínculos ativos por docente
     */
    @Query("SELECT dp FROM DocenteProgramaEntity dp WHERE dp.docente.id = :docenteId AND dp.dataFim IS NULL")
    List<DocenteProgramaEntity> findVinculosAtivosByDocenteId(@Param("docenteId") Long docenteId);

    /**
     * Busca vínculo específico docente-programa
     */
    @Query("SELECT dp FROM DocenteProgramaEntity dp WHERE dp.docente.id = :docenteId AND dp.programa.id = :programaId")
    Optional<DocenteProgramaEntity> findByDocenteIdAndProgramaId(@Param("docenteId") Long docenteId, @Param("programaId") Long programaId);

    /**
     * Busca docentes permanentes de um programa
     */
    @Query("SELECT dp FROM DocenteProgramaEntity dp WHERE dp.programa.id = :programaId AND dp.categoria = 'PERMANENTE' AND dp.dataFim IS NULL")
    List<DocenteProgramaEntity> findDocentesPermanentesByProgramaId(@Param("programaId") Long programaId);
}
