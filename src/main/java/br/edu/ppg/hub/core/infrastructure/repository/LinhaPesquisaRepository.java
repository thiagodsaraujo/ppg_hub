package br.edu.ppg.hub.core.infrastructure.repository;

import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para LinhaPesquisa.
 */
@Repository
public interface LinhaPesquisaRepository extends JpaRepository<LinhaPesquisa, Long> {

    /**
     * Busca linhas de pesquisa por programa.
     */
    Page<LinhaPesquisa> findByProgramaId(Long programaId, Pageable pageable);

    /**
     * Busca linhas de pesquisa ativas por programa.
     */
    List<LinhaPesquisa> findByProgramaIdAndAtivaTrue(Long programaId);

    /**
     * Busca linhas de pesquisa por coordenador.
     */
    List<LinhaPesquisa> findByCoordenadorId(Long coordenadorId);

    /**
     * Busca linha de pesquisa por programa e nome.
     */
    Optional<LinhaPesquisa> findByProgramaIdAndNome(Long programaId, String nome);

    /**
     * Verifica se existe linha de pesquisa com mesmo nome no programa.
     */
    boolean existsByProgramaIdAndNome(Long programaId, String nome);

    /**
     * Busca linhas por nome (busca parcial, case insensitive).
     */
    @Query("SELECT l FROM LinhaPesquisa l WHERE LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<LinhaPesquisa> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    /**
     * Busca linhas por palavras-chave.
     */
    @Query("SELECT l FROM LinhaPesquisa l WHERE LOWER(l.palavrasChave) LIKE LOWER(CONCAT('%', :palavra, '%'))")
    Page<LinhaPesquisa> findByPalavrasChaveContainingIgnoreCase(@Param("palavra") String palavra, Pageable pageable);

    /**
     * Conta linhas de pesquisa por programa.
     */
    long countByProgramaId(Long programaId);

    /**
     * Conta linhas ativas por programa.
     */
    long countByProgramaIdAndAtivaTrue(Long programaId);
}
