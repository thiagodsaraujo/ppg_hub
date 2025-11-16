package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.DiscenteEntity;
import com.ppghub.infrastructure.persistence.entity.DiscenteEntity.StatusMatricula;
import com.ppghub.infrastructure.persistence.entity.DiscenteEntity.NivelFormacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para Discente.
 */
@Repository
public interface JpaDiscenteRepository extends JpaRepository<DiscenteEntity, Long> {

    /**
     * Busca discente por matrícula
     */
    Optional<DiscenteEntity> findByMatricula(String matricula);

    /**
     * Busca discente por email
     */
    Optional<DiscenteEntity> findByEmail(String email);

    /**
     * Busca discente por CPF
     */
    Optional<DiscenteEntity> findByCpf(String cpf);

    /**
     * Busca discente por Lattes ID
     */
    Optional<DiscenteEntity> findByLattesId(String lattesId);

    /**
     * Busca discente por ORCID
     */
    Optional<DiscenteEntity> findByOrcid(String orcid);

    /**
     * Busca discente por OpenAlex Author ID
     */
    Optional<DiscenteEntity> findByOpenalexAuthorId(String openalexAuthorId);

    /**
     * Busca discentes por programa
     */
    List<DiscenteEntity> findByProgramaId(Long programaId);

    /**
     * Busca discentes por programa com paginação
     */
    Page<DiscenteEntity> findByProgramaId(Long programaId, Pageable pageable);

    /**
     * Busca discentes por orientador
     */
    List<DiscenteEntity> findByOrientadorId(Long orientadorId);

    /**
     * Busca discentes por status de matrícula
     */
    List<DiscenteEntity> findByStatusMatricula(StatusMatricula statusMatricula);

    /**
     * Busca discentes por nível de formação
     */
    List<DiscenteEntity> findByNivelFormacao(NivelFormacao nivelFormacao);

    /**
     * Busca discentes ativos por programa
     */
    @Query("SELECT d FROM DiscenteEntity d WHERE d.programa.id = :programaId AND d.statusMatricula = 'ATIVO'")
    List<DiscenteEntity> findDiscentesAtivosByPrograma(@Param("programaId") Long programaId);

    /**
     * Busca discentes por nome (busca parcial, case insensitive)
     */
    @Query("SELECT d FROM DiscenteEntity d WHERE LOWER(d.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<DiscenteEntity> searchByNome(@Param("nome") String nome, Pageable pageable);

    /**
     * Busca discentes que já defenderam
     */
    @Query("SELECT d FROM DiscenteEntity d WHERE d.dataDefesa IS NOT NULL ORDER BY d.dataDefesa DESC")
    List<DiscenteEntity> findDiscentesQueDefenderam();

    /**
     * Busca discentes que ainda não defenderam (candidatos a defesa)
     */
    @Query("SELECT d FROM DiscenteEntity d WHERE d.dataDefesa IS NULL AND d.statusMatricula = 'ATIVO'")
    List<DiscenteEntity> findDiscentesCandidatosDefesa();

    /**
     * Busca discentes por período de ingresso
     */
    @Query("SELECT d FROM DiscenteEntity d WHERE d.dataIngresso BETWEEN :dataInicio AND :dataFim")
    List<DiscenteEntity> findByPeriodoIngresso(
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    /**
     * Busca discentes sem orientador definido
     */
    @Query("SELECT d FROM DiscenteEntity d WHERE d.orientador IS NULL AND d.statusMatricula = 'ATIVO'")
    List<DiscenteEntity> findDiscentesSemOrientador();

    /**
     * Conta discentes por programa e status
     */
    @Query("SELECT COUNT(d) FROM DiscenteEntity d WHERE d.programa.id = :programaId AND d.statusMatricula = :status")
    Long countByProgramaAndStatus(
        @Param("programaId") Long programaId,
        @Param("status") StatusMatricula status
    );

    /**
     * Busca discentes que precisam de sincronização com OpenAlex
     */
    @Query("SELECT d FROM DiscenteEntity d WHERE d.openalexAuthorId IS NOT NULL " +
           "AND (d.openalexLastSyncAt IS NULL OR d.openalexLastSyncAt < :thresholdDate)")
    List<DiscenteEntity> findDiscentesNeedingSync(@Param("thresholdDate") java.time.LocalDateTime thresholdDate);

    /**
     * Busca discentes por programa e nível de formação
     */
    @Query("SELECT d FROM DiscenteEntity d WHERE d.programa.id = :programaId AND d.nivelFormacao = :nivel")
    List<DiscenteEntity> findByProgramaAndNivel(
        @Param("programaId") Long programaId,
        @Param("nivel") NivelFormacao nivel
    );
}
