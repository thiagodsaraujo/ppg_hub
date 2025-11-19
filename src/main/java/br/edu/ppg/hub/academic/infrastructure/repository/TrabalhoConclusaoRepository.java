package br.edu.ppg.hub.academic.infrastructure.repository;

import br.edu.ppg.hub.academic.domain.enums.StatusTrabalho;
import br.edu.ppg.hub.academic.domain.enums.TipoTrabalho;
import br.edu.ppg.hub.academic.domain.model.TrabalhoConclusao;
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
 * Repository para a entidade TrabalhoConclusao.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Repository
public interface TrabalhoConclusaoRepository extends JpaRepository<TrabalhoConclusao, Long> {

    /**
     * Busca trabalho por ID do discente.
     *
     * @param discenteId ID do discente
     * @return Trabalho encontrado
     */
    Optional<TrabalhoConclusao> findByDiscenteId(Long discenteId);

    /**
     * Busca trabalhos por ID do orientador.
     *
     * @param orientadorId ID do orientador
     * @return Lista de trabalhos
     */
    List<TrabalhoConclusao> findByOrientadorId(Long orientadorId);

    /**
     * Busca trabalhos por ID do orientador (paginado).
     *
     * @param orientadorId ID do orientador
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    Page<TrabalhoConclusao> findByOrientadorId(Long orientadorId, Pageable pageable);

    /**
     * Busca trabalhos por ID do coorientador.
     *
     * @param coorientadorId ID do coorientador
     * @return Lista de trabalhos
     */
    List<TrabalhoConclusao> findByCoorientadorId(Long coorientadorId);

    /**
     * Busca trabalhos por ID do coorientador (paginado).
     *
     * @param coorientadorId ID do coorientador
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    Page<TrabalhoConclusao> findByCoorientadorId(Long coorientadorId, Pageable pageable);

    /**
     * Busca trabalhos por tipo.
     *
     * @param tipo Tipo do trabalho
     * @return Lista de trabalhos
     */
    List<TrabalhoConclusao> findByTipo(TipoTrabalho tipo);

    /**
     * Busca trabalhos por tipo (paginado).
     *
     * @param tipo Tipo do trabalho
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    Page<TrabalhoConclusao> findByTipo(TipoTrabalho tipo, Pageable pageable);

    /**
     * Busca trabalhos por status.
     *
     * @param status Status do trabalho
     * @return Lista de trabalhos
     */
    List<TrabalhoConclusao> findByStatus(StatusTrabalho status);

    /**
     * Busca trabalhos por status (paginado).
     *
     * @param status Status do trabalho
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    Page<TrabalhoConclusao> findByStatus(StatusTrabalho status, Pageable pageable);

    /**
     * Busca trabalhos por tipo e status.
     *
     * @param tipo Tipo do trabalho
     * @param status Status do trabalho
     * @return Lista de trabalhos
     */
    List<TrabalhoConclusao> findByTipoAndStatus(TipoTrabalho tipo, StatusTrabalho status);

    /**
     * Busca trabalhos defendidos em um período.
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de trabalhos
     */
    List<TrabalhoConclusao> findByDataDefesaBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca trabalhos defendidos em um período (paginado).
     *
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos
     */
    Page<TrabalhoConclusao> findByDataDefesaBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    /**
     * Busca trabalhos por ano de defesa.
     *
     * @param anoDefesa Ano da defesa
     * @return Lista de trabalhos
     */
    List<TrabalhoConclusao> findByAnoDefesa(Integer anoDefesa);

    /**
     * Busca trabalhos por ano e semestre de defesa.
     *
     * @param anoDefesa Ano da defesa
     * @param semestreDefesa Semestre da defesa
     * @return Lista de trabalhos
     */
    List<TrabalhoConclusao> findByAnoDefesaAndSemestreDefesa(Integer anoDefesa, Integer semestreDefesa);

