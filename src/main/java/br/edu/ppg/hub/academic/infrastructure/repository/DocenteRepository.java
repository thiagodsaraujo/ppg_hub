package br.edu.ppg.hub.academic.infrastructure.repository;

import br.edu.ppg.hub.academic.domain.enums.StatusDocente;
import br.edu.ppg.hub.academic.domain.enums.TipoVinculoDocente;
import br.edu.ppg.hub.academic.domain.model.Docente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Docente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {

    /**
     * Busca docente por ID do usuário
     */
    Optional<Docente> findByUsuarioId(Long usuarioId);

    /**
     * Busca docente por ID do usuário e ID do programa
     */
    Optional<Docente> findByUsuarioIdAndProgramaId(Long usuarioId, Long programaId);

    /**
     * Busca todos os docentes de um programa
     */
    List<Docente> findByProgramaId(Long programaId);

    /**
     * Busca todos os docentes de um programa (paginado)
     */
    Page<Docente> findByProgramaId(Long programaId, Pageable pageable);

    /**
     * Busca docentes de um programa por status
     */
    List<Docente> findByProgramaIdAndStatus(Long programaId, StatusDocente status);

    /**
     * Busca docentes de um programa por status (paginado)
     */
    Page<Docente> findByProgramaIdAndStatus(Long programaId, StatusDocente status, Pageable pageable);

    /**
     * Busca docentes por tipo de vínculo
     */
    List<Docente> findByTipoVinculo(TipoVinculoDocente tipoVinculo);

    /**
     * Busca docentes por tipo de vínculo (paginado)
     */
    Page<Docente> findByTipoVinculo(TipoVinculoDocente tipoVinculo, Pageable pageable);

    /**
     * Busca docentes de um programa por tipo de vínculo
     */
    List<Docente> findByProgramaIdAndTipoVinculo(Long programaId, TipoVinculoDocente tipoVinculo);

    /**
     * Busca docentes de uma linha de pesquisa
     */
    List<Docente> findByLinhaPesquisaId(Long linhaPesquisaId);

    /**
     * Busca docentes de uma linha de pesquisa (paginado)
     */
    Page<Docente> findByLinhaPesquisaId(Long linhaPesquisaId, Pageable pageable);

    /**
     * Busca docentes permanentes de um programa
     */
    @Query("SELECT d FROM Docente d WHERE d.programa.id = :programaId AND d.tipoVinculo = 'PERMANENTE' AND d.status = 'ATIVO'")
    List<Docente> findDocentesPermanentesAtivos(@Param("programaId") Long programaId);

    /**
     * Busca docentes com orientações disponíveis (menos de 8 orientações ativas)
     */
    @Query("SELECT d FROM Docente d WHERE d.programa.id = :programaId " +
           "AND d.status = 'ATIVO' " +
           "AND (d.orientacoesMestradoAndamento + d.orientacoesDoutoradoAndamento) < :limiteOrientacoes " +
           "ORDER BY (d.orientacoesMestradoAndamento + d.orientacoesDoutoradoAndamento) ASC")
    List<Docente> findDocentesComOrientacoesDisponiveis(
        @Param("programaId") Long programaId,
        @Param("limiteOrientacoes") int limiteOrientacoes
    );

    /**
     * Busca docentes bolsistas de produtividade de um programa
     */
    @Query("SELECT d FROM Docente d WHERE d.programa.id = :programaId " +
           "AND d.bolsistaProdutividade = true " +
           "AND (d.vigenciaBolsaFim IS NULL OR d.vigenciaBolsaFim >= CURRENT_DATE)")
    List<Docente> findBolsistasProdutividade(@Param("programaId") Long programaId);

    /**
     * Conta docentes por programa
     */
    long countByProgramaId(Long programaId);

    /**
     * Conta docentes por programa e status
     */
    long countByProgramaIdAndStatus(Long programaId, StatusDocente status);

    /**
     * Conta docentes por programa e tipo de vínculo
     */
    long countByProgramaIdAndTipoVinculo(Long programaId, TipoVinculoDocente tipoVinculo);

    /**
     * Verifica se já existe docente com o mesmo usuário e programa
     */
    boolean existsByUsuarioIdAndProgramaId(Long usuarioId, Long programaId);

    /**
     * Busca docentes com mais orientações concluídas
     */
    @Query("SELECT d FROM Docente d WHERE d.programa.id = :programaId " +
           "ORDER BY (d.orientacoesMestradoConcluidas + d.orientacoesDoutoradoConcluidas) DESC")
    Page<Docente> findDocentesMaisProdutivos(@Param("programaId") Long programaId, Pageable pageable);
}
