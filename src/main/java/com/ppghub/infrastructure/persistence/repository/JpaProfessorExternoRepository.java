package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.ProfessorExternoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para ProfessorExterno.
 */
@Repository
public interface JpaProfessorExternoRepository extends JpaRepository<ProfessorExternoEntity, Long> {

    /**
     * Busca professor externo por email
     */
    Optional<ProfessorExternoEntity> findByEmail(String email);

    /**
     * Busca professor externo por ORCID
     */
    Optional<ProfessorExternoEntity> findByOrcid(String orcid);

    /**
     * Busca professor externo por Lattes ID
     */
    Optional<ProfessorExternoEntity> findByLattesId(String lattesId);

    /**
     * Busca professor externo por OpenAlex Author ID
     */
    Optional<ProfessorExternoEntity> findByOpenalexAuthorId(String openalexAuthorId);

    /**
     * Busca professores externos por instituição de origem
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE LOWER(p.instituicaoOrigem) LIKE LOWER(CONCAT('%', :instituicao, '%'))")
    List<ProfessorExternoEntity> findByInstituicaoOrigem(@Param("instituicao") String instituicao);

    /**
     * Busca professores externos por nome (busca parcial, case insensitive)
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<ProfessorExternoEntity> searchByNome(@Param("nome") String nome, Pageable pageable);

    /**
     * Busca professores externos ativos
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE p.ativo = true ORDER BY p.nome")
    List<ProfessorExternoEntity> findProfessoresAtivos();

    /**
     * Busca professores externos validados
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE p.validado = true")
    List<ProfessorExternoEntity> findProfessoresValidados();

    /**
     * Busca professores externos não validados
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE p.validado = false AND p.ativo = true")
    List<ProfessorExternoEntity> findProfessoresNaoValidados();

    /**
     * Busca professores externos com identificador acadêmico
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE " +
           "p.lattesId IS NOT NULL OR p.orcid IS NOT NULL OR p.openalexAuthorId IS NOT NULL")
    List<ProfessorExternoEntity> findProfessoresComIdentificador();

    /**
     * Busca professores externos sem identificador acadêmico
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE " +
           "p.lattesId IS NULL AND p.orcid IS NULL AND p.openalexAuthorId IS NULL AND p.ativo = true")
    List<ProfessorExternoEntity> findProfessoresSemIdentificador();

    /**
     * Busca professores externos que precisam de sincronização com OpenAlex
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE p.openalexAuthorId IS NOT NULL " +
           "AND (p.openalexLastSyncAt IS NULL OR p.openalexLastSyncAt < :thresholdDate)")
    List<ProfessorExternoEntity> findProfessoresNeedingSync(@Param("thresholdDate") java.time.LocalDateTime thresholdDate);

    /**
     * Busca professores externos por especialidade
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE LOWER(p.especialidade) LIKE LOWER(CONCAT('%', :especialidade, '%'))")
    List<ProfessorExternoEntity> findByEspecialidade(@Param("especialidade") String especialidade);

    /**
     * Busca professores externos por titulação
     */
    List<ProfessorExternoEntity> findByTitulacao(String titulacao);

    /**
     * Busca professores externos com mais participações em bancas
     */
    @Query("SELECT p, SIZE(p.participacoesBancas) as participacoes " +
           "FROM ProfessorExternoEntity p " +
           "WHERE p.ativo = true " +
           "ORDER BY SIZE(p.participacoesBancas) DESC")
    Page<ProfessorExternoEntity> findProfessoresMaisParticipacoes(Pageable pageable);

    /**
     * Busca professores externos ou por identificador ou por email
     */
    @Query("SELECT p FROM ProfessorExternoEntity p WHERE " +
           "p.email = :email OR p.orcid = :orcid OR p.lattesId = :lattesId OR p.openalexAuthorId = :openalexId")
    Optional<ProfessorExternoEntity> findByEmailOrIdentificadores(
        @Param("email") String email,
        @Param("orcid") String orcid,
        @Param("lattesId") String lattesId,
        @Param("openalexId") String openalexId
    );

    /**
     * Conta professores externos por instituição
     */
    @Query("SELECT p.instituicaoOrigem, COUNT(p) FROM ProfessorExternoEntity p " +
           "WHERE p.instituicaoOrigem IS NOT NULL " +
           "GROUP BY p.instituicaoOrigem " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> countByInstituicao();
}
