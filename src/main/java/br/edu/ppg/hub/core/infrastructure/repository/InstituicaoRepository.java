package br.edu.ppg.hub.core.infrastructure.repository;

import br.edu.ppg.hub.core.domain.model.Instituicao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Instituição.
 *
 * Utiliza Spring Data JPA para fornecer operações CRUD e consultas customizadas.
 */
@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

    /**
     * Busca instituição por código único
     */
    Optional<Instituicao> findByCodigo(String codigo);

    /**
     * Busca instituição por CNPJ
     */
    Optional<Instituicao> findByCnpj(String cnpj);

    /**
     * Verifica se existe instituição com o código
     */
    boolean existsByCodigo(String codigo);

    /**
     * Verifica se existe instituição com o CNPJ
     */
    boolean existsByCnpj(String cnpj);

    /**
     * Verifica se existe instituição com o código, excluindo um ID específico
     * Útil para validação em updates
     */
    boolean existsByCodigoAndIdNot(String codigo, Long id);

    /**
     * Verifica se existe instituição com o CNPJ, excluindo um ID específico
     * Útil para validação em updates
     */
    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    /**
     * Lista instituições ativas com paginação
     */
    Page<Instituicao> findByAtivoTrue(Pageable pageable);

    /**
     * Lista instituições por tipo
     */
    List<Instituicao> findByTipoAndAtivoTrue(String tipo);

    /**
     * Conta total de instituições ativas
     */
    long countByAtivoTrue();

    /**
     * Busca instituições por termo livre (busca em múltiplos campos)
     * Busca no código, nome completo, nome abreviado e sigla
     */
    @Query("SELECT i FROM Instituicao i WHERE " +
           "LOWER(i.codigo) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(i.nomeCompleto) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(i.nomeAbreviado) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(i.sigla) LIKE LOWER(CONCAT('%', :termo, '%'))")
    Page<Instituicao> searchByTermo(@Param("termo") String termo, Pageable pageable);

    /**
     * Busca instituições ativas por termo livre
     */
    @Query("SELECT i FROM Instituicao i WHERE i.ativo = true AND (" +
           "LOWER(i.codigo) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(i.nomeCompleto) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(i.nomeAbreviado) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(i.sigla) LIKE LOWER(CONCAT('%', :termo, '%')))")
    Page<Instituicao> searchAtivasByTermo(@Param("termo") String termo, Pageable pageable);

    /**
     * Retorna estatísticas de instituições agrupadas por tipo
     */
    @Query("SELECT i.tipo, COUNT(i) FROM Instituicao i WHERE i.ativo = true GROUP BY i.tipo ORDER BY i.tipo")
    List<Object[]> getEstatisticasPorTipo();

    /**
     * Lista instituições ordenadas por nome abreviado
     */
    List<Instituicao> findAllByOrderByNomeAbreviadoAsc();

    /**
     * Lista instituições ativas ordenadas por nome abreviado
     */
    List<Instituicao> findByAtivoTrueOrderByNomeAbreviadoAsc();
}
