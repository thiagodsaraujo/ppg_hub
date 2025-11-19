package br.edu.ppg.hub.academic.infrastructure.repository;

import br.edu.ppg.hub.academic.domain.enums.ResultadoBanca;
import br.edu.ppg.hub.academic.domain.enums.TipoBanca;
import br.edu.ppg.hub.academic.domain.model.Banca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository para a entidade Banca.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Repository
public interface BancaRepository extends JpaRepository<Banca, Long> {

    /**
     * Busca bancas por trabalho de conclusão.
     *
     * @param trabalhoConclusaoId ID do trabalho de conclusão
     * @return Lista de bancas
     */
    List<Banca> findByTrabalhoConclusaoId(Long trabalhoConclusaoId);

    /**
     * Busca bancas por discente.
     *
     * @param discenteId ID do discente
     * @return Lista de bancas
     */
    List<Banca> findByDiscenteId(Long discenteId);

    /**
     * Busca bancas por discente (paginado).
     *
     * @param discenteId ID do discente
     * @param pageable Parâmetros de paginação
     * @return Página de bancas
     */
    Page<Banca> findByDiscenteId(Long discenteId, Pageable pageable);

    /**
     * Busca bancas por presidente.
     *
     * @param presidenteId ID do presidente
     * @return Lista de bancas
     */
    List<Banca> findByPresidenteId(Long presidenteId);

    /**
     * Busca bancas por presidente (paginado).
     *
     * @param presidenteId ID do presidente
     * @param pageable Parâmetros de paginação
     * @return Página de bancas
     */
    Page<Banca> findByPresidenteId(Long presidenteId, Pageable pageable);

    /**
     * Busca bancas por tipo.
     *
     * @param tipo Tipo da banca
     * @return Lista de bancas
     */
    List<Banca> findByTipo(TipoBanca tipo);

    /**
     * Busca bancas por tipo (paginado).
     *
     * @param tipo Tipo da banca
     * @param pageable Parâmetros de paginação
     * @return Página de bancas
     */
    Page<Banca> findByTipo(TipoBanca tipo, Pageable pageable);

    /**
     * Busca bancas por status.
     *
     * @param status Status da banca
     * @return Lista de bancas
     */
    List<Banca> findByStatus(String status);

    /**
     * Busca bancas por status (paginado).
     *
     * @param status Status da banca
     * @param pageable Parâmetros de paginação
     * @return Página de bancas
     */
    Page<Banca> findByStatus(String status, Pageable pageable);

    /**
     * Busca bancas por resultado.
     *
     * @param resultado Resultado da banca
     * @return Lista de bancas
     */
    List<Banca> findByResultado(ResultadoBanca resultado);

    /**
     * Busca bancas por resultado (paginado).
     *
     * @param resultado Resultado da banca
     * @param pageable Parâmetros de paginação
     * @return Página de bancas
     */
    Page<Banca> findByResultado(ResultadoBanca resultado, Pageable pageable);

    /**
     * Busca bancas realizadas em um período.
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de bancas
     */
    List<Banca> findByDataRealizacaoBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca bancas realizadas em um período (paginado).
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @param pageable Parâmetros de paginação
     * @return Página de bancas
     */
    Page<Banca> findByDataRealizacaoBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    /**
     * Busca bancas agendadas em um período.
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de bancas
     */
    List<Banca> findByDataAgendadaBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca bancas agendadas em um período (paginado).
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @param pageable Parâmetros de paginação
     * @return Página de bancas
     */
    Page<Banca> findByDataAgendadaBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    /**
     * Busca bancas agendadas (futuras, sem resultado).
     *
     * @return Lista de bancas agendadas
     */
    @Query("SELECT b FROM Banca b WHERE b.status = 'Agendada' " +
           "AND b.dataAgendada >= CURRENT_DATE " +
           "AND b.resultado IS NULL " +
           "ORDER BY b.dataAgendada ASC, b.horarioInicio ASC")
    List<Banca> findBancasAgendadas();

    /**
     * Busca bancas agendadas (paginado).
     *
     * @param pageable Parâmetros de paginação
     * @return Página de bancas agendadas
     */
    @Query("SELECT b FROM Banca b WHERE b.status = 'Agendada' " +
           "AND b.dataAgendada >= CURRENT_DATE " +
           "AND b.resultado IS NULL " +
           "ORDER BY b.dataAgendada ASC, b.horarioInicio ASC")
    Page<Banca> findBancasAgendadas(Pageable pageable);

    /**
     * Busca bancas agendadas para hoje.
     *
     * @return Lista de bancas de hoje
     */
    @Query("SELECT b FROM Banca b WHERE b.status = 'Agendada' " +
           "AND b.dataAgendada = CURRENT_DATE " +
           "ORDER BY b.horarioInicio ASC")
    List<Banca> findBancasHoje();

