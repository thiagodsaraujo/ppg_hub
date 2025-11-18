package br.edu.ppg.hub.academic.application.dto.discente;

import br.edu.ppg.hub.academic.domain.model.Discente;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import br.edu.ppg.hub.core.domain.model.Programa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Discente e DTOs
 *
 * @author PPG Hub
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DiscenteMapper {

    /**
     * Converte CreateDTO para entidade Discente
     */
    public Discente toEntity(
            DiscenteCreateDTO dto,
            Usuario usuario,
            Programa programa,
            LinhaPesquisa linhaPesquisa,
            Docente orientador
    ) {
        return Discente.builder()
                .usuario(usuario)
                .programa(programa)
                .linhaPesquisa(linhaPesquisa)
                .orientador(orientador)
                .numeroMatricula(dto.getNumeroMatricula())
                .tipoCurso(dto.getTipoCurso())
                .turma(dto.getTurma())
                .semestreIngresso(dto.getSemestreIngresso())
                .dataIngresso(dto.getDataIngresso())
                .tituloProjeto(dto.getTituloProjeto())
                .resumoProjeto(dto.getResumoProjeto())
                .palavrasChaveProjeto(dto.getPalavrasChaveProjeto())
                .areaCnpq(dto.getAreaCnpq())
                .tipoIngresso(dto.getTipoIngresso())
                .notaProcessoSeletivo(dto.getNotaProcessoSeletivo())
                .classificacaoProcesso(dto.getClassificacaoProcesso())
                .coorientadorExternoNome(dto.getCoorientadorExternoNome())
                .coorientadorExternoInstituicao(dto.getCoorientadorExternoInstituicao())
                .coorientadorExternoEmail(dto.getCoorientadorExternoEmail())
                .coorientadorExternoTitulacao(dto.getCoorientadorExternoTitulacao())
                .bolsista(dto.getBolsista())
                .tipoBolsa(dto.getTipoBolsa())
                .modalidadeBolsa(dto.getModalidadeBolsa())
                .valorBolsa(dto.getValorBolsa())
                .dataInicioBolsa(dto.getDataInicioBolsa())
                .dataFimBolsa(dto.getDataFimBolsa())
                .numeroProcessoBolsa(dto.getNumeroProcessoBolsa())
                .agenciaFomento(dto.getAgenciaFomento())
                .creditosNecessarios(dto.getCreditosNecessarios())
                .prazoOriginal(dto.getPrazoOriginal())
                .build();
    }

    /**
     * Atualiza uma entidade existente com dados do UpdateDTO
     */
    public void updateEntity(
            Discente discente,
            DiscenteUpdateDTO dto,
            LinhaPesquisa linhaPesquisa,
            Docente orientador,
            Docente coorientadorInterno
    ) {
        if (dto.getLinhaPesquisaId() != null) {
            discente.setLinhaPesquisa(linhaPesquisa);
        }
        if (dto.getOrientadorId() != null) {
            discente.setOrientador(orientador);
        }
        if (dto.getCoorientadorInternoId() != null) {
            discente.setCoorientadorInterno(coorientadorInterno);
        }
        if (dto.getDataPrimeiraMatricula() != null) {
            discente.setDataPrimeiraMatricula(dto.getDataPrimeiraMatricula());
        }
        if (dto.getTituloProjeto() != null) {
            discente.setTituloProjeto(dto.getTituloProjeto());
        }
        if (dto.getResumoProjeto() != null) {
            discente.setResumoProjeto(dto.getResumoProjeto());
        }
        if (dto.getPalavrasChaveProjeto() != null) {
            discente.setPalavrasChaveProjeto(dto.getPalavrasChaveProjeto());
        }
        if (dto.getAreaCnpq() != null) {
            discente.setAreaCnpq(dto.getAreaCnpq());
        }
        if (dto.getCoorientadorExternoNome() != null) {
            discente.setCoorientadorExternoNome(dto.getCoorientadorExternoNome());
        }
        if (dto.getCoorientadorExternoInstituicao() != null) {
            discente.setCoorientadorExternoInstituicao(dto.getCoorientadorExternoInstituicao());
        }
        if (dto.getCoorientadorExternoEmail() != null) {
            discente.setCoorientadorExternoEmail(dto.getCoorientadorExternoEmail());
        }
        if (dto.getCoorientadorExternoTitulacao() != null) {
            discente.setCoorientadorExternoTitulacao(dto.getCoorientadorExternoTitulacao());
        }
        if (dto.getProficienciaIdioma() != null) {
            discente.setProficienciaIdioma(dto.getProficienciaIdioma());
        }
        if (dto.getProficienciaStatus() != null) {
            discente.setProficienciaStatus(dto.getProficienciaStatus());
        }
        if (dto.getDataProficiencia() != null) {
            discente.setDataProficiencia(dto.getDataProficiencia());
        }
        if (dto.getArquivoProficiencia() != null) {
            discente.setArquivoProficiencia(dto.getArquivoProficiencia());
        }
        if (dto.getBolsista() != null) {
            discente.setBolsista(dto.getBolsista());
        }
        if (dto.getTipoBolsa() != null) {
            discente.setTipoBolsa(dto.getTipoBolsa());
        }
        if (dto.getModalidadeBolsa() != null) {
            discente.setModalidadeBolsa(dto.getModalidadeBolsa());
        }
        if (dto.getValorBolsa() != null) {
            discente.setValorBolsa(dto.getValorBolsa());
        }
        if (dto.getDataInicioBolsa() != null) {
            discente.setDataInicioBolsa(dto.getDataInicioBolsa());
        }
        if (dto.getDataFimBolsa() != null) {
            discente.setDataFimBolsa(dto.getDataFimBolsa());
        }
        if (dto.getNumeroProcessoBolsa() != null) {
            discente.setNumeroProcessoBolsa(dto.getNumeroProcessoBolsa());
        }
        if (dto.getAgenciaFomento() != null) {
            discente.setAgenciaFomento(dto.getAgenciaFomento());
        }
        if (dto.getCreditosNecessarios() != null) {
            discente.setCreditosNecessarios(dto.getCreditosNecessarios());
        }
        if (dto.getCoeficienteRendimento() != null) {
            discente.setCoeficienteRendimento(dto.getCoeficienteRendimento());
        }
        if (dto.getQualificacaoRealizada() != null) {
            discente.setQualificacaoRealizada(dto.getQualificacaoRealizada());
        }
        if (dto.getDataQualificacao() != null) {
            discente.setDataQualificacao(dto.getDataQualificacao());
        }
        if (dto.getResultadoQualificacao() != null) {
            discente.setResultadoQualificacao(dto.getResultadoQualificacao());
        }
        if (dto.getPrazoOriginal() != null) {
            discente.setPrazoOriginal(dto.getPrazoOriginal());
        }
        if (dto.getProrrogacoes() != null) {
            discente.setProrrogacoes(dto.getProrrogacoes());
        }
        if (dto.getDataLimiteAtual() != null) {
            discente.setDataLimiteAtual(dto.getDataLimiteAtual());
        }
        if (dto.getDataDefesa() != null) {
            discente.setDataDefesa(dto.getDataDefesa());
        }
        if (dto.getResultadoDefesa() != null) {
            discente.setResultadoDefesa(dto.getResultadoDefesa());
        }
        if (dto.getNotaDefesa() != null) {
            discente.setNotaDefesa(dto.getNotaDefesa());
        }
        if (dto.getTituloFinal() != null) {
            discente.setTituloFinal(dto.getTituloFinal());
        }
        if (dto.getDocumentos() != null) {
            discente.setDocumentos(dto.getDocumentos());
        }
        if (dto.getStatus() != null) {
            discente.setStatus(dto.getStatus());
        }
        if (dto.getMotivoDesligamento() != null) {
            discente.setMotivoDesligamento(dto.getMotivoDesligamento());
        }
        if (dto.getDataDesligamento() != null) {
            discente.setDataDesligamento(dto.getDataDesligamento());
        }
        if (dto.getDestinoEgresso() != null) {
            discente.setDestinoEgresso(dto.getDestinoEgresso());
        }
        if (dto.getAtuacaoPosFormatura() != null) {
            discente.setAtuacaoPosFormatura(dto.getAtuacaoPosFormatura());
        }
    }

    /**
     * Converte entidade para ResponseDTO
     */
    public DiscenteResponseDTO toResponseDTO(Discente discente) {
        return DiscenteResponseDTO.builder()
                .id(discente.getId())
                .usuarioId(discente.getUsuario().getId())
                .usuarioNome(discente.getUsuario().getNomeCompleto())
                .usuarioEmail(discente.getUsuario().getEmail())
                .programaId(discente.getPrograma().getId())
                .programaNome(discente.getPrograma().getNome())
                .programaSigla(discente.getPrograma().getSigla())
                .linhaPesquisaId(discente.getLinhaPesquisa() != null ? discente.getLinhaPesquisa().getId() : null)
                .linhaPesquisaNome(discente.getLinhaPesquisa() != null ? discente.getLinhaPesquisa().getNome() : null)
                .orientadorId(discente.getOrientador() != null ? discente.getOrientador().getId() : null)
                .orientadorNome(discente.getOrientador() != null ? discente.getOrientador().getUsuario().getNomeCompleto() : null)
                .coorientadorInternoId(discente.getCoorientadorInterno() != null ? discente.getCoorientadorInterno().getId() : null)
                .coorientadorInternoNome(discente.getCoorientadorInterno() != null ? discente.getCoorientadorInterno().getUsuario().getNomeCompleto() : null)
                .numeroMatricula(discente.getNumeroMatricula())
                .tipoCurso(discente.getTipoCurso())
                .turma(discente.getTurma())
                .semestreIngresso(discente.getSemestreIngresso())
                .dataIngresso(discente.getDataIngresso())
                .dataPrimeiraMatricula(discente.getDataPrimeiraMatricula())
                .tituloProjeto(discente.getTituloProjeto())
                .resumoProjeto(discente.getResumoProjeto())
                .palavrasChaveProjeto(discente.getPalavrasChaveProjeto())
                .areaCnpq(discente.getAreaCnpq())
                .tipoIngresso(discente.getTipoIngresso())
                .notaProcessoSeletivo(discente.getNotaProcessoSeletivo())
                .classificacaoProcesso(discente.getClassificacaoProcesso())
                .coorientadorExternoNome(discente.getCoorientadorExternoNome())
                .coorientadorExternoInstituicao(discente.getCoorientadorExternoInstituicao())
                .coorientadorExternoEmail(discente.getCoorientadorExternoEmail())
                .coorientadorExternoTitulacao(discente.getCoorientadorExternoTitulacao())
                .proficienciaIdioma(discente.getProficienciaIdioma())
                .proficienciaStatus(discente.getProficienciaStatus())
                .dataProficiencia(discente.getDataProficiencia())
                .arquivoProficiencia(discente.getArquivoProficiencia())
                .bolsista(discente.getBolsista())
                .tipoBolsa(discente.getTipoBolsa())
                .modalidadeBolsa(discente.getModalidadeBolsa())
                .valorBolsa(discente.getValorBolsa())
                .dataInicioBolsa(discente.getDataInicioBolsa())
                .dataFimBolsa(discente.getDataFimBolsa())
                .numeroProcessoBolsa(discente.getNumeroProcessoBolsa())
                .agenciaFomento(discente.getAgenciaFomento())
                .creditosNecessarios(discente.getCreditosNecessarios())
                .coeficienteRendimento(discente.getCoeficienteRendimento())
                .qualificacaoRealizada(discente.getQualificacaoRealizada())
                .dataQualificacao(discente.getDataQualificacao())
                .resultadoQualificacao(discente.getResultadoQualificacao())
                .prazoOriginal(discente.getPrazoOriginal())
                .prorrogacoes(discente.getProrrogacoes())
                .dataLimiteAtual(discente.getDataLimiteAtual())
                .dataDefesa(discente.getDataDefesa())
                .resultadoDefesa(discente.getResultadoDefesa())
                .notaDefesa(discente.getNotaDefesa())
                .tituloFinal(discente.getTituloFinal())
                .documentos(discente.getDocumentos())
                .status(discente.getStatus())
                .motivoDesligamento(discente.getMotivoDesligamento())
                .dataDesligamento(discente.getDataDesligamento())
                .destinoEgresso(discente.getDestinoEgresso())
                .atuacaoPosFormatura(discente.getAtuacaoPosFormatura())
                .createdAt(discente.getCreatedAt())
                .updatedAt(discente.getUpdatedAt())
                // Campos calculados
                .isAtivo(discente.isAtivo())
                .podeDefender(discente.podeDefender())
                .temOrientador(discente.temOrientador())
                .temCoorientador(discente.temCoorientador())
                .temBolsaVigente(discente.temBolsaVigente())
                .totalProrrogacoes(discente.getTotalProrrogacoes())
                .isPrazoVencido(discente.isPrazoVencido())
                .mesesAtePrazo(discente.getMesesAtePrazo())
                .build();
    }
}
