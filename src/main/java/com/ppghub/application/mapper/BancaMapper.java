package com.ppghub.application.mapper;

import com.ppghub.application.dto.request.BancaCreateRequest;
import com.ppghub.application.dto.request.BancaUpdateRequest;
import com.ppghub.application.dto.response.BancaResponse;
import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import com.ppghub.infrastructure.persistence.entity.DiscenteEntity;
import com.ppghub.infrastructure.persistence.entity.ProgramaEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct Mapper para conversão entre BancaEntity e DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {MembroBancaMapper.class}
)
public interface BancaMapper {

    /**
     * Converte CreateRequest para Entity
     */
    @Mapping(target = "discente", source = "discenteId", qualifiedByName = "idToDiscente")
    @Mapping(target = "programa", source = "programaId", qualifiedByName = "idToPrograma")
    @Mapping(target = "tipoBanca", source = "tipoBanca", qualifiedByName = "stringToTipoBanca")
    @Mapping(target = "statusBanca", source = "statusBanca", qualifiedByName = "stringToStatusBanca")
    @Mapping(target = "membros", ignore = true) // Membros serão adicionados separadamente
    BancaEntity toEntity(BancaCreateRequest request);

    /**
     * Converte Entity para Response
     */
    @Mapping(target = "discenteId", source = "discente.id")
    @Mapping(target = "discenteNome", source = "discente.nome")
    @Mapping(target = "discenteMatricula", source = "discente.matricula")
    @Mapping(target = "programaId", source = "programa.id")
    @Mapping(target = "programaNome", source = "programa.nome")
    @Mapping(target = "programaSigla", source = "programa.sigla")
    @Mapping(target = "tipoBanca", source = "tipoBanca", qualifiedByName = "tipoBancaToString")
    @Mapping(target = "statusBanca", source = "statusBanca", qualifiedByName = "statusBancaToString")
    @Mapping(target = "resultadoBanca", source = "resultadoBanca", qualifiedByName = "resultadoBancaToString")
    @Mapping(target = "numeroMembrosTitulares", expression = "java(entity.getNumeroMembrosTitulares())")
    @Mapping(target = "numeroMembrosExternos", expression = "java(entity.getNumeroMembrosExternos())")
    BancaResponse toResponse(BancaEntity entity);

    /**
     * Converte lista de Entities para lista de Responses
     */
    List<BancaResponse> toResponseList(List<BancaEntity> entities);

    /**
     * Atualiza Entity existente com dados do Request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "discente", ignore = true) // Não atualizar discente
    @Mapping(target = "programa", ignore = true) // Não atualizar programa
    @Mapping(target = "membros", ignore = true) // Membros gerenciados separadamente
    @Mapping(target = "statusBanca", source = "statusBanca", qualifiedByName = "stringToStatusBanca")
    @Mapping(target = "resultadoBanca", source = "resultadoBanca", qualifiedByName = "stringToResultadoBanca")
    void updateEntityFromRequest(BancaUpdateRequest request, @MappingTarget BancaEntity entity);

    /**
     * Mapeia ID para DiscenteEntity
     */
    @Named("idToDiscente")
    default DiscenteEntity idToDiscente(Long discenteId) {
        if (discenteId == null) {
            return null;
        }
        DiscenteEntity discente = new DiscenteEntity();
        discente.setId(discenteId);
        return discente;
    }

    /**
     * Mapeia ID para ProgramaEntity
     */
    @Named("idToPrograma")
    default ProgramaEntity idToPrograma(Long programaId) {
        if (programaId == null) {
            return null;
        }
        ProgramaEntity programa = new ProgramaEntity();
        programa.setId(programaId);
        return programa;
    }

    /**
     * Converte String para Enum TipoBanca
     */
    @Named("stringToTipoBanca")
    default BancaEntity.TipoBanca stringToTipoBanca(String tipo) {
        if (tipo == null) {
            return null;
        }
        return BancaEntity.TipoBanca.valueOf(tipo);
    }

    /**
     * Converte Enum TipoBanca para String
     */
    @Named("tipoBancaToString")
    default String tipoBancaToString(BancaEntity.TipoBanca tipo) {
        if (tipo == null) {
            return null;
        }
        return tipo.name();
    }

    /**
     * Converte String para Enum StatusBanca
     */
    @Named("stringToStatusBanca")
    default BancaEntity.StatusBanca stringToStatusBanca(String status) {
        if (status == null) {
            return null;
        }
        return BancaEntity.StatusBanca.valueOf(status);
    }

    /**
     * Converte Enum StatusBanca para String
     */
    @Named("statusBancaToString")
    default String statusBancaToString(BancaEntity.StatusBanca status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    /**
     * Converte String para Enum ResultadoBanca
     */
    @Named("stringToResultadoBanca")
    default BancaEntity.ResultadoBanca stringToResultadoBanca(String resultado) {
        if (resultado == null) {
            return null;
        }
        return BancaEntity.ResultadoBanca.valueOf(resultado);
    }

    /**
     * Converte Enum ResultadoBanca para String
     */
    @Named("resultadoBancaToString")
    default String resultadoBancaToString(BancaEntity.ResultadoBanca resultado) {
        if (resultado == null) {
            return null;
        }
        return resultado.name();
    }
}
