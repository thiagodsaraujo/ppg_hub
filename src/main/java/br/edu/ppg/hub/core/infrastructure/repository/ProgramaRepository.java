package br.edu.ppg.hub.core.infrastructure.repository;

import br.edu.ppg.hub.core.domain.enums.StatusPrograma;
import br.edu.ppg.hub.core.domain.model.Programa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Programa.
 */
@Repository
public interface ProgramaRepository extends JpaRepository<Programa, Long> {

    /**
     * Busca programa por código CAPES.
     */
    Optional<Programa> findByCodigoCapes(String codigoCapes);

    /**
     * Busca programas por instituição.
     */
    Page<Programa> findByInstituicaoId(Long instituicaoId, Pageable pageable);

    /**
     * Busca programas ativos por instituição.
     */
    Page<Programa> findByInstituicaoIdAndStatus(Long instituicaoId, StatusPrograma status, Pageable pageable);

    /**
     * Busca programas por status.
     */
    Page<Programa> findByStatus(StatusPrograma status, Pageable pageable);

    /**
     * Busca programas por coordenador.
     */
    List<Programa> findByCoordenadorId(Long coordenadorId);

    /**
     * Busca programa por instituição e sigla.
     */
    Optional<Programa> findByInstituicaoIdAndSigla(Long instituicaoId, String sigla);

    /**
     * Verifica se existe programa com mesma sigla na instituição.
     */
    boolean existsByInstituicaoIdAndSigla(Long instituicaoId, String sigla);

    /**
     * Verifica se existe programa com código CAPES.
     */
    boolean existsByCodigoCapes(String codigoCapes);

    /**
     * Busca programas por nome (busca parcial, case insensitive).
     */
    @Query("SELECT p FROM Programa p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Programa> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    /**
     * Busca programas por área de concentração.
     */
    @Query("SELECT p FROM Programa p WHERE LOWER(p.areaConcentracao) LIKE LOWER(CONCAT('%', :area, '%'))")
    Page<Programa> findByAreaConcentracaoContainingIgnoreCase(@Param("area") String area, Pageable pageable);

    /**
     * Conta programas por instituição.
     */
    long countByInstituicaoId(Long instituicaoId);

    /**
     * Conta programas ativos por instituição.
     */
    long countByInstituicaoIdAndStatus(Long instituicaoId, StatusPrograma status);
}
