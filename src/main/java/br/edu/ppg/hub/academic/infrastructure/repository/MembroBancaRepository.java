package br.edu.ppg.hub.academic.infrastructure.repository;

import br.edu.ppg.hub.academic.domain.model.MembroBanca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade MembroBanca.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Repository
public interface MembroBancaRepository extends JpaRepository<MembroBanca, Long> {

    /**
     * Busca membros por banca.
     *
     * @param bancaId ID da banca
     * @return Lista de membros
     */
    List<MembroBanca> findByBancaId(Long bancaId);

    /**
     * Busca membros por banca ordenados por ordem de apresentação.
     *
     * @param bancaId ID da banca
     * @return Lista de membros ordenados
     */
    List<MembroBanca> findByBancaIdOrderByOrdemApresentacaoAsc(Long bancaId);

    /**
     * Busca membros por docente.
     *
     * @param docenteId ID do docente
     * @return Lista de membros
     */
    List<MembroBanca> findByDocenteId(Long docenteId);

    /**
     * Busca membros por docente (paginado).
     *
     * @param docenteId ID do docente
     * @param pageable Parâmetros de paginação
     * @return Página de membros
     */
    Page<MembroBanca> findByDocenteId(Long docenteId, Pageable pageable);

    /**
     * Busca membros por função.
     *
     * @param funcao Função do membro (Presidente, Examinador_Interno, etc.)
     * @return Lista de membros
     */
    List<MembroBanca> findByFuncao(String funcao);

    /**
     * Busca membros por tipo.
     *
     * @param tipo Tipo do membro (Interno ou Externo)
     * @return Lista de membros
     */
    List<MembroBanca> findByTipo(String tipo);

    /**
     * Busca membros de uma banca por função.
     *
     * @param bancaId ID da banca
     * @param funcao Função do membro
     * @return Lista de membros
     */
    List<MembroBanca> findByBancaIdAndFuncao(Long bancaId, String funcao);

    /**
     * Busca membros de uma banca por tipo.
     *
     * @param bancaId ID da banca
     * @param tipo Tipo do membro
     * @return Lista de membros
     */
    List<MembroBanca> findByBancaIdAndTipo(Long bancaId, String tipo);

    /**
     * Busca presidente de uma banca.
     *
     * @param bancaId ID da banca
     * @return Presidente da banca
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.funcao = 'Presidente'")
    Optional<MembroBanca> findPresidenteBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros externos de uma banca.
     *
     * @param bancaId ID da banca
     * @return Lista de membros externos
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.tipo = 'Externo'")
    List<MembroBanca> findMembrosExternosPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros internos de uma banca.
     *
     * @param bancaId ID da banca
     * @return Lista de membros internos
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.tipo = 'Interno'")
    List<MembroBanca> findMembrosInternosPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros que confirmaram presença.
     *
     * @param bancaId ID da banca
     * @return Lista de membros confirmados
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.confirmado = true")
    List<MembroBanca> findMembrosConfirmadosPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros que não confirmaram presença.
     *
     * @param bancaId ID da banca
     * @return Lista de membros não confirmados
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND (m.confirmado = false OR m.confirmado IS NULL)")
    List<MembroBanca> findMembrosNaoConfirmadosPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros presentes na banca.
     *
     * @param bancaId ID da banca
     * @return Lista de membros presentes
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.presente = true")
    List<MembroBanca> findMembrosPresentesPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros ausentes na banca.
     *
     * @param bancaId ID da banca
     * @return Lista de membros ausentes
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.presente = false")
    List<MembroBanca> findMembrosAusentesPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros que atribuíram nota.
     *
     * @param bancaId ID da banca
     * @return Lista de membros que atribuíram nota
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.notaIndividual IS NOT NULL")
    List<MembroBanca> findMembrosComNotaPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros que emitiram parecer.
     *
     * @param bancaId ID da banca
     * @return Lista de membros que emitiram parecer
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.parecerIndividual IS NOT NULL AND m.parecerIndividual != ''")
    List<MembroBanca> findMembrosComParecerPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros suplentes de uma banca.
     *
     * @param bancaId ID da banca
     * @return Lista de membros suplentes
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.funcao = 'Suplente'")
    List<MembroBanca> findSuplentesPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca membros titulares de uma banca (não suplentes).
     *
     * @param bancaId ID da banca
     * @return Lista de membros titulares
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.funcao != 'Suplente'")
    List<MembroBanca> findTitularesPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Conta membros de uma banca.
     *
     * @param bancaId ID da banca
     * @return Quantidade de membros
     */
    long countByBancaId(Long bancaId);

    /**
     * Conta membros externos de uma banca.
     *
     * @param bancaId ID da banca
     * @return Quantidade de membros externos
     */
    @Query("SELECT COUNT(m) FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.tipo = 'Externo'")
    long countMembrosExternosPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Conta membros internos de uma banca.
     *
     * @param bancaId ID da banca
     * @return Quantidade de membros internos
     */
    @Query("SELECT COUNT(m) FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.tipo = 'Interno'")
    long countMembrosInternosPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Conta membros titulares de uma banca (não suplentes).
     *
     * @param bancaId ID da banca
     * @return Quantidade de membros titulares
     */
    @Query("SELECT COUNT(m) FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.funcao != 'Suplente'")
    long countTitularesPorBanca(@Param("bancaId") Long bancaId);

    /**
     * Conta participações de um docente em bancas.
     *
     * @param docenteId ID do docente
     * @return Quantidade de participações
     */
    long countByDocenteId(Long docenteId);

    /**
     * Verifica se docente já é membro de uma banca.
     *
     * @param bancaId ID da banca
     * @param docenteId ID do docente
     * @return true se já é membro, false caso contrário
     */
    boolean existsByBancaIdAndDocenteId(Long bancaId, Long docenteId);

    /**
     * Verifica se já existe presidente definido para a banca.
     *
     * @param bancaId ID da banca
     * @return true se já existe presidente, false caso contrário
     */
    @Query("SELECT COUNT(m) > 0 FROM MembroBanca m WHERE m.banca.id = :bancaId " +
           "AND m.funcao = 'Presidente'")
    boolean existsPresidentePorBanca(@Param("bancaId") Long bancaId);

    /**
     * Busca todas as participações de um docente em bancas, ordenadas por data.
     *
     * @param docenteId ID do docente
     * @return Lista de participações ordenadas
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.docente.id = :docenteId " +
           "ORDER BY m.banca.dataAgendada DESC")
    List<MembroBanca> findParticipacoesPorDocente(@Param("docenteId") Long docenteId);

    /**
     * Busca bancas onde o docente foi presidente.
     *
     * @param docenteId ID do docente
     * @return Lista de bancas onde foi presidente
     */
    @Query("SELECT m FROM MembroBanca m WHERE m.docente.id = :docenteId " +
           "AND m.funcao = 'Presidente' " +
           "ORDER BY m.banca.dataAgendada DESC")
    List<MembroBanca> findBancasComoPresidente(@Param("docenteId") Long docenteId);
}