    /**
     * Busca trabalhos publicados no repositório.
     *
     * @return Lista de trabalhos publicados
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE t.status = 'PUBLICADO' " +
           "AND t.uriRepositorio IS NOT NULL")
    List<TrabalhoConclusao> findTrabalhosPublicados();

    /**
     * Busca trabalhos publicados no repositório (paginado).
     *
     * @param pageable Parâmetros de paginação
     * @return Página de trabalhos publicados
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE t.status = 'PUBLICADO' " +
           "AND t.uriRepositorio IS NOT NULL")
    Page<TrabalhoConclusao> findTrabalhosPublicados(Pageable pageable);

    /**
     * Busca trabalhos pendentes de defesa (qualificados).
     * Um trabalho está pendente de defesa se foi qualificado mas ainda não foi defendido.
     *
     * @return Lista de trabalhos pendentes de defesa
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE t.status = 'QUALIFICADO' " +
           "AND t.dataDefesa IS NULL")
    List<TrabalhoConclusao> findTrabalhosPendentesDefesa();

    /**
     * Busca trabalhos por orientador com status específico.
     *
     * @param orientadorId ID do orientador
     * @param status Status do trabalho
     * @return Lista de trabalhos
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE t.orientador.id = :orientadorId " +
           "AND t.status = :status")
    List<TrabalhoConclusao> findByOrientadorIdAndStatus(
        @Param("orientadorId") Long orientadorId,
        @Param("status") StatusTrabalho status
    );

    /**
     * Busca trabalhos em elaboração sem arquivo anexado.
     *
     * @return Lista de trabalhos
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE t.status = 'EM_PREPARACAO' " +
           "AND (t.arquivoPdf IS NULL OR t.arquivoPdf = '')")
    List<TrabalhoConclusao> findTrabalhosEmElaboracaoSemArquivo();

    /**
     * Busca trabalhos aprovados pendentes de publicação.
     *
     * @return Lista de trabalhos aprovados pendentes de publicação
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE t.status = 'APROVADO' " +
           "AND (t.uriRepositorio IS NULL OR t.uriRepositorio = '')")
    List<TrabalhoConclusao> findTrabalhosAprovadosPendentePublicacao();

    /**
     * Busca trabalhos por área CNPq.
     *
     * @param areaCnpq Área CNPq
     * @return Lista de trabalhos
     */
    List<TrabalhoConclusao> findByAreaCnpqContainingIgnoreCase(String areaCnpq);

    /**
     * Busca trabalhos por palavra-chave (em português).
     *
     * @param palavraChave Palavra-chave a buscar
     * @return Lista de trabalhos
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE " +
           "LOWER(t.palavrasChavePortugues) LIKE LOWER(CONCAT('%', :palavraChave, '%'))")
    List<TrabalhoConclusao> findByPalavraChave(@Param("palavraChave") String palavraChave);

    /**
     * Busca trabalhos por texto no título ou resumo.
     *
     * @param texto Texto a buscar
     * @return Lista de trabalhos
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE " +
           "LOWER(t.tituloPortugues) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(t.tituloIngles) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(t.resumoPortugues) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<TrabalhoConclusao> findByTextoTituloOuResumo(@Param("texto") String texto);

    /**
     * Busca trabalhos por DOI.
     *
     * @param doi DOI do trabalho
     * @return Trabalho encontrado
     */
    Optional<TrabalhoConclusao> findByDoi(String doi);

    /**
     * Busca trabalhos mais citados.
     *
     * @param limite Número máximo de resultados
     * @return Lista de trabalhos mais citados
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE t.citacoesCount > 0 " +
           "ORDER BY t.citacoesCount DESC")
    List<TrabalhoConclusao> findTrabalhosMaisCitados(Pageable pageable);

    /**
     * Busca trabalhos mais baixados.
     *
     * @param limite Número máximo de resultados
     * @return Lista de trabalhos mais baixados
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE t.downloadsCount > 0 " +
           "ORDER BY t.downloadsCount DESC")
    List<TrabalhoConclusao> findTrabalhosMaisBaixados(Pageable pageable);

    /**
     * Busca trabalhos premiados.
     *
     * @return Lista de trabalhos premiados
     */
    @Query("SELECT t FROM TrabalhoConclusao t WHERE t.premiosReconhecimentos IS NOT NULL " +
           "AND t.premiosReconhecimentos != ''")
    List<TrabalhoConclusao> findTrabalhosPremiadps();

    /**
     * Conta trabalhos por tipo.
     *
     * @param tipo Tipo do trabalho
     * @return Quantidade de trabalhos
     */
    long countByTipo(TipoTrabalho tipo);

    /**
     * Conta trabalhos por status.
     *
     * @param status Status do trabalho
     * @return Quantidade de trabalhos
     */
    long countByStatus(StatusTrabalho status);

    /**
     * Conta trabalhos por orientador.
     *
     * @param orientadorId ID do orientador
     * @return Quantidade de trabalhos
     */
    long countByOrientadorId(Long orientadorId);

    /**
     * Conta trabalhos defendidos em um ano.
     *
     * @param anoDefesa Ano da defesa
     * @return Quantidade de trabalhos
     */
    long countByAnoDefesa(Integer anoDefesa);

    /**
     * Verifica se já existe trabalho para o discente.
     *
     * @param discenteId ID do discente
     * @return true se existe, false caso contrário
     */
    boolean existsByDiscenteId(Long discenteId);
}