    /**
     * Busca bancas agendadas para os próximos dias.
     *
     * @param dias Número de dias à frente
     * @return Lista de bancas
     */
    @Query("SELECT b FROM Banca b WHERE b.status = 'Agendada' " +
           "AND b.dataAgendada BETWEEN CURRENT_DATE AND CURRENT_DATE + :dias " +
           "ORDER BY b.dataAgendada ASC, b.horarioInicio ASC")
    List<Banca> findBancasProximosDias(@Param("dias") int dias);

    /**
     * Busca bancas realizadas (com resultado).
     *
     * @return Lista de bancas realizadas
     */
    @Query("SELECT b FROM Banca b WHERE b.status = 'Realizada' " +
           "AND b.resultado IS NOT NULL " +
           "ORDER BY b.dataRealizacao DESC")
    List<Banca> findBancasRealizadas();

    /**
     * Busca bancas realizadas (paginado).
     *
     * @param pageable Parâmetros de paginação
     * @return Página de bancas realizadas
     */
    @Query("SELECT b FROM Banca b WHERE b.status = 'Realizada' " +
           "AND b.resultado IS NOT NULL " +
           "ORDER BY b.dataRealizacao DESC")
    Page<Banca> findBancasRealizadas(Pageable pageable);

    /**
     * Busca bancas por tipo e status.
     *
     * @param tipo Tipo da banca
     * @param status Status da banca
     * @return Lista de bancas
     */
    List<Banca> findByTipoAndStatus(TipoBanca tipo, String status);

    /**
     * Busca bancas por modalidade.
     *
     * @param modalidade Modalidade da banca (Presencial, Virtual, Híbrida)
     * @return Lista de bancas
     */
    List<Banca> findByModalidade(String modalidade);

    /**
     * Busca bancas virtuais agendadas.
     *
     * @return Lista de bancas virtuais
     */
    @Query("SELECT b FROM Banca b WHERE b.status = 'Agendada' " +
           "AND b.modalidade IN ('Virtual', 'Híbrida') " +
           "AND b.dataAgendada >= CURRENT_DATE " +
           "ORDER BY b.dataAgendada ASC")
    List<Banca> findBancasVirtuaisAgendadas();

    /**
     * Busca bancas sem ata registrada.
     *
     * @return Lista de bancas sem ata
     */
    @Query("SELECT b FROM Banca b WHERE b.status = 'Realizada' " +
           "AND (b.ataArquivo IS NULL OR b.ataArquivo = '') " +
           "ORDER BY b.dataRealizacao DESC")
    List<Banca> findBancasSemAta();

    /**
     * Busca bancas que exigem correções.
     *
     * @return Lista de bancas com correções exigidas
     */
    @Query("SELECT b FROM Banca b WHERE b.resultado = 'APROVADO_COM_RESTRICOES' " +
           "AND b.correcoesExigidas IS NOT NULL")
    List<Banca> findBancasComCorrecoesExigidas();

    /**
     * Busca bancas aprovadas com distinção.
     *
     * @return Lista de bancas aprovadas com distinção
     */
    @Query("SELECT b FROM Banca b WHERE b.resultado = 'APROVADO_COM_DISTINCAO' " +
           "ORDER BY b.dataRealizacao DESC")
    List<Banca> findBancasAprovadasComDistincao();

    /**
     * Busca bancas por discente e tipo.
     *
     * @param discenteId ID do discente
     * @param tipo Tipo da banca
     * @return Lista de bancas
     */
    List<Banca> findByDiscenteIdAndTipo(Long discenteId, TipoBanca tipo);

    /**
     * Conta bancas por tipo.
     *
     * @param tipo Tipo da banca
     * @return Quantidade de bancas
     */
    long countByTipo(TipoBanca tipo);

    /**
     * Conta bancas por status.
     *
     * @param status Status da banca
     * @return Quantidade de bancas
     */
    long countByStatus(String status);

    /**
     * Conta bancas por resultado.
     *
     * @param resultado Resultado da banca
     * @return Quantidade de bancas
     */
    long countByResultado(ResultadoBanca resultado);

    /**
     * Conta bancas por presidente.
     *
     * @param presidenteId ID do presidente
     * @return Quantidade de bancas
     */
    long countByPresidenteId(Long presidenteId);

    /**
     * Verifica se discente já tem banca agendada de determinado tipo.
     *
     * @param discenteId ID do discente
     * @param tipo Tipo da banca
     * @return true se existe, false caso contrário
     */
    boolean existsByDiscenteIdAndTipoAndStatus(Long discenteId, TipoBanca tipo, String status);
}
