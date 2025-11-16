package com.ppghub.infrastructure.persistence.repository;

import com.ppghub.infrastructure.persistence.entity.ProgramaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para Programa.
 */
@Repository
public interface JpaProgramaRepository extends JpaRepository<ProgramaEntity, Long> {

    /**
     * Busca programa por código CAPES
     */
    Optional<ProgramaEntity> findByCodigoCapes(String codigoCapes);

    /**
     * Busca programas por instituição
     */
    List<ProgramaEntity> findByInstituicaoId(Long instituicaoId);

    /**
     * Busca programas por área de conhecimento
     */
    List<ProgramaEntity> findByAreaConhecimento(String areaConhecimento);

    /**
     * Busca programas por status
     */
    List<ProgramaEntity> findByStatus(String status);

    /**
     * Busca programas com conceito CAPES mínimo
     */
    @Query("SELECT p FROM ProgramaEntity p WHERE p.conceitoCapes >= :conceito ORDER BY p.conceitoCapes DESC")
    List<ProgramaEntity> findByConceitoMinimoCapes(@Param("conceito") Integer conceito);

    /**
     * Busca programas ativos por instituição
     */
    @Query("SELECT p FROM ProgramaEntity p WHERE p.instituicao.id = :instituicaoId AND p.status = 'ATIVO'")
    List<ProgramaEntity> findProgramasAtivosByInstituicao(@Param("instituicaoId") Long instituicaoId);
}
